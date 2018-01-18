package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.OrganizationCorrection;
import org.complitex.correction.service.OrganizationCorrectionBean;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @EJB
    private OrganizationCorrectionBean organizationCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return domainSyncAdapter.getOrganizationSyncs(date);
    }

    @Override
    public List<? extends DomainObject> getParentObjects() {
        return null;
    }

    @Override
    public List<? extends Correction> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId) {
        return organizationCorrectionBean.getOrganizationCorrections(externalId, objectId, organizationId);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return Objects.equals(domainSync.getName(), domainObject.getStringValue(IOrganizationStrategy.NAME)) &&
                Objects.equals(domainSync.getAltName(), domainObject.getStringValue(IOrganizationStrategy.NAME, Locales.getAlternativeLocale())) &&
                Objects.equals(domainSync.getAdditionalName(), domainObject.getStringValue(IOrganizationStrategy.SHORT_NAME)) &&
                Objects.equals(domainSync.getAltAdditionalName(), domainObject.getStringValue(IOrganizationStrategy.SHORT_NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return correction.getCorrection().equals(domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return correction1.getCorrection().equals(correction2.getCorrection());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return organizationStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .addAttribute(IOrganizationStrategy.NAME, domainSync.getName())
                        .addAttribute(IOrganizationStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId())
                        .addAttribute(IOrganizationStrategy.SHORT_NAME, domainSync.getAdditionalName())
                        .addAttribute(IOrganizationStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        OrganizationCorrection organizationCorrection = new OrganizationCorrection(domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getName(), organizationId, null, moduleBean.getModuleId());

        organizationCorrectionBean.save(organizationCorrection);

        return organizationCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        organizationCorrectionBean.save((OrganizationCorrection) correction);
    }

    @Override
    public IStrategy getStrategy() {
        return organizationStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setStringValue(IOrganizationStrategy.EDRPOU, domainSync.getAdditionalParentId() + "");
        domainObject.setStringValue(IOrganizationStrategy.CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(IOrganizationStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(IOrganizationStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(IOrganizationStrategy.SHORT_NAME, domainSync.getAdditionalName());
        domainObject.setStringValue(IOrganizationStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
        domainObject.setValueId(ORGANIZATION_TYPE, OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE);
    }

    @Override
    public Long getParentObjectId(DomainObject parentDomainObject, DomainSync domainSync, Long organizationId) {
        return null;
    }
}
