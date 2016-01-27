package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.web.component.ShowMode;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 22.01.2016 13:09.
 */
@Stateless
public class OrganizationSyncHandler implements IAddressSyncHandler{
    @EJB
    private AddressSyncAdapter addressSyncAdapter;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private AddressSyncBean addressSyncBean;

    @Override
    public Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return addressSyncAdapter.getOrganizationSyncs(date);
    }

    @Override
    public List<? extends DomainObject> getObjects(DomainObject parent) {
        return organizationStrategy.getList(new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return null;
    }

    @Override
    public boolean isEqualNames(AddressSync sync, DomainObject object) {
        return sync.getName().equals(object.getStringValue(IOrganizationStrategy.NAME).toUpperCase());
    }

    @Override
    public Long getParentId(AddressSync sync, DomainObject parent) {
        return null;
    }

    @Override
    public void insert(AddressSync sync) {
        DomainObject domainObject = organizationStrategy.newInstance();

        domainObject.setExternalId(sync.getExternalId());
        domainObject.setStringValue(IOrganizationStrategy.NAME, sync.getName());
        domainObject.setStringValue(IOrganizationStrategy.CODE, sync.getExternalId());

        organizationStrategy.insert(domainObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void update(AddressSync sync) {
        DomainObject oldObject = organizationStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setExternalId(sync.getExternalId());
        newObject.setStringValue(DistrictStrategy.NAME, sync.getName());
        newObject.setStringValue(DistrictStrategy.CODE, sync.getExternalId());

        organizationStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(AddressSync sync) {
        organizationStrategy.archive(organizationStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return object.getStringValue(IOrganizationStrategy.NAME);
    }
}
