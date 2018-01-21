package org.complitex.sync.handler;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.street.StreetStrategy;
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
 *         Date: 03.08.2014 6:46
 */
@Stateless
public class StreetSyncHandler implements IDomainSyncHandler {

    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private CorrectionBean correctionBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        List<DomainSync> cityTypeDomainSyncs = domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.DISTRICT,
                Long.valueOf(parentDomainSync.getAdditionalParentId()))));

        if (cityTypeDomainSyncs.isEmpty()){
            throw new CorrectionNotFoundException("city type correction not found " + cityTypeDomainSyncs);
        }

        return domainSyncAdapter.getStreetSyncs(parentDomainSync.getName(),
                cityTypeDomainSyncs.get(0).getAdditionalName(), date);
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return domainSyncBean.getList(FilterWrapper.of(new DomainSync(SyncEntity.CITY, SYNCHRONIZED)));
    }

    public Long getAdditionalParentObjectId(DomainSync domainSync, Long organizationId){
        List<Correction> streetTypeCorrections = correctionBean.getCorrectionsByExternalId(AddressEntity.STREET_TYPE,
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

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
        return Objects.equals(domainObject.getValueId(StreetStrategy.STREET_TYPE), getAdditionalParentObjectId(domainSync, organizationId)) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(StreetStrategy.NAME)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(StreetStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        //todo city
        return correction.getAdditionalParentId().equals(getAdditionalParentObjectId(domainSync, organizationId)) &&
                StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return correction1.getParentId().equals(correction2.getParentId()) &&
                correction1.getAdditionalParentId().equals(correction2.getAdditionalParentId()) &&
                StringUtil.isEqualIgnoreCase(correction2.getCorrection(), correction2.getCorrection());
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
                        .addAttribute(StreetStrategy.NAME, domainSync.getName())
                        .addAttribute(StreetStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
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
        correction.setAdditionalParentId(getAdditionalParentObjectId(domainSync, organizationId));
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
