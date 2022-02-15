package ru.complitex.sync.handler;

import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.exception.RemoteCallException;
import ru.complitex.address.strategy.street_type.StreetTypeStrategy;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.DomainSyncParameter;
import ru.complitex.sync.service.DomainSyncJsonAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.util.StringUtil.isEqualIgnoreCase;

/**
 * @author Anatoly Ivanov
 *         Date: 23.07.2014 22:57
 */
@Stateless
public class StreetTypeSyncHandler extends DomainSyncHandler {
    @EJB
    private DomainSyncJsonAdapter addressSyncJsonAdapter;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private CorrectionBean correctionBean;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return addressSyncJsonAdapter.getStreetTypeSyncs(new DomainSyncParameter(date));
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return isEqualIgnoreCase(domainSync.getName(), streetTypeStrategy.getName(domainObject)) //todo get
                && isEqualIgnoreCase(domainSync.getAdditionalName(), streetTypeStrategy.getShortName(domainObject))
                && isEqualIgnoreCase(domainSync.getAltName(), streetTypeStrategy.getName(domainObject, Locales.getAlternativeLocale()))
                && isEqualIgnoreCase(domainSync.getAltAdditionalName(), streetTypeStrategy.getShortName(domainObject, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return StringUtil.isEqualIgnoreCase(correction.getCorrection(), domainSync.getName());
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return StringUtil.isEqualIgnoreCase(correction1.getCorrection(), correction2.getCorrection());
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return streetTypeStrategy.getList(
                new DomainObjectFilter()
                        .setStatus(ShowMode.ACTIVE.name())
                        .setNullValue(true)
                        .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                        .addAttribute(StreetTypeStrategy.NAME, domainSync.getName())
                        .addAttribute(StreetTypeStrategy.SHORT_NAME, domainSync.getAdditionalName()));
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        Correction streetTypeCorrection = new Correction(AddressEntity.STREET_TYPE.getEntityName(),
                domainSync.getExternalId(), domainObject.getObjectId(), domainSync.getName(), organizationId,
                null);

        correctionBean.save(streetTypeCorrection);

        return streetTypeCorrection;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {
        correction.setCorrection(domainSync.getName());

        correctionBean.save(correction);
    }

    @Override
    public IStrategy getStrategy() {
        return streetTypeStrategy;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        domainObject.setStringValue(StreetTypeStrategy.NAME, domainSync.getName());
        domainObject.setStringValue(StreetTypeStrategy.NAME, domainSync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, domainSync.getAdditionalName());
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, domainSync.getAltAdditionalName(), Locales.getAlternativeLocale());
    }
}
