package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return cityStrategy.getList(new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
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

    public void insert(AddressSync sync){
        DomainObject domainObject = districtStrategy.newInstance();

        domainObject.setExternalId(sync.getExternalId());
        domainObject.setParentId(sync.getParentId());
        domainObject.setStringValue(DistrictStrategy.NAME, sync.getName());
        domainObject.setStringValue(DistrictStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(DistrictStrategy.CODE, sync.getExternalId());

        districtStrategy.insert(domainObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    public void update(AddressSync sync){
        DomainObject oldObject = districtStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setExternalId(sync.getExternalId());
        newObject.setStringValue(DistrictStrategy.NAME, sync.getName());
        newObject.setStringValue(DistrictStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());
        newObject.setStringValue(DistrictStrategy.CODE, sync.getExternalId());

        districtStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    public void archive(AddressSync sync){
        districtStrategy.archive(districtStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());

        addressSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return districtStrategy.getName(object);
    }
}
