package org.complitex.sync.handler;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.country.CountryStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.Status;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 17:19
 */
@Stateless
public class CountrySyncHandler implements IDomainSyncHandler{
    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private CountryStrategy countryStrategy;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return domainSyncAdapter.getCountrySyncs(date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return StringUtil.isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(CountryStrategy.NAME)) &&
                StringUtil.isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(CountryStrategy.NAME, Locales.getAlternativeLocale()));
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
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return countryStrategy.getList(new DomainObjectFilter()
                .setStatus(Status.ACTIVE.name())
                .setNullValue(true)
                .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                .addAttribute(CountryStrategy.NAME, domainSync.getName())
                .addAttribute(CountryStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction correction = new Correction(AddressEntity.COUNTRY.getEntityName(), domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getName(), organizationId, null);

        correctionBean.save(correction);

        return correction;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return countryStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setStringValue(CountryStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(CountryStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
