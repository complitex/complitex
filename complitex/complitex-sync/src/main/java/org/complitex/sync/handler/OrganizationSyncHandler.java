package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;
import org.complitex.sync.service.IDomainSyncHandler;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.common.strategy.organization.IOrganizationStrategy.ORGANIZATION_TYPE;

/**
 * @author inheaven on 22.01.2016 13:09.
 */
@Stateless
public class OrganizationSyncHandler implements IDomainSyncHandler {
    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private DomainSyncBean domainSyncBean;

    @Override
    public Cursor<DomainSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return domainSyncAdapter.getOrganizationSyncs(date);
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
    public boolean isEqualNames(DomainSync sync, DomainObject object) {
        return sync.getName().equals(object.getStringValue(IOrganizationStrategy.NAME).toUpperCase());
    }

    @Override
    public Long getParentId(DomainSync sync, DomainObject parent) {
        return null;
    }

    @Override
    public void insert(DomainSync sync) {
        DomainObject domainObject = organizationStrategy.newInstance();

        domainObject.setExternalId(sync.getExternalId());
        domainObject.setStringValue(IOrganizationStrategy.NAME, sync.getName());
        domainObject.setStringValue(IOrganizationStrategy.CODE, sync.getExternalId());

        Attribute servicingOrganization = new Attribute();
        servicingOrganization.setAttributeId(1L);
        servicingOrganization.setEntityAttributeId(ORGANIZATION_TYPE);
        servicingOrganization.setValueId( OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE);
        domainObject.addAttribute(servicingOrganization);

        organizationStrategy.insert(domainObject, sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public void update(DomainSync sync) {
        DomainObject oldObject = organizationStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setExternalId(sync.getExternalId());
        newObject.setStringValue(IOrganizationStrategy.NAME, sync.getName());
        newObject.setStringValue(IOrganizationStrategy.CODE, sync.getExternalId());

        organizationStrategy.update(oldObject, newObject, sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(DomainSync sync) {
        organizationStrategy.archive(organizationStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return object.getStringValue(IOrganizationStrategy.NAME);
    }
}
