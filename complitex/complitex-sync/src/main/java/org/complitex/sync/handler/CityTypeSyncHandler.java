package org.complitex.sync.handler;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 17:18
 */
@Stateless
public class CityTypeSyncHandler implements IDomainSyncHandler{
    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return addressSyncAdapter.getCityTypeSyncs(date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return StringUtil.isEqualIgnoreCase(domainObject.getStringValue(CityTypeStrategy.NAME), domainSync.getName()) &&
                StringUtil.isEqualIgnoreCase(domainObject.getStringValue(CityTypeStrategy.NAME, Locales.getAlternativeLocale()), domainSync.getAltName());
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
        return cityTypeStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .addAttribute(DistrictStrategy.NAME, domainSync.getName())
                        .addAttribute(DistrictStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction cityTypeCorrection = new Correction(AddressEntity.CITY_TYPE.getEntityName(), null,
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), organizationId, null);

        correctionBean.save(cityTypeCorrection);

        return cityTypeCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return cityTypeStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setStringValue(CityTypeStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(CityTypeStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
