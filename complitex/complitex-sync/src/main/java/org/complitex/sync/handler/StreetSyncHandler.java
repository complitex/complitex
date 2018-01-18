package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.Status;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.util.StringUtil;
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
import java.util.Objects;

import static org.complitex.common.util.StringUtil.isEqualIgnoreCase;

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

    @Override
    public List<? extends DomainObject> getParentObjects() {
        return cityStrategy.getList(new DomainObjectFilter().setStatus(Status.ACTIVE.name()));
    }

    @Override
    public List<? extends Correction> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId) {
        return addressCorrectionBean.getStreetCorrections(parentObjectId, null, externalId, objectId, null,
                organizationId, null);
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
        }

        return Objects.equals(domainObject.getValueId(StreetStrategy.STREET_TYPE), streetTypeCorrections.get(0).getObjectId()) &&
                isEqualIgnoreCase(domainSync.getName(), domainObject.getStringValue(StreetStrategy.NAME)) &&
                isEqualIgnoreCase(domainSync.getAltName(), domainObject.getStringValue(StreetStrategy.NAME, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
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
        List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
        }

        return streetStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .setParentEntity("city")
                        .setParentId(domainSync.getParentObjectId())
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
                domainSync.getAdditionalParentId(), null, organizationId);

        if (streetTypeCorrections.isEmpty()) {
            throw new RuntimeException("street type correction not found" + domainSync);
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

    @Override
    public Long getParentObjectId(DomainObject parentDomainObject, DomainSync domainSync, Long organizationId) {
        return parentDomainObject.getObjectId();
    }
}
