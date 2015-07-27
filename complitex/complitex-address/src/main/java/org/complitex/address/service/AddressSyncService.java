package org.complitex.address.service;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncStatus;
import org.complitex.address.entity.SyncBeginMessage;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.complitex.address.service.IAddressSyncHandler.NOT_FOUND_ID;

/**
 * @author Anatoly Ivanov
 *         Date: 016 16.07.14 20:29
 */
@Singleton
@ConcurrencyManagement(BEAN)
public class AddressSyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB(beanName = "DistrictSyncHandler")
    private IAddressSyncHandler districtSyncHandler;

    @EJB(beanName = "StreetTypeSyncHandler")
    private IAddressSyncHandler streetTypeSyncHandler;

    @EJB(beanName = "StreetSyncHandler")
    private IAddressSyncHandler streetSyncHandler;

    @EJB(beanName = "BuildingSyncHandler")
    private IAddressSyncHandler buildingSyncHandler;

    @EJB
    private BroadcastService broadcastService;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    private AtomicBoolean lockSync = new AtomicBoolean(false);

    private AtomicBoolean cancelSync = new AtomicBoolean(false);

    @Asynchronous
    public void syncAll(){
        sync(AddressEntity.DISTRICT);
        sync(AddressEntity.STREET_TYPE);
        sync(AddressEntity.STREET);
        sync(AddressEntity.BUILDING);
    }

    private IAddressSyncHandler getHandler(AddressEntity type){
        switch (type){
            case DISTRICT:
                return districtSyncHandler;
            case STREET_TYPE:
                return streetTypeSyncHandler;
            case STREET:
                return streetSyncHandler;
            case BUILDING:
                return buildingSyncHandler;

            default:
                throw new IllegalArgumentException();
        }
    }

    public void insert(AddressSync sync, Locale locale){
        getHandler(sync.getType()).insert(sync, locale);
    }

    public void update(AddressSync sync, Locale locale){
        getHandler(sync.getType()).update(sync, locale);
    }

    public void archive(AddressSync sync){
        getHandler(sync.getType()).archive(sync);
    }

    public void sync(AddressEntity type){
        if (lockSync.get()){
            return;
        }

        try {
            //lock sync
            lockSync.set(true);
            cancelSync.set(false);

            List<? extends DomainObject> parents = getHandler(type).getParentObjects();

            if (parents != null){
                for (DomainObject parent : parents) {
                    sync(parent, type);

                    if (cancelSync.get()){
                        break;
                    }
                }
            }else{
                sync(null, type);
            }
        } catch (Exception e) {
            log.error("Ошибка синхронизации", e);

            String message = ExceptionUtil.getCauseMessage(e, true);

            broadcastService.broadcast(getClass(), "error", message != null ? message : e.getMessage());
        } finally {
            //unlock sync
            lockSync.set(false);

            broadcastService.broadcast(getClass(), "done", type.name());
        }
    }

    public void sync(DomainObject parent,  AddressEntity type) throws RemoteCallException {
        IAddressSyncHandler handler = getHandler(type);

        Date date = DateUtil.getCurrentDate();

        Cursor<AddressSync> cursor = handler.getAddressSyncs(parent, date);

        SyncBeginMessage begin = new SyncBeginMessage();
        begin.setType(type.getEntityName());
        begin.setCount(cursor.getData() != null ? cursor.getData().size() : 0L);

        if (parent != null){
            if (type.equals(AddressEntity.DISTRICT) || type.equals(AddressEntity.STREET)){
                begin.setParentName(cityStrategy.getName(parent));
            }else if (type.equals(AddressEntity.BUILDING)){
                begin.setParentName(districtStrategy.getName(parent));
            }
        }

        broadcastService.broadcast(getClass(), "begin", begin);

        if (cursor.getData() == null) {
            return;
        }

        List<? extends DomainObject> objects = handler.getObjects(parent);

        for (AddressSync sync : cursor.getData()) {
            Long parentId = handler.getParentId(sync, parent);

            if (NOT_FOUND_ID.equals(parentId)){
                continue;
            }

            sync.setParentObjectId(parentId);
            sync.setType(type);

            for (DomainObject object : objects) {
                //все норм
                if (sync.getExternalId().equals(object.getExternalId()) && handler.isEqualNames(sync, object)) {
                    sync.setObjectId(object.getObjectId());
                    sync.setStatus(AddressSyncStatus.LOCAL);

                    break;
                }

                //новое название
                if (sync.getExternalId().equals(object.getExternalId())) {
                    sync.setObjectId(object.getObjectId());
                    sync.setStatus(AddressSyncStatus.NEW_NAME);

                    if (addressSyncBean.isExist(sync)) {
                        sync.setDate(date);
                        addressSyncBean.save(sync);
                    }

                    break;
                }

                //дубликат
                if (handler.isEqualNames(sync, object)) {
                    sync.setObjectId(object.getObjectId());
                    sync.setStatus(AddressSyncStatus.DUPLICATE);

                    if (addressSyncBean.isExist(sync)) {
                        sync.setDate(date);
                        addressSyncBean.save(sync);
                    }

                    break;
                }
            }

            //новый
            if (sync.getStatus() == null) {
                sync.setStatus(AddressSyncStatus.NEW);

                if (addressSyncBean.isExist(sync)) {
                    sync.setDate(date);
                    addressSyncBean.save(sync);
                }
            }

            broadcastService.broadcast(getClass(), "processed", sync);
        }

        for (DomainObject object : objects) {
            if (object.getExternalId() == null) {
                continue;
            }

            boolean archive = true;

            for (AddressSync sync : cursor.getData()) {
                if (sync.getExternalId().equals(object.getExternalId()) || handler.isEqualNames(sync, object)) {

                    archive = false;

                    break;
                }
            }

            //архив
            if (archive) {
                AddressSync s = new AddressSync();

                if (parent != null){
                    s.setParentObjectId(parent.getObjectId());
                }

                s.setObjectId(object.getObjectId());
                s.setExternalId(object.getExternalId());
                s.setName("");
                s.setType(type);
                s.setStatus(AddressSyncStatus.ARCHIVAL);

                if (addressSyncBean.isExist(s)) {
                    s.setDate(date);

                    addressSyncBean.save(s);
                }

                broadcastService.broadcast(getClass(), "processed", s);
            }
        }
    }

    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean isLockSync(){
        return lockSync.get();
    }
}