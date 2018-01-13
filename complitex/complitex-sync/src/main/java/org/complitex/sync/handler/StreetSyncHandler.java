package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.entity.StreetTypeCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:46
 */
@Stateless
public class StreetSyncHandler implements IDomainSyncHandler {

    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return domainSyncAdapter.getStreetSyncs(cityStrategy.getName(parent),
                cityTypeStrategy.getShortName(parent.getAttribute(CityStrategy.CITY_TYPE).getValueId()),
                date);
    }

    public List<? extends DomainObject> getObjects(DomainObject parent) {
        return streetStrategy.getList(new DomainObjectFilter()
                .setParent("city", parent.getObjectId())
                .setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return cityStrategy.getList(new DomainObjectFilter());
    }

    @Override
    public List<? extends Correction> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getStreetCorrections(parentObjectId, null, externalId, objectId, null,
                organizationId, null);
    }

    @Override
    public void update(Correction correction) {
        addressCorrectionBean.save((StreetCorrection) correction);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
        }

        return Objects.equals(domainObject.getValueId(StreetStrategy.STREET_TYPE), streetTypeCorrections.get(0).getObjectId()) &&
                Objects.equals(domainSync.getName(), domainObject.getStringValue(DistrictStrategy.NAME)) &&
                Objects.equals(domainSync.getAltName(), domainObject.getStringValue(DistrictStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync) {
        return streetStrategy.getList(
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
        StreetCorrection streetCorrection = new StreetCorrection(domainObject.getParentId(),
                domainObject.getValueId(StreetStrategy.STREET_TYPE), domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getName(), organizationId, null, moduleBean.getModuleId());

        addressCorrectionBean.insert(streetCorrection);

        return streetCorrection;
    }

    @Override
    public IStrategy getStrategy() {
        return streetStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
        }

        domainObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(domainSync.getParentObjectId());
        domainObject.setValueId(StreetStrategy.STREET_TYPE, streetTypeCorrections.get(0).getObjectId());
        domainObject.setStringValue(StreetStrategy.STREET_CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
