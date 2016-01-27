package org.complitex.address.service;

import org.complitex.address.entity.*;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.ShowMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static org.complitex.address.entity.SyncEntity.*;
import static org.complitex.address.entity.AddressSyncStatus.*;
import static org.complitex.address.service.IAddressSyncHandler.NOT_FOUND_ID;

/**
 * @author Anatoly Ivanov
 *         Date: 016 16.07.14 20:29
 */
@Singleton
@ConcurrencyManagement(BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class AddressSyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB
    private BroadcastService broadcastService;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @Resource
    private UserTransaction tx;

    private AtomicBoolean lockSync = new AtomicBoolean(false);

    private AtomicBoolean cancelSync = new AtomicBoolean(false);

    @Asynchronous
    public void syncAll(){
        sync(DISTRICT);
        sync(STREET_TYPE);
        sync(STREET);
        sync(BUILDING);
    }

    private IAddressSyncHandler getHandler(SyncEntity type){
        switch (type){
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

    public void insert(AddressSync sync){
        getHandler(sync.getType()).insert(sync);
    }

    public void update(AddressSync sync){
        getHandler(sync.getType()).update(sync);
    }

    public void archive(AddressSync sync){
        getHandler(sync.getType()).archive(sync);
    }

    @Asynchronous
    public void sync(SyncEntity type){
        sync(type, null);
    }

    @Asynchronous
    public void sync(SyncEntity type, Map<String, DomainObject> map){
        if (lockSync.get()){
            return;
        }

        try {
            //lock sync
            lockSync.set(true);
            cancelSync.set(false);

            Date date = DateUtil.getCurrentDate();

            List<? extends DomainObject> parents = getHandler(type).getParentObjects(map);

            if (parents != null){
                for (DomainObject p : parents) {
                    sync(p, type, map, date);

                    if (cancelSync.get()){
                        break;
                    }
                }
            }else{
                sync(null, type, map, date);
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

    public void sync(DomainObject parent, SyncEntity syncEntity, Map<String, DomainObject> map, Date date)
            throws RemoteCallException {
        IAddressSyncHandler handler = getHandler(syncEntity);

        Cursor<AddressSync> cursor = handler.getAddressSyncs(parent, date);

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

        if (cursor.getData() == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<DomainObject> objects = (List<DomainObject>) handler.getObjects(parent);

        Map<String, DomainObject> objectMap = objects.parallelStream()
                .filter(o -> o.getExternalId() != null)
                .collect(Collectors.toMap(DomainObject::getExternalId, o -> o));

        //коды улиц
        if (syncEntity.equals(STREET)){
            objects.parallelStream().forEach(o -> o.getAttributes().parallelStream()
                    .filter(a -> a.getAttributeTypeId() == StreetStrategy.STREET_CODE)
                    .forEach(a -> objectMap.put(String.valueOf(a.getValueId()), o)));
        }

        cursor.getData().parallelStream().forEach(sync -> {
                    try {
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

                        //uppercase
                        if (sync.getName() != null){
                            sync.setName(sync.getName().toUpperCase());
                        }

                        if (sync.getAdditionalName() != null){
                            sync.setAdditionalName(sync.getAdditionalName().toUpperCase());
                        }

                        if (sync.getAltName() != null){
                            sync.setAltName(sync.getAltName().toUpperCase());
                        }

                        if (sync.getAltAdditionalName() != null){
                            sync.setAltAdditionalName(sync.getAltAdditionalName().toUpperCase());
                        }

                        sync.setParentId(parentId);
                        sync.setType(syncEntity);
                        sync.setDate(date);

                        if (BUILDING.equals(syncEntity) && map.get("district") != null){
                            sync.setAdditionalParentId(map.get("district").getId());
                        }

                        String uniqueExternalId = sync.getUniqueExternalId();

                        DomainObject object = objectMap.get(uniqueExternalId);

                        if (object != null){
                            sync.setObjectId(object.getObjectId());

                            //все норм
                            if (handler.isEqualNames(sync, object)) {
                                sync.setStatus(LOCAL);
                            }else
                                //новое название
                                sync.setStatus(AddressSyncStatus.NEW_NAME);
                        }else{
                            //дубликат
                            objects.parallelStream().filter(o -> !Objects.equals(uniqueExternalId, o.getExternalId()) &&
                                    handler.hasEqualNames(sync, o))
                                    .findAny()
                                    .ifPresent(o -> {
                                        sync.setObjectId(o.getObjectId());
                                        sync.setStatus(AddressSyncStatus.DUPLICATE);
                                    });

                            //внешний дубликат
                            if (sync.getStatus() == null) {
                                cursor.getData().parallelStream()
                                        .filter(s -> s.getStatus() != null &&
                                                !Objects.equals(sync.getExternalId(), s.getExternalId()) &&
                                                Objects.equals(sync.getAdditionalExternalId(), s.getAdditionalExternalId()) &&
                                                Objects.equals(sync.getName(), s.getName()) &&
                                                Objects.equals(sync.getAdditionalName(), s.getAdditionalName()) &&
                                                Objects.equals(sync.getAltName(), s.getAltName()) &&
                                                Objects.equals(sync.getAltAdditionalName(), s.getAltAdditionalName()))
                                        .findAny()
                                        .ifPresent(s -> {
                                            sync.setStatus(AddressSyncStatus.EXTERNAL_DUPLICATE);
                                            log.warn("Внешний дубликат: {} {}", sync, s);
                                        });
                            }
                        }

                        //новый
                        if (sync.getStatus() == null) {
                            sync.setStatus(AddressSyncStatus.NEW);
                        }

                        //сохранение
                        tx.begin();
                        if (!sync.getStatus().equals(LOCAL) && !addressSyncBean.isExist(sync)) {
                            addressSyncBean.save(sync);
                        }
                        tx.commit();

                        broadcastService.broadcast(getClass(), "processed", sync);
                    } catch (Exception e) {
                        broadcastService.broadcast(getClass(), "error", "Ошибка синхронизации: " + ExceptionUtil.getCauseMessage(e));
                        log.error("ошибка синхронизации", e);

                        try {
                            tx.rollback();
                        } catch (SystemException e1) {
                            log.error("rollback error", e1);
                        }
                    }
                }
        );

        if (!BUILDING.equals(syncEntity)) { //todo fix building object list
            Map<String, AddressSync> syncMap = cursor.getData().parallelStream()
                    .filter(a -> a.getExternalId() != null)
                    .collect(Collectors.toMap(AddressSync::getUniqueExternalId, a -> a));

            objects.parallelStream().filter(o -> o.getExternalId() != null).forEach(object -> {
                AddressSync addressSync = syncMap.get(object.getExternalId());

                //архив
                if (addressSync == null) {
                    AddressSync s = new AddressSync();

                    if (parent != null){
                        s.setParentId(parent.getObjectId());
                    }

                    s.setObjectId(object.getObjectId());
                    s.setName(handler.getName(object));
                    s.setType(syncEntity);
                    s.setUniqueExternalId(object.getExternalId());
                    s.setStatus(AddressSyncStatus.ARCHIVAL);
                    s.setDate(date);

                    if (!addressSyncBean.isExist(s)) {
                        addressSyncBean.save(s);
                    }

                    broadcastService.broadcast(getClass(), "processed", s);
                }
            });
        }
    }

    public void cancelSync(){
        cancelSync.set(true);
    }

    public boolean isLockSync(){
        return lockSync.get();
    }

    @Asynchronous
    public void addAll(Long parentObjectId, SyncEntity syncEntity){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(syncEntity);
        addressSync.setStatus(AddressSyncStatus.NEW);

        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream()
                .filter(s -> parentObjectId == null || parentObjectId.equals(s.getParentId()))
                .forEach(s -> {
                    if (!cancelSync.get()) {
                        try {
                            s.setType(syncEntity);
                            insert(s);
                            broadcastService.broadcast(getClass(), "add_all", s);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            broadcastService.broadcast(getClass(), "error", e.getMessage());
                        }
                    }
                });

        addressSync.setStatus(AddressSyncStatus.EXTERNAL_DUPLICATE);
        List<AddressSync> externalDuplicate = addressSyncBean.getList(FilterWrapper.of(addressSync));

        //street code duplicate
        if (syncEntity.equals(STREET)){
            externalDuplicate.stream().forEach(s -> {
                DomainObjectFilter filter = new DomainObjectFilter();
                filter.setParent("city", s.getParentId());
                filter.addAttribute(StreetStrategy.NAME, s.getName());
                filter.setStatus(ShowMode.ACTIVE.name());

                streetStrategy.getList(filter).stream()
                        .filter(o -> getHandler(STREET).hasEqualNames(s, o))
                        .findAny()
                        .ifPresent(o -> {
                            s.setObjectId(o.getObjectId());
                            s.setStatus(DUPLICATE);
                            insert(s);
                        });
            });
        }

        externalDuplicate.stream().forEach(s -> addressSyncBean.delete(s.getId()));

        lockSync.set(false);

        broadcastService.broadcast(getClass(), "add_all_complete", syncEntity);
    }

    @Asynchronous
    public void updateAll(Long parentObjectId, SyncEntity syncEntity){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(syncEntity);

        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream()
                .filter(s -> parentObjectId == null || parentObjectId.equals(s.getParentId()))
                .filter(s -> s.getStatus().equals(AddressSyncStatus.NEW_NAME) ||
                        s.getStatus().equals(AddressSyncStatus.DUPLICATE))
                .forEach(a -> {
                    if (!cancelSync.get()) {
                        try {
                            update(a);

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

        addressSyncBean.getList(FilterWrapper.of(addressSync)).stream()
                .filter(s -> s.getStatus().equals(EXTERNAL_DUPLICATE))
                .forEach(s -> addressSyncBean.delete(s.getId()));

        lockSync.set(false);

        broadcastService.broadcast(getClass(), "add_all_complete", syncEntity);
    }

    @Asynchronous
    public void deleteAll(Long parentObjectId, SyncEntity syncEntity){
        lockSync.set(true);
        cancelSync.set(false);

        AddressSync addressSync = new AddressSync();
        addressSync.setType(syncEntity);
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

        broadcastService.broadcast(getClass(), "add_all_complete", syncEntity);
    }
}