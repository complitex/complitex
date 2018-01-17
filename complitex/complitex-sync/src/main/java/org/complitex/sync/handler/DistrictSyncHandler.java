package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.DistrictCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly Ivanov
 * Date: 17.07.2014 23:34
 */
@Stateless
public class DistrictSyncHandler implements IDomainSyncHandler {
    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @Override
    public List<? extends DomainObject> getParentObjects() {
        return cityStrategy.getList(new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return addressSyncAdapter.getDistrictSyncs(cityStrategy.getName(parent),
                cityTypeStrategy.getShortName(parent.getAttribute(CityStrategy.CITY_TYPE).getValueId()),
                date);
    }

    @Override
    public List<DistrictCorrection> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getDistrictCorrections(parentObjectId, externalId, null, null,
                organizationId,null);
    }

    @Override
    public void update(Correction correction) {
        addressCorrectionBean.save((DistrictCorrection) correction);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return Objects.equals(domainSync.getName(), domainObject.getStringValue(DistrictStrategy.NAME)) &&
                Objects.equals(domainSync.getAltName(), domainObject.getStringValue(DistrictStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setParentEntityId(DistrictStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(domainSync.getParentObjectId());
        domainObject.setStringValue(DistrictStrategy.CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(DistrictStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(DistrictStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return districtStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("city")
                        .setParentId(domainSync.getParentObjectId())
                        .addAttribute(DistrictStrategy.NAME, domainSync.getName())
                        .addAttribute(DistrictStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        DistrictCorrection districtCorrection = new DistrictCorrection(domainObject.getParentId(), domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getName(), organizationId, null, moduleBean.getModuleId());

        addressCorrectionBean.insert(districtCorrection);

        return districtCorrection;
    }

    @Override
    public IStrategy getStrategy() {
        return districtStrategy;
    }
}
