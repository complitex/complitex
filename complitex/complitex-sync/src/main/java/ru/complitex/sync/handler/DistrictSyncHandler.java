package ru.complitex.sync.handler;

import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.SyncEntity;
import ru.complitex.sync.service.DomainSyncBean;
import ru.complitex.sync.service.DomainSyncJsonAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.complitex.common.util.StringUtil.isEqualIgnoreCase;
import static ru.complitex.sync.entity.DomainSyncStatus.SYNCHRONIZED;

/**
 * @author Anatoly Ivanov
 * Date: 17.07.2014 23:34
 */
@Stateless
public class DistrictSyncHandler extends DomainSyncHandler {
    @EJB
    private DomainSyncJsonAdapter addressSyncJsonAdapter;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private DomainSyncBean domainSyncBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync city, Date date) throws RemoteCallException {
        return addressSyncJsonAdapter.getDistrictSyncs(getCityDomainSyncParameter(city, date));
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.CITY, SYNCHRONIZED)));
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
