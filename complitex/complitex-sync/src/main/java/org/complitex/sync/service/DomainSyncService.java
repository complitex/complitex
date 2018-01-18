package org.complitex.sync.service;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.DomainSyncStatus;
import org.complitex.sync.entity.SyncBeginMessage;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.sync.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.complitex.common.entity.FilterWrapper.of;
import static org.complitex.sync.entity.DomainSyncStatus.*;
import static org.complitex.sync.entity.SyncEntity.*;

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
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CorrectionBean correctionBean;

    private AtomicBoolean processing = new AtomicBoolean(false);

    private AtomicBoolean cancelSync = new AtomicBoolean(false);


    private IDomainSyncHandler getHandler(SyncEntity syncEntity){
        switch (syncEntity){
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

            List<? extends DomainObject> parents = getHandler(syncEntity).getParentObjects();

            if (parents != null){
                for (DomainObject p : parents) {
                    load(syncEntity, p, date);
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

    private void load(SyncEntity syncEntity, DomainObject parent, Date date)
            throws RemoteCallException{
        if (cancelSync.get()){
            return;
        }

        Long organizationId = addressSyncAdapter.getOrganization().getObjectId();

        Cursor<DomainSync> cursor = getHandler(syncEntity).getCursorDomainSyncs(parent, date);

        SyncBeginMessage message = new SyncBeginMessage();
        message.setSyncEntity(syncEntity);
        message.setCount(cursor.getData() != null ? cursor.getData().size() : 0L);

        if (parent != null){
            if (syncEntity.equals(DISTRICT) || syncEntity.equals(STREET)){
                message.setParentName(cityStrategy.getName(parent));
            }else if (syncEntity.equals(BUILDING)){
                switch (parent.getEntityName()){
                    case "district":
                        message.setParentName(districtStrategy.getName(parent));
                        break;
                    case "street":
                        message.setParentName(streetStrategy.getName(parent));
                        break;
                }
            }
        }

        broadcastService.broadcast(getClass(), "begin", message);

        if (cursor.getData() != null) {
            cursor.getData().forEach(s -> {
                if (parent != null) {
                    s.setParentObjectId(getHandler(syncEntity).getParentObjectId(parent, s, organizationId));
                }

                s.setStatus(LOADED);
                s.setType(syncEntity);
                s.setDate(date);

                domainSyncBean.insert(s);

                broadcastService.broadcast(getClass(), "processed", s);
            });
        }
    }

    private List<DomainSync> getDomainSyncs(SyncEntity syncEntity, DomainSyncStatus syncStatus, Long externalId) {
        return domainSyncBean.getList(of(new DomainSync(syncEntity, syncStatus, null, externalId)));
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
            getDomainSyncs(syncEntity, null, null).forEach(ds -> {
                if (cancelSync.get()){
                    return;
                }

                List<? extends Correction> corrections = handler.getCorrections(ds.getParentObjectId(), ds.getExternalId(),
                        null, organizationId);

                if (!corrections.isEmpty()){
                    if (corrections.size() > 1){
                        log.warn("sync: corrections > 1 {}", corrections); //todo
                    }

                    Correction correction = corrections.get(0);

                    if (!handler.isCorresponds(correction, ds, organizationId)){
                        handler.updateCorrection(correction, ds, organizationId);

                        log.info("sync: update correction name {}", correction);
                    }

                    DomainObject domainObject = handler.getStrategy().getDomainObject(correction.getObjectId());

                    try {
                        if (handler.isCorresponds(domainObject, ds, organizationId)){
                            ds.setStatus(SYNCHRONIZED);
                            domainSyncBean.updateStatus(ds);
                        }else{
                            List<? extends Correction> objectCorrections = handler.getCorrections(null, null,
                                    domainObject.getObjectId(),  organizationId);

                            if (objectCorrections.size() == 1 && objectCorrections.get(0).getId().equals(correction.getId())){
                                handler.updateValues(domainObject, ds, organizationId);
                                handler.getStrategy().update(domainObject);

                                log.info("sync: update domain object {}", domainObject);
                            }else {
                                ds.setStatus(DEFERRED);
                                domainSyncBean.updateStatus(ds);
                            }
                        }
                    } catch (Exception e) {
                        ds.setStatus(ERROR);
                        domainSyncBean.updateStatus(ds);
                    }
                }else {
                    List<? extends DomainObject> domainObjects = handler.getDomainObjects(ds, organizationId);

                    DomainObject domainObject;

                    if (!domainObjects.isEmpty()){
                        domainObject = domainObjects.get(0);

                        for (int i = 1; i < domainObjects.size(); ++i){
                            handler.getStrategy().disable(domainObjects.get(i));

                            log.info("sync: disable domain object {}", domainObjects.get(i));
                        }
                    }else{
                        domainObject = handler.getStrategy().newInstance();
                        handler.updateValues(domainObject, ds, organizationId);
                        handler.getStrategy().insert(domainObject, ds.getDate());

                        log.info("sync: add domain object {}", domainObject);
                    }

                    Correction correction = handler.insertCorrection(domainObject, ds, organizationId);

                    log.info("sync: add correction {}", correction);

                    ds.setStatus(SYNCHRONIZED);
                    domainSyncBean.updateStatus(ds);
                }

                broadcastService.broadcast(getClass(), "processed", ds);
            });

            //clear
            handler.getCorrections(null, null, null, organizationId).forEach(c -> {
                if (getDomainSyncs(syncEntity, null, c.getExternalId()).isEmpty()){
                    correctionBean.delete(c);

                    log.info("sync: delete correction {}", c);
                }
            });

            //deferred
            getDomainSyncs(syncEntity, DEFERRED, null).forEach(ds -> {
                List<? extends Correction> corrections = handler.getCorrections(ds.getParentObjectId(),
                        ds.getExternalId(), null, organizationId);

                if (corrections.isEmpty()){
                    throw new RuntimeException("sync: deferred correction nod found " + ds);
                }

                Correction correction = corrections.get(0);

                List<? extends Correction> objectCorrections = handler.getCorrections(null, null,
                        correction.getObjectId(), organizationId);

                boolean corresponds = objectCorrections.stream()
                        .allMatch(c -> c.getId().equals(correction.getId()) || handler.isCorresponds(c, correction));

                if (corresponds){
                    DomainObject domainObject = handler.getStrategy().getDomainObject(correction.getObjectId());

                    handler.updateValues(domainObject, ds, organizationId);
                    handler.getStrategy().update(domainObject);

                    log.info("sync: update deferred domain object {}", domainObject);

                    ds.setStatus(SYNCHRONIZED);
                    domainSyncBean.updateStatus(ds);
                }else{
                    List<? extends DomainObject> domainObjects = handler.getDomainObjects(ds, organizationId);

                    if (!domainObjects.isEmpty()){
                        correction.setObjectId(domainObjects.get(0).getObjectId());

                        correctionBean.save(correction);

                        log.info("sync: update deferred correction {}", correction);
                    }else{
                        DomainObject domainObject = handler.getStrategy().newInstance();
                        handler.updateValues(domainObject, ds, organizationId);
                        handler.getStrategy().insert(domainObject, ds.getDate());

                        log.info("sync: add deferred domain object {}", domainObject);
                    }

                    ds.setStatus(SYNCHRONIZED);
                    domainSyncBean.updateStatus(ds);
                }
            });

            broadcastService.broadcast(getClass(), "info", "Синхронизация завершена успешно");
            log.info("sync: completed");
        } catch (Exception e) {
            log.error("sync: error", e);

            broadcastService.broadcast(getClass(), "error", ExceptionUtil.getCauseMessage(e));
        } finally {
            processing.set(false);
        }

    }


    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean getProcessing(){
        return processing.get();
    }





}