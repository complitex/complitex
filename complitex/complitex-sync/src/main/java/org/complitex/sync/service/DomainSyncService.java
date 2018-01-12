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
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.SyncBeginMessage;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.sync.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.complitex.sync.entity.DomainSyncStatus.LOADED;
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
    public void load(SyncEntity syncEntity, Map<String, DomainObject> map){
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

            List<? extends DomainObject> parents = getHandler(syncEntity).getParentObjects(map);

            if (parents != null){
                for (DomainObject p : parents) {
                    load(syncEntity, p, map, date);
                }
            }else{
                load(syncEntity, null, map, date);
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

    private void load(SyncEntity syncEntity, DomainObject parent, Map<String, DomainObject> map, Date date)
            throws RemoteCallException{
        if (cancelSync.get()){
            return;
        }

        domainSyncBean.delete(syncEntity);

        Cursor<DomainSync> cursor = getHandler(syncEntity).getAddressSyncs(parent, date);

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

        cursor.getData().forEach(s -> {
            s.setStatus(LOADED);
            s.setType(syncEntity);
            s.setDate(date);

            domainSyncBean.save(s);
        });
    }

    public void sync(Long parentId, SyncEntity syncEntity){
        processing.set(true);
        cancelSync.set(false);

        broadcastService.broadcast(getClass(), "info","Начата синхронизация");
        log.info("sync: begin");

        getHandler(syncEntity).sync(parentId);

        broadcastService.broadcast(getClass(), "info", "Синхронизация завершена успешно");
        log.info("sync: completed");

        processing.set(false);
    }


    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean getProcessing(){
        return processing.get();
    }





}