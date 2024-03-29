package ru.complitex.sync.service;

import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Status;
import ru.complitex.common.service.BroadcastService;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.ExceptionUtil;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.DomainSyncStatus;
import ru.complitex.sync.entity.SyncBeginMessage;
import ru.complitex.sync.entity.SyncEntity;
import ru.complitex.sync.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static ru.complitex.common.entity.FilterWrapper.of;
import static ru.complitex.sync.entity.DomainSyncStatus.*;

/**
 * @author Anatoly Ivanov
 *         Date: 016 16.07.14 20:29
 */
@Singleton
@ConcurrencyManagement(BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class DomainSyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private BroadcastService broadcastService;

    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CorrectionBean correctionBean;

    private AtomicBoolean processing = new AtomicBoolean(false);

    private AtomicBoolean cancelSync = new AtomicBoolean(false);


    private IDomainSyncHandler getHandler(SyncEntity syncEntity){
        switch (syncEntity){
            case COUNTRY:
                return EjbBeanLocator.getBean(CountrySyncHandler.class);
            case REGION:
                return EjbBeanLocator.getBean(RegionSyncHandler.class);
            case CITY_TYPE:
                return EjbBeanLocator.getBean(CityTypeSyncHandler.class);
            case CITY:
                return EjbBeanLocator.getBean(CitySyncHandler.class);
            case DISTRICT:
                return EjbBeanLocator.getBean(DistrictSyncHandler.class);
            case STREET_TYPE:
                return EjbBeanLocator.getBean(StreetTypeSyncHandler.class);
            case STREET:
                return EjbBeanLocator.getBean(StreetSyncHandler.class);
            case BUILDING:
                return EjbBeanLocator.getBean(BuildingSyncHandler.class);
            case ORGANIZATION:
                return EjbBeanLocator.getBean(OrganizationSyncHandler.class);

            default:
                throw new IllegalArgumentException();
        }
    }


    @Asynchronous
    public void load(SyncEntity syncEntity){
        if (processing.get()){
            return;
        }

        try {
            //lock sync
            processing.set(true);
            cancelSync.set(false);

            //clear
            domainSyncBean.delete(syncEntity);

            Date date = DateUtil.getCurrentDate();

            List<DomainSync> parentDomainSyncs = getHandler(syncEntity).getParentDomainSyncs();

            if (parentDomainSyncs != null){
                for (DomainSync sync : parentDomainSyncs) {
                    load(syncEntity, sync, date);
                }
            }else{
                load(syncEntity, null, date);
            }
        } catch (Exception e) {
            log.error("Ошибка синхронизации", e);

            String message = ExceptionUtil.getCauseMessage(e, true);

            broadcastService.broadcast(getClass(), "error", message != null ? message : e.getMessage());
        } finally {
            //unlock sync
            processing.set(false);

            broadcastService.broadcast(getClass(), "done", syncEntity.name());
        }
    }

    private void load(SyncEntity syncEntity, DomainSync parentDomainSync, Date date)
            throws RemoteCallException{
        if (cancelSync.get()){
            return;
        }

        Cursor<DomainSync> cursor = getHandler(syncEntity).getCursorDomainSyncs(parentDomainSync, date);

        SyncBeginMessage message = new SyncBeginMessage();

        message.setSyncEntity(syncEntity);
        message.setCount(cursor.getData() != null ? cursor.getData().size() : 0L);

        if (parentDomainSync != null){
            message.setParentName(parentDomainSync.getName());
        }

        broadcastService.broadcast(getClass(), "begin", message);

        if (cursor.getData() != null) {
            cursor.getData().forEach(s -> {
                s.setStatus(LOADED);
                s.setType(syncEntity);
                s.setDate(date);

                domainSyncBean.insert(s);

                broadcastService.broadcast(getClass(), "processed", s);
            });
        }
    }

    private List<DomainSync> getDomainSyncs(SyncEntity syncEntity, DomainSyncStatus syncStatus, Long externalId) {
        List<DomainSync> domainSyncs = domainSyncBean.getList(of(new DomainSync(syncEntity, syncStatus, externalId)));

        if (syncEntity.equals(SyncEntity.ORGANIZATION)) {
            Map<Long, DomainSync> map = domainSyncs.stream().collect(Collectors.toMap(DomainSync::getExternalId, sync -> sync));

            Map<Long, Integer> levelMap = new HashMap<>();

            domainSyncs.forEach(sync -> {
                int level = 0;

                DomainSync p = sync;

                while (p != null && p.getParentId() != null && level < 100){
                    p = map.get(p.getParentId());

                    level++;
                }

                levelMap.put(sync.getExternalId(), level);
            });

            domainSyncs.sort(Comparator.comparingInt(sync -> levelMap.get(sync.getExternalId())));
        }

        return domainSyncs;
    }

    @Asynchronous
    public void sync(SyncEntity syncEntity){
        processing.set(true);
        cancelSync.set(false);

        try {
            broadcastService.broadcast(getClass(), "info", "Начата синхронизация");
            log.info("sync: begin");

            Long organizationId = addressSyncAdapter.getOrganization().getObjectId();

            IDomainSyncHandler handler = getHandler(syncEntity);

            //sync
            getDomainSyncs(syncEntity, null, null).forEach(sync -> {
                try {
                    if (cancelSync.get()){
                        return;
                    }

                    List<? extends Correction> corrections = correctionBean.getCorrectionsByExternalId(syncEntity,
                            sync.getExternalId(), organizationId, null);

                    if (!corrections.isEmpty()){
                        if (corrections.size() > 1){
                            log.warn("sync: corrections > 1 {}", corrections); //todo
                        }

                        Correction correction = corrections.get(0);

                        if (!handler.isCorresponds(correction, sync, organizationId)){
                            handler.updateCorrection(correction, sync, organizationId);

                            log.info("sync: update correction name {}", correction);
                        }

                        DomainObject domainObject = handler.getStrategy().getDomainObject(correction.getObjectId());

                        if (handler.isCorresponds(domainObject, sync, organizationId)){
                            if (!domainObject.getStatus().equals(Status.ACTIVE)){
                                handler.getStrategy().enable(domainObject);

                                log.info("sync: enable domain object {}", correction);
                            }

                            sync.setStatus(SYNCHRONIZED);
                            domainSyncBean.updateStatus(sync);
                        }else{
                            List<? extends Correction> objectCorrections = correctionBean.getCorrectionsByObjectId(syncEntity,
                                    domainObject.getObjectId(),  organizationId, null);

                            if (objectCorrections.size() == 1 && objectCorrections.get(0).getId().equals(correction.getId())){
                                handler.updateValues(domainObject, sync, organizationId);
                                handler.getStrategy().update(domainObject);

                                log.info("sync: update domain object {}", domainObject);

                                sync.setStatus(SYNCHRONIZED);
                                domainSyncBean.updateStatus(sync);
                            }else {
                                sync.setStatus(DEFERRED);
                                domainSyncBean.updateStatus(sync);
                            }
                        }

                        handler.getDomainObjects(sync, organizationId).stream()
                                .filter(o -> sync.getStatus().equals(SYNCHRONIZED))
                                .filter(o -> !o.getObjectId().equals(domainObject.getObjectId()))
                                .forEach(o -> {
                                    handler.getStrategy().disable(o);

                                    log.info("sync: disable duplicate domain object {}", o);
                                });
                    }else {
                        List<? extends DomainObject> domainObjects = handler.getDomainObjects(sync, organizationId);

                        DomainObject domainObject;

                        if (!domainObjects.isEmpty()){
                            domainObject = domainObjects.get(0);

                            for (int i = 1; i < domainObjects.size(); ++i){
                                handler.getStrategy().disable(domainObjects.get(i));

                                log.info("sync: disable domain object {}", domainObjects.get(i));
                            }
                        }else{
                            domainObject = handler.getStrategy().newInstance();
                            handler.updateValues(domainObject, sync, organizationId);
                            handler.getStrategy().insert(domainObject, sync.getDate());

                            log.info("sync: add domain object {}", domainObject);
                        }

                        Correction correction = handler.insertCorrection(domainObject, sync, organizationId);

                        log.info("sync: add correction {}", correction);

                        sync.setStatus(SYNCHRONIZED);
                        domainSyncBean.updateStatus(sync);
                    }
                } catch (CorrectionNotFoundException e) {
                    sync.setStatus(ERROR);
                    domainSyncBean.updateStatus(sync);

                    log.warn("sync: warn sync {}", e.getMessage());
                } catch (Exception e){
                    log.error("sync: error sync {}", sync);

                    throw e;
                }

                broadcastService.broadcast(getClass(), "processed", sync);
            });

            //clear
            correctionBean.getCorrections(syncEntity, null, organizationId, null).forEach(c -> {
                if (getDomainSyncs(syncEntity, null, c.getExternalId()).isEmpty()){
                    correctionBean.delete(c);

                    log.info("sync: delete correction {}", c);
                }
            });

            //deferred
            getDomainSyncs(syncEntity, DEFERRED, null).forEach(sync -> {
                if (domainSyncBean.getObject(sync.getId()).getStatus().equals(DEFERRED)) {
                    List<? extends Correction> corrections = correctionBean.getCorrectionsByExternalId(syncEntity,
                            sync.getExternalId(), organizationId, null);

                    if (corrections.isEmpty()){
                        throw new RuntimeException("sync: deferred correction nod found " + sync);
                    }

                    Correction correction = corrections.get(0);

                    List<? extends Correction> objectCorrections = correctionBean.getCorrectionsByObjectId(syncEntity,
                            correction.getObjectId(), organizationId, null);

                    boolean corresponds = objectCorrections.stream()
                            .allMatch(c -> c.getId().equals(correction.getId()) || handler.isCorresponds(c, correction));

                    if (corresponds){
                        DomainObject domainObject = handler.getStrategy().getDomainObject(correction.getObjectId());

                        handler.updateValues(domainObject, sync, organizationId);
                        handler.getStrategy().update(domainObject);

                        log.info("sync: update deferred domain object {}", domainObject);

                        objectCorrections.forEach(c -> {
                            if (!c.getId().equals(correction.getId())){
                                DomainSync domainSync = getDomainSyncs(syncEntity, null, c.getExternalId()).get(0);

                                domainSync.setStatus(SYNCHRONIZED);
                                domainSyncBean.updateStatus(domainSync);
                            }
                        });
                    }else{
                        List<? extends DomainObject> domainObjects = handler.getDomainObjects(sync, organizationId);

                        if (!domainObjects.isEmpty()){
                            correction.setObjectId(domainObjects.get(0).getObjectId());

                            correctionBean.save(correction);

                            log.info("sync: update deferred correction {}", correction);
                        }else{
                            DomainObject domainObject = handler.getStrategy().newInstance();
                            handler.updateValues(domainObject, sync, organizationId);
                            handler.getStrategy().insert(domainObject, sync.getDate());

                            log.info("sync: add deferred domain object {}", domainObject);
                        }
                    }

                    sync.setStatus(SYNCHRONIZED);
                    domainSyncBean.updateStatus(sync);
                }
            });

            broadcastService.broadcast(getClass(), "info", "Синхронизация завершена успешно");
            log.info("sync: completed");
        } catch (Exception e) {
            log.error("sync: error", e);

            broadcastService.broadcast(getClass(), "error", ExceptionUtil.getCauseMessage(e));
        } finally {
            processing.set(false);
            cancelSync.set(true);
        }
    }

    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean isProcessing(){
        return processing.get();
    }
}
