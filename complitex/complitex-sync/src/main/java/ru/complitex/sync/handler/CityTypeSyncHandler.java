package ru.complitex.sync.handler;

import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.address.strategy.city_type.CityTypeStrategy;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.service.DomainSyncAdapter;
import ru.complitex.sync.service.DomainSyncBean;

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
                        .setNullValue(true)
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .addAttribute(CityTypeStrategy.NAME, domainSync.getName()));
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
