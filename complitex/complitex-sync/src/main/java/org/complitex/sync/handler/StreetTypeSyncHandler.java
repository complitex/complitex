package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.StreetTypeCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static org.complitex.common.util.StringUtil.isEqualIgnoreCase;

/**
 * @author Anatoly Ivanov
 *         Date: 23.07.2014 22:57
 */
@Stateless
public class StreetTypeSyncHandler implements IDomainSyncHandler {
    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return addressSyncAdapter.getStreetTypeSyncs(date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return isEqualIgnoreCase(domainSync.getName(), streetTypeStrategy.getName(domainObject)) //todo get
                && isEqualIgnoreCase(domainSync.getAdditionalName(), streetTypeStrategy.getShortName(domainObject))
                && isEqualIgnoreCase(domainSync.getAltName(), streetTypeStrategy.getName(domainObject, Locales.getAlternativeLocale()))
                && isEqualIgnoreCase(domainSync.getAltAdditionalName(), streetTypeStrategy.getShortName(domainObject, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return StringUtil.isEqualIgnoreCase(correction1.getCorrection(), correction2.getCorrection());
    }

    @Override
    public List<? extends Correction> getCorrections(Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getStreetTypeCorrections(externalId, objectId, organizationId);
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return streetTypeStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .addAttribute(StreetTypeStrategy.NAME, domainSync.getName())
                        .addAttribute(StreetTypeStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId())
                        .addAttribute(StreetTypeStrategy.SHORT_NAME, domainSync.getAdditionalName())
                        .addAttribute(StreetTypeStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        StreetTypeCorrection correction = new StreetTypeCorrection(domainSync.getExternalId(), domainObject.getObjectId(),
                domainSync.getName(), organizationId, null, moduleBean.getModuleId());

        addressCorrectionBean.insert(correction);

        return correction;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        addressCorrectionBean.save((StreetTypeCorrection) correction);
    }

    @Override
    public IStrategy getStrategy() {
        return streetTypeStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setStringValue(StreetTypeStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(StreetTypeStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, domainSync.getAdditionalName());
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
    }

    @Override
    public Long getParentObjectId(DomainObject parentDomainObject, DomainSync domainSync, Long organizationId) {
        return null;
    }
}
