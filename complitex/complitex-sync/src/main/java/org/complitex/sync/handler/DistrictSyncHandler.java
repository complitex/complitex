package org.complitex.sync.handler;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.district.DistrictStrategy;
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
 * Date: 17.07.2014 23:34
 */
@Stateless
public class DistrictSyncHandler implements IDomainSyncHandler {
    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private DomainSyncBean domainSyncBean;

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.CITY, SYNCHRONIZED)));
    }

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        List<DomainSync> cityTypeDomainSyncs = domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.CITY_TYPE,
                Long.valueOf(parentDomainSync.getAdditionalParentId()))));

        if (cityTypeDomainSyncs.isEmpty()){
            throw new CorrectionNotFoundException("city type correction not found " + cityTypeDomainSyncs);
        }

        return addressSyncAdapter.getDistrictSyncs(cityTypeDomainSyncs.get(0).getAdditionalName(),
                parentDomainSync.getName(), date);
    }

    private Long getParentObjectId(DomainSync domainSync, Long organizationId){
        List<Correction> cityCorrections = correctionBean.getCorrectionsByExternalId(AddressEntity.CITY,
                domainSync.getParentId(), organizationId, null);

        if (cityCorrections.isEmpty()){
            throw new CorrectionNotFoundException("city correction not found " + cityCorrections);
        }

        return cityCorrections.get(0).getObjectId();
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return Objects.equals(domainObject.getParentId(), getParentObjectId(domainSync, organizationId)) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(DistrictStrategy.NAME)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(DistrictStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return Objects.equals(correction.getParentId(), getParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return correction1.getParentId().equals(correction2.getParentId()) &&
                StringUtil.isEqualIgnoreCase(correction1.getCorrection(), correction2.getCorrection());
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setParentEntityId(DistrictStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(getParentObjectId(domainSync, organizationId));
        domainObject.setStringValue(DistrictStrategy.CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(DistrictStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(DistrictStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return districtStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setNullValue(true)
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("city")
                        .setParentId(getParentObjectId(domainSync, organizationId))
                        .addAttribute(DistrictStrategy.NAME, domainSync.getName()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction districtCorrection = new Correction(AddressEntity.DISTRICT.getEntityName(), domainObject.getParentId(),
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), organizationId, null);

        correctionBean.save(districtCorrection);

        return districtCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return districtStrategy;
    }
}
