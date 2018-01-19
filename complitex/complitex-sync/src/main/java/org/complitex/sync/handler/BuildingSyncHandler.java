package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.BuildingCorrection;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.DistrictCorrection;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.SyncEntity;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.complitex.common.util.StringUtil.isEqualIgnoreCase;
import static org.complitex.sync.entity.DomainSyncStatus.SYNCHRONIZED;

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
    private BuildingStrategy buildingStrategy;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private ModuleBean moduleBean;

    @EJB
    private DomainSyncBean domainSyncBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return domainSyncAdapter.getBuildingSyncs(parentDomainSync.getName(), "", "", date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.DISTRICT, SYNCHRONIZED)));
    }

    @Override
    public List<? extends Correction> getCorrections(Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getBuildingCorrections(externalId, objectId, organizationId);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        if (streetCorrections.size() > 1){
            throw new RuntimeException("street correction size > 1 " + domainSync);
        }

        return Objects.equals(domainObject.getParentId(), streetCorrections.get(0).getObjectId()) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(BuildingStrategy.NUMBER)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(BuildingStrategy.NUMBER, Locales.getAlternativeLocale())) &&
                isEqualIgnoreCase(domainSync.getAdditionalName(), domainObject.getStringValue(BuildingStrategy.CORP)) &&
                isEqualIgnoreCase(domainSync.getAltAdditionalName(), domainObject.getStringValue(BuildingStrategy.CORP, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        return ((BuildingCorrection)correction).getStreetId().equals(streetCorrections.get(0).getObjectId()) &&
                correction.getCorrection().equals(domainSync.getName()) &&
                ((BuildingCorrection) correction).getCorrectionCorp().equals(domainSync.getAdditionalName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        BuildingCorrection c1 = (BuildingCorrection) correction1;
        BuildingCorrection c2 = (BuildingCorrection) correction2;

        return c1.getStreetId().equals(c2.getStreetId()) &&
                c1.getCorrection().equals(c2.getCorrection()) &&
                c1.getCorrectionCorp().equals(c2.getCorrectionCorp());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        return buildingStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("street")
                        .setParentId(streetCorrections.get(0).getObjectId())
                        .addAttribute(BuildingStrategy.NUMBER, domainSync.getName())
                        .addAttribute(BuildingStrategy.NUMBER, domainSync.getAltName(), Locales.getAlternativeLocaleId())
                        .addAttribute(BuildingStrategy.CORP, domainSync.getAdditionalName())
                        .addAttribute(BuildingStrategy.CORP, domainSync.getAltAdditionalName(), Locales.getAlternativeLocaleId()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        BuildingCorrection buildingCorrection = new BuildingCorrection(streetCorrections.get(0).getObjectId(),
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), StringUtil.valueOf(domainSync.getAdditionalName()),
                organizationId, null, moduleBean.getModuleId());

        addressCorrectionBean.insert(buildingCorrection);

        return buildingCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        ((BuildingCorrection)correction).setStreetId(streetCorrections.get(0).getObjectId());
        correction.setCorrection(domainSync.getName());
        ((BuildingCorrection) correction).setCorrectionCorp(domainSync.getAdditionalName());

        addressCorrectionBean.update((BuildingCorrection) correction);
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
        domainObject.setValueId(BuildingStrategy.DISTRICT, districtCorrections.get(0).getObjectId());
        domainObject.setStringValue(BuildingStrategy.NUMBER, domainSync.getName());
        domainObject.setStringValue(BuildingStrategy.NUMBER, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(BuildingStrategy.CORP, domainSync.getAdditionalName());
        domainObject.setStringValue(BuildingStrategy.CORP, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
    }

    @Override
    public Long getParentObjectId(DomainObject parentDomainObject, DomainSync domainSync, Long organizationId) {
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null, null,
                domainSync.getParentId(), null, null, organizationId, null);

        if (streetCorrections.isEmpty()){
            throw new RuntimeException("street correction not found " + domainSync);
        }

        return streetCorrections.get(0).getObjectId();
    }
}
