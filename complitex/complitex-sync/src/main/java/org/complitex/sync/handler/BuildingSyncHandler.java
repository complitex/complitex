package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.BuildingCorrection;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.DistrictCorrection;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:47
 */
@Stateless
public class BuildingSyncHandler implements IDomainSyncHandler {
    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        switch (parent.getEntityName()){
            case "district":
                return domainSyncAdapter.getBuildingSyncs(districtStrategy.getName(parent), "", "", date);
            case "street":
                return domainSyncAdapter.getBuildingSyncs("",
                        streetStrategy.getStreetTypeShortName(parent),
                        streetStrategy.getName(parent), date);
        }

        throw new IllegalArgumentException("parent entity name not district or street");
    }

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        if (map.containsKey("street") && map.get("street").getId() > 0){
            return Collections.singletonList(map.get("street"));
        }else if (map.containsKey("district") && map.get("district").getId() > 0){
            return Collections.singletonList(map.get("district"));
        }else {
            return districtStrategy.getList(new DomainObjectFilter());
        }
    }

    @Override
    public List<? extends Correction> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getBuildingCorrections(parentObjectId, externalId, objectId, organizationId);
    }

    @Override
    public void update(Correction correction) {
        addressCorrectionBean.update((BuildingCorrection) correction);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        return Objects.equals(domainObject.getParentId(), streetCorrections.get(0).getObjectId()) &&
                Objects.equals(domainSync.getName(), domainObject.getStringValue(BuildingStrategy.NUMBER)) &&
                Objects.equals(domainSync.getAltName(), domainObject.getStringValue(BuildingStrategy.NUMBER, Locales.getAlternativeLocale())) &&
                Objects.equals(domainSync.getAdditionalName(), domainObject.getStringValue(BuildingStrategy.CORP)) &&
                Objects.equals(domainSync.getAltAdditionalName(), domainObject.getStringValue(BuildingStrategy.CORP, Locales.getAlternativeLocale()));
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync) {
        return buildingStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("street")
                        .setParentId(domainSync.getParentObjectId())
                        .addAttribute(BuildingStrategy.NUMBER, domainSync.getName())
                        .addAttribute(BuildingStrategy.NUMBER, domainSync.getAltName(), Locales.getAlternativeLocaleId())
                        .addAttribute(BuildingStrategy.CORP, domainSync.getName())
                        .addAttribute(BuildingStrategy.CORP, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        List<DistrictCorrection> districtCorrections = addressCorrectionBean.getDistrictCorrections(null,
                domainSync.getAdditionalParentId(), null, null, organizationId, null);

        if (districtCorrections.isEmpty()){
            throw new RuntimeException("district correction not found " + domainSync);
        }

        BuildingCorrection buildingCorrection = new BuildingCorrection(streetCorrections.get(0).getObjectId(),
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), domainSync.getAdditionalName(),
                organizationId, null, moduleBean.getModuleId());

        addressCorrectionBean.insert(buildingCorrection);

        return buildingCorrection;
    }

    @Override
    public IStrategy getStrategy() {
        return buildingStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        List<DistrictCorrection> districtCorrections = addressCorrectionBean.getDistrictCorrections(null,
                domainSync.getAdditionalParentId(), null, null, organizationId, null);

        if (districtCorrections.isEmpty()){
            throw new RuntimeException("district correction not found " + domainSync);
        }

        domainObject.setParentId(streetCorrections.get(0).getObjectId());
//        domainObject.setValueId(); todo district
        domainObject.setStringValue(BuildingStrategy.NUMBER, domainSync.getName());
        domainObject.setStringValue(BuildingStrategy.NUMBER, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(BuildingStrategy.CORP, domainSync.getAdditionalName());
        domainObject.setStringValue(BuildingStrategy.CORP, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
    }
}
