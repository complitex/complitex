package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.util.CloneUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.complitex.address.strategy.building_address.BuildingAddressStrategy.CORP;
import static org.complitex.address.strategy.building_address.BuildingAddressStrategy.NUMBER;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:47
 */
@Stateless
public class BuildingSyncHandler implements IAddressSyncHandler {
    @EJB
    private AddressSyncAdapter addressSyncAdapter;

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private BuildingAddressStrategy buildingAddressStrategy;

    @Override
    public Cursor<AddressSync> getAddressSyncs(final DomainObject parent, Date date) throws RemoteCallException {
            return addressSyncAdapter.getBuildingSyncs(districtStrategy.getName(parent), "", "", date);
    }

    @Override
    public List<? extends DomainObject> getObjects(DomainObject parent) {
        //.addAdditionalParam(DISTRICT_ID, parent.getObjectId())
        return buildingAddressStrategy.getList(new DomainObjectFilter());
    }

    @Override
    public List<? extends DomainObject> getParentObjects() {
        return districtStrategy.getList(new DomainObjectFilter());
    }

    @Override
    public boolean isEqualNames(AddressSync sync, DomainObject object) {
        String  streetExternalId = streetStrategy.getExternalId(object.getParentId());

        return streetExternalId != null &&
                sync.getName().equals(object.getStringValue(NUMBER)) &&
                Objects.equals(sync.getAdditionalName(), object.getStringValue(CORP)) &&
                Objects.equals(streetExternalId, sync.getAdditionalExternalId());
    }

    @Override
    public Long getParentId(AddressSync sync, DomainObject parent) {
        Long objectId = streetStrategy.getObjectId(sync.getAdditionalExternalId());

        return objectId != null ? objectId : NOT_FOUND_ID;
    }

    @Override
    public Long getAdditionalParentId(AddressSync sync, DomainObject parent) {
        return parent.getObjectId();
    }

    @Override
    public void insert(AddressSync sync, Locale locale) {
        Building building = buildingStrategy.newInstance();
        building.setLongValue(BuildingStrategy.DISTRICT, sync.getAdditionalParentId());

        DomainObject buildingAddress = building.getPrimaryAddress();

        buildingAddress.setExternalId(sync.getExternalId());
        buildingAddress.setParentEntityId(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID);
        buildingAddress.setParentId(sync.getParentId());

        //building number
        buildingAddress.setStringValue(NUMBER, sync.getName(), locale);

        //building part
        if (sync.getAdditionalName() != null) {
            buildingAddress.setStringValue(CORP, sync.getAdditionalName(), locale);
        }

        buildingStrategy.insert(building, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void update(AddressSync sync, Locale locale) {
        DomainObject oldObject = buildingAddressStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        //building number
        newObject.setStringValue(NUMBER, sync.getName(), locale);

        //building part
        if (sync.getAdditionalName() != null) {
            newObject.setStringValue(CORP, sync.getAdditionalName(), locale);
        }

        buildingStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(AddressSync sync) {
        buildingStrategy.archive(buildingStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        addressSyncBean.delete(sync.getId());
    }
}
