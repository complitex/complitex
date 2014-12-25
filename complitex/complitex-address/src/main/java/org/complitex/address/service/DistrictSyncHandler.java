package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.CloneUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly Ivanov
 * Date: 17.07.2014 23:34
 */
@Stateless
public class DistrictSyncHandler implements IAddressSyncHandler {
    @EJB
    private ConfigBean configBean;

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB
    private AddressSyncAdapter addressSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @Override
    public List<? extends DomainObject> getParentObjects() {
        return cityStrategy.getList(new DomainObjectFilter());
    }

    @Override
    public Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) {
        return addressSyncAdapter.getDistrictSyncs(cityStrategy.getName(parent),
                cityTypeStrategy.getShortName(parent.getAttribute(CityStrategy.CITY_TYPE).getValueId()),
                date);
    }

    @Override
    public List<? extends DomainObject> getObjects(DomainObject parent) {
        return districtStrategy.getList(new DomainObjectFilter().setParent("city", parent.getObjectId()));
    }

    @Override
    public boolean isEqualNames(AddressSync sync, DomainObject object) {
        return sync.getName().equals(districtStrategy.getName(object));
    }

    @Override
    public Long getParentId(AddressSync sync, DomainObject parent) {
        return parent.getObjectId();
    }

    public void insert(AddressSync sync, Locale locale){
        DomainObject domainObject = districtStrategy.newInstance();

        domainObject.setExternalId(sync.getExternalId());
        domainObject.setParentId(sync.getParentObjectId());
        domainObject.setStringValue(DistrictStrategy.NAME, sync.getName(), locale);
        domainObject.setStringValue(DistrictStrategy.CODE, sync.getExternalId());

        districtStrategy.insert(domainObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    public void update(AddressSync sync, Locale locale){
        DomainObject oldObject = districtStrategy.findById(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setExternalId(sync.getExternalId());
        newObject.setStringValue(DistrictStrategy.NAME, sync.getName(), locale);
        newObject.setStringValue(DistrictStrategy.CODE, sync.getExternalId());

        districtStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    public void archive(AddressSync sync){
        districtStrategy.archive(districtStrategy.findById(sync.getObjectId(), true), sync.getDate());

        addressSyncBean.delete(sync.getId());
    }
}
