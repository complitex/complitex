package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
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
import org.complitex.correction.entity.CityCorrection;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.entity.StreetTypeCorrection;
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

    @EJB
    private DomainSyncBean domainSyncBean;

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

    @Override
    public List<? extends Correction> getCorrections(Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getStreetCorrections(null, null, externalId, objectId, null,
                organizationId, null);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        return Objects.equals(domainObject.getValueId(StreetStrategy.STREET_TYPE), streetTypeCorrections.get(0).getObjectId()) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(StreetStrategy.NAME)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(StreetStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        return ((StreetCorrection) correction).getStreetTypeId().equals(streetTypeCorrections.get(0).getObjectId()) &&
                StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        StreetCorrection c1 = (StreetCorrection) correction1;
        StreetCorrection c2 = (StreetCorrection) correction2;

        return c1.getCityId().equals(c2.getCityId()) &&
                c1.getStreetTypeId().equals(c2.getStreetTypeId()) &&
                StringUtil.isEqualIgnoreCase(c1.getCorrection(), c2.getCorrection());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(domainSync.getParentId(), organizationId);

        if (cityCorrections.isEmpty()){
            throw new CorrectionNotFoundException("city correction not found " + cityCorrections);
        }

        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        return streetStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("city")
                        .setParentId(cityCorrections.get(0).getObjectId())
                        .addAttribute(StreetStrategy.STREET_TYPE, streetTypeCorrections.get(0).getObjectId())
                        .addAttribute(StreetStrategy.NAME, domainSync.getName())
                        .addAttribute(StreetStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocaleId()));
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
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        ((StreetCorrection) correction).setStreetTypeId(streetTypeCorrections.get(0).getObjectId());
        correction.setCorrection(domainSync.getName());

        addressCorrectionBean.save((StreetCorrection) correction);
    }

    @Override
    public IStrategy getStrategy() {
        return streetStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(domainSync.getParentId(), organizationId);

        if (cityCorrections.isEmpty()){
            throw new CorrectionNotFoundException("city correction not found " + cityCorrections);
        }

        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                Long.valueOf(domainSync.getAdditionalParentId()), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new CorrectionNotFoundException("street type correction not found" + domainSync);
        }

        domainObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
        domainObject.setParentId(cityCorrections.get(0).getObjectId());
        domainObject.setValueId(StreetStrategy.STREET_TYPE, streetTypeCorrections.get(0).getObjectId());
        domainObject.setStringValue(StreetStrategy.STREET_CODE, domainSync.getAdditionalExternalId());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(StreetStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
    }
}
