package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
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
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return domainSyncAdapter.getOrganizationSyncs(date);
    }

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return null;
    }

    public void insert(DomainSync sync) {
        DomainObject domainObject = organizationStrategy.newInstance();

//        domainObject.setExternalId(sync.getExternalId());
        domainObject.setStringValue(IOrganizationStrategy.NAME, sync.getName());
//        domainObject.setStringValue(IOrganizationStrategy.CODE, sync.getExternalId());

        Attribute servicingOrganization = new Attribute();
        servicingOrganization.setAttributeId(1L);
        servicingOrganization.setEntityAttributeId(ORGANIZATION_TYPE);
        servicingOrganization.setValueId( OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE);
        domainObject.addAttribute(servicingOrganization);

        organizationStrategy.insert(domainObject, sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public void sync(Long parentId) {

    }

}
