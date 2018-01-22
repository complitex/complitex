package org.complitex.sync.handler;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.DomainSyncStatus;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 17:17
 */
@Stateless
public class CitySyncHandler implements IDomainSyncHandler{
    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private CityStrategy cityStrategy;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return addressSyncAdapter.getCitySyncs(parentDomainSync.getName(), date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.REGION, DomainSyncStatus.SYNCHRONIZED)));
    }

    private Long getParentObjectId(DomainSync domainSync, Long organizationId){ //todo parent entity correction
        List<Correction> cityCorrections = correctionBean.getCorrectionsByExternalId(AddressEntity.REGION,
                domainSync.getParentId(), organizationId, null);

        if (cityCorrections.isEmpty()){
            throw new CorrectionNotFoundException("region correction not found " + domainSync);
        }

        return cityCorrections.get(0).getObjectId();
    }

    private Long getAdditionalParentObjectId(DomainSync domainSync, Long organizationId){
        List<Correction> cityTypeCorrections = correctionBean.getCorrectionsByExternalId(AddressEntity.CITY_TYPE,
                Long.valueOf(domainSync.getAdditionalParentId()), organizationId, null);

        if (cityTypeCorrections.isEmpty()){
            throw new CorrectionNotFoundException("city type correction not found " + domainSync);
        }

        return cityTypeCorrections.get(0).getObjectId();
    }

     @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return Objects.equals(domainObject.getParentId(), getParentObjectId(domainSync, organizationId)) &&
                Objects.equals(domainObject.getValueId(CityStrategy.CITY_TYPE), getAdditionalParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(domainObject.getStringValue(CityStrategy.NAME), domainSync.getName()) &&
                StringUtil.isEqualIgnoreCase(domainObject.getStringValue(CityStrategy.NAME, Locales.getAlternativeLocale()), domainSync.getAltName());
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return Objects.equals(correction.getParentId(), getParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return Objects.equals(correction1.getParentId(), correction2.getParentId()) &&
                StringUtil.isEqualIgnoreCase(correction1.getCorrection(), correction2.getCorrection());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return cityStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("region")
                        .setParentId(getParentObjectId(domainSync, organizationId))
                        .addAttribute(CityStrategy.CITY_TYPE, getAdditionalParentObjectId(domainSync, organizationId))
                        .addAttribute(CityStrategy.NAME, domainSync.getName())
                        .addAttribute(CityStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) { //todo update and insert
        Correction regionCorrection = new Correction(AddressEntity.CITY.getEntityName(), domainObject.getParentId(),
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), organizationId, null);

        correctionBean.save(regionCorrection);

        return regionCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setParentId(getParentObjectId(domainSync, organizationId));
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return cityStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setParentEntityId(CityStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(getParentObjectId(domainSync, organizationId));
        domainObject.setValueId(CityStrategy.CITY_TYPE, getAdditionalParentObjectId(domainSync, organizationId));
        domainObject.setStringValue(CityStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(CityStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
