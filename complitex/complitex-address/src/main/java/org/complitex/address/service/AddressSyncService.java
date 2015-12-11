package org.complitex.address.service;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncStatus;
import org.complitex.address.entity.SyncBeginMessage;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.complitex.address.entity.AddressSyncStatus.LOCAL;
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

    @Asynchronous
    public void sync(AddressEntity type){
        if (lockSync.get()){
            return;
        }

        try {
            //lock sync
            lockSync.set(true);
            cancelSync.set(false);

            Date date = DateUtil.getCurrentDate();

            List<? extends DomainObject> parents = getHandler(type).getParentObjects();

            if (parents != null){
                for (DomainObject parent : parents) {
                    sync(parent, type, date);

                    if (cancelSync.get()){
                        break;
                    }
                }
            }else{
                sync(null, type, date);
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

    public void sync(DomainObject parent,  AddressEntity type, Date date) throws RemoteCallException {
        IAddressSyncHandler handler = getHandler(type);

        Cursor<AddressSync> cursor = handler.getAddressSyncs(parent, date);

        SyncBeginMessage begin = new SyncBeginMessage();
        begin.setAddressEntity(type);
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

        @SuppressWarnings("unchecked")
        List<DomainObject> objects = (List<DomainObject>) handler.getObjects(parent);

        Map<String, DomainObject> objectMap = objects.parallelStream()
                .filter(o -> o.getExternalId() != null)
                .collect(Collectors.toMap(DomainObject::getExternalId, o -> o));

        cursor.getData().parallelStream().forEach(sync -> {
                    if (sync.getExternalId() == null){
                        broadcastService.broadcast(getClass(), "error", "Пустой вненший код: " + sync.toString());
                        return;
                    }

                    if (sync.getName() == null){
                        broadcastService.broadcast(getClass(), "error", "Пустое название: " + sync.toString());
                        return;
                    }

                    Long parentId = handler.getParentId(sync, parent);

                    if (NOT_FOUND_ID.equals(parentId)){
                        broadcastService.broadcast(getClass(), "error", "Родительский объект не найден: " + sync.toString());
                        return;
                    }

                    sync.setParentId(parentId);
                    sync.setAdditionalParentId(handler.getAdditionalParentId(sync, parent));
                    sync.setType(type);
                    sync.setDate(date);

                    DomainObject object = objectMap.get(sync.getExternalId());

                    if (object != null){
                        sync.setObjectId(object.getObjectId());

                        //все норм
                        if (handler.isEqualNames(sync, object)) {
                            sync.setStatus(LOCAL);
                        }else
                            //новое название
                            sync.setStatus(AddressSyncStatus.NEW_NAME);
                    }

                    //дубликат
                    objects.parallelStream().filter(o -> !Objects.equals(sync.getExternalId(), o.getExternalId()) &&
                            handler.hasEqualNames(sync, o))
                            .findAny()
                            .ifPresent(o -> {
                                sync.setObjectId(o.getObjectId());
                                sync.setStatus(AddressSyncStatus.DUPLICATE);
                            });

                    //внешний дубликат
                    cursor.getData().parallelStream()
                            .filter(s -> !Objects.equals(sync.getExternalId(), s.getExternalId()) &&
                                    Objects.equals(sync.getName(), s.getName()) &&
                                    Objects.equals(sync.getAdditionalName(), s.getAdditionalName()))
                            .findAny()
                            .ifPresent(s -> sync.setStatus(AddressSyncStatus.EXTERNAL_DUPLICATE));

                    //новый
                    if (sync.getStatus() == null) {
                        sync.setStatus(AddressSyncStatus.NEW);
                    }

                    //сохранение
                    if (!sync.getStatus().equals(LOCAL) && !addressSyncBean.isExist(sync)) {
                        addressSyncBean.save(sync);
                    }

                    broadcastService.broadcast(getClass(), "processed", sync);
                }
        );

        Map<String, AddressSync> syncMap = cursor.getData().parallelStream()
                .filter(a -> a.getExternalId() != null)
                .collect(Collectors.toMap(AddressSync::getExternalId, a -> a));

        objects.parallelStream().filter(o -> o.getExternalId() != null).forEach(object -> {
            AddressSync addressSync = syncMap.get(object.getExternalId());

            //архив
            if (addressSync == null) {
                AddressSync s = new AddressSync();

                if (parent != null){
                    s.setParentId(parent.getObjectId());
                }

                s.setObjectId(object.getObjectId());
                s.setExternalId(object.getExternalId());
                s.setName(handler.getName(object));
                s.setType(type);
                s.setStatus(AddressSyncStatus.ARCHIVAL);
                s.setDate(date);

                if (!addressSyncBean.isExist(s)) {
                    addressSyncBean.save(s);
                }

                broadcastService.broadcast(getClass(), "processed", s);
            }
        });
    }

    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean isLockSync(){
        return lockSync.get();
    }

    @Asynchronous
    public void addAll(Long parentObjectId, AddressEntity addressEntity, Locale locale){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(addressEntity);
        addressSync.setStatus(AddressSyncStatus.NEW);
        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream() //todo parallel mysql lock
                .filter(s -> parentObjectId == null || parentObjectId.equals(s.getParentId()))
                .forEach(a -> {
                    if (!cancelSync.get()) {
                        try {
                            insert(a, locale);
                            broadcastService.broadcast(getClass(), "add_all", a);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            broadcastService.broadcast(getClass(), "error", e.getMessage());
                        }
                    }
                });
        lockSync.set(false);

        broadcastService.broadcast(getClass(), "add_all_complete", addressEntity);
    }

    @Asynchronous
    public void updateAll(Long parentObjectId, AddressEntity addressEntity, Locale locale){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(addressEntity);
        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream()
                .filter(s -> parentObjectId == null || parentObjectId.equals(s.getParentId()))
                .filter(s -> s.getStatus().equals(AddressSyncStatus.NEW_NAME) ||
                        s.getStatus().equals(AddressSyncStatus.DUPLICATE))
                .forEach(a -> {
                    if (!cancelSync.get()) {
                        try {
                            update(a, locale);

                            String key = a.getStatus().equals(AddressSyncStatus.NEW_NAME)
                                    ? "update_new_name_all"
                                    : "update_duplicate_all";

                            broadcastService.broadcast(getClass(), key, a);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            broadcastService.broadcast(getClass(), "error", e.getMessage());
                        }
                    }
                });
        lockSync.set(false);

        broadcastService.broadcast(getClass(), "add_all_complete", addressEntity);
    }

    @Asynchronous
    public void deleteAll(Long parentObjectId, AddressEntity addressEntity){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(addressEntity);
        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream()
                .filter(s -> parentObjectId == null || parentObjectId.equals(s.getParentId()))
                .forEach(a -> {
                    if (!cancelSync.get()) {
                        try {
                            addressSyncBean.delete(a.getId());
                            broadcastService.broadcast(getClass(), "delete_all", a);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            broadcastService.broadcast(getClass(), "error", e.getMessage());
                        }
                    }
                });
        lockSync.set(false);

        broadcastService.broadcast(getClass(), "add_all_complete", addressEntity);
    }
}