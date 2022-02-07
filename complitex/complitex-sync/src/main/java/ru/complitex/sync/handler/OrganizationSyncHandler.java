package ru.complitex.sync.handler;

import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.ORGANIZATION_TYPE;
import static ru.complitex.organization.strategy.OrganizationStrategy.ORGANIZATION_ENTITY;

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
    private CorrectionBean correctionBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return domainSyncAdapter.getOrganizationSyncs(date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return Objects.equals(domainObject.getParentId(), getParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(IOrganizationStrategy.NAME)) &&
                StringUtil.isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(IOrganizationStrategy.NAME, Locales.getAlternativeLocale())) &&
                StringUtil.isEqualIgnoreCase(domainSync.getAdditionalName(), domainObject.getStringValue(IOrganizationStrategy.SHORT_NAME)) &&
                StringUtil.isEqualIgnoreCase(domainSync.getAltAdditionalName(), domainObject.getStringValue(IOrganizationStrategy.SHORT_NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return Objects.equals(correction.getCorrection(), domainSync.getAdditionalExternalId());
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
                        .setNullValue(true)
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("organization")
                        .setParentId(getParentObjectId(domainSync, organizationId))
                        .addAttribute(IOrganizationStrategy.NAME, domainSync.getName())
                        .addAttribute(IOrganizationStrategy.SHORT_NAME, domainSync.getAdditionalName()));
    }

    private Long getParentObjectId(DomainSync domainSync, Long organizationId) {
        if (domainSync.getParentId() != null) {
            List<Correction> organizationCorrections = correctionBean.getCorrectionsByExternalId(ORGANIZATION_ENTITY,
                    domainSync.getParentId(),  organizationId, null);

            if (organizationCorrections.isEmpty()) {
                throw new CorrectionNotFoundException("organization parent correction not found " + domainSync);
            }

            return organizationCorrections.get(0).getObjectId();
        }

        return null;
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction organizationCorrection = new Correction(ORGANIZATION_ENTITY.getEntityName(), domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getAdditionalExternalId(), organizationId, null);

        correctionBean.save(organizationCorrection);

        return organizationCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getAdditionalExternalId());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return organizationStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        if (domainSync.getParentId() != null) {
            domainObject.setParentEntityId(IOrganizationStrategy.ENTITY_ID);
            domainObject.setParentId(getParentObjectId(domainSync, organizationId));
        }

        domainObject.setStringValue(IOrganizationStrategy.EDRPOU, domainSync.getAdditionalParentId());
        domainObject.setStringValue(IOrganizationStrategy.CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(IOrganizationStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(IOrganizationStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(IOrganizationStrategy.SHORT_NAME, domainSync.getAdditionalName());
        domainObject.setStringValue(IOrganizationStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
        domainObject.setValueId(ORGANIZATION_TYPE, OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE);
    }
}
