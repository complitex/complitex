package ru.complitex.sync.handler;

import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.address.strategy.street.StreetStrategy;
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
 *         Date: 03.08.2014 6:46
 */
@Stateless
public class StreetSyncHandler extends DomainSyncHandler {
    @EJB
    private DomainSyncJsonAdapter domainSyncJsonAdapter;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private CorrectionBean correctionBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync city, Date date) throws RemoteCallException {
        return domainSyncJsonAdapter.getStreetSyncs(getCityDomainSyncParameter(city, date));
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.CITY, SYNCHRONIZED)));
    }

    public Long getAdditionalParentObjectId(DomainSync domainSync, Long organizationId){
        List<Correction> streetTypeCorrections = correctionBean.getCorrectionsByExternalId(AddressEntity.STREET_TYPE,
                Long.valueOf(domainSync.getAdditionalParentId()), organizationId, null);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        return streetTypeCorrections.get(0).getObjectId();
    }

    public Long getParentObjectId(DomainSync domainSync, Long organizationId){
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
                Objects.equals(domainObject.getValueId(StreetStrategy.STREET_TYPE), getAdditionalParentObjectId(domainSync, organizationId)) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(StreetStrategy.NAME)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(StreetStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {

        return Objects.equals(correction.getParentId(),getParentObjectId(domainSync, organizationId)) &&
                Objects.equals(correction.getAdditionalParentId(), getAdditionalParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return Objects.equals(correction1.getParentId(), correction2.getParentId()) &&
                Objects.equals(correction1.getAdditionalParentId(), correction2.getAdditionalParentId()) &&
                StringUtil.isEqualIgnoreCase(correction1.getCorrection(), correction2.getCorrection());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
           return streetStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("city")
                        .setParentId(getParentObjectId(domainSync, organizationId))
                        .addAttribute(StreetStrategy.STREET_TYPE, getAdditionalParentObjectId(domainSync, organizationId))
                        .addAttribute(StreetStrategy.NAME, domainSync.getName()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction streetCorrection = new Correction(AddressEntity.STREET.getEntityName(), domainObject.getParentId(),
                domainObject.getValueId(StreetStrategy.STREET_TYPE), domainSync.getExternalId(),
                domainObject.getObjectId(), domainSync.getName(), organizationId, null);

        correctionBean.save(streetCorrection);

        return streetCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setAdditionalParentId(getAdditionalParentObjectId(domainSync, organizationId)); //todo parentId
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return streetStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(getParentObjectId(domainSync, organizationId));
        domainObject.setValueId(StreetStrategy.STREET_TYPE, getAdditionalParentObjectId(domainSync, organizationId));
        domainObject.setStringValue(StreetStrategy.STREET_CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
