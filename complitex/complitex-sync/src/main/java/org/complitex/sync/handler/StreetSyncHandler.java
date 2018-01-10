package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;
import org.complitex.sync.service.IDomainSyncHandler;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.complitex.address.strategy.street.StreetStrategy.STREET_CODE;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:46
 */
@Stateless
public class StreetSyncHandler implements IDomainSyncHandler {
    @EJB
    private ConfigBean configBean;

    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private DomainSyncBean domainSyncBean;

    @Override
    public Cursor<DomainSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return domainSyncAdapter.getStreetSyncs(cityStrategy.getName(parent),
                cityTypeStrategy.getShortName(parent.getAttribute(CityStrategy.CITY_TYPE).getValueId()),
                date);
    }

    @Override
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
    public boolean isEqualNames(DomainSync sync, DomainObject object) {
        return Objects.equals(sync.getName(), streetStrategy.getName(object))
                && Objects.equals(sync.getAltName(), streetStrategy.getName(object, Locales.getAlternativeLocale()))
                && Objects.equals(sync.getAdditionalExternalId(), streetStrategy.getStreetTypeExternalId(object));
    }

    @Override
    public Long getParentId(DomainSync sync, DomainObject parent) {
        return parent.getObjectId();
    }

    @Override
    public void insert(DomainSync sync) {
        if (false){ //todo
            DomainObject oldStreet = streetStrategy.getDomainObject(sync.getObjectId());
            DomainObject street = streetStrategy.getDomainObject(sync.getObjectId());

            street.addAttribute(STREET_CODE, Long.valueOf(sync.getExternalId()));

            streetStrategy.update(oldStreet, street, sync.getDate());
            domainSyncBean.delete(sync.getId());
        }else{
            DomainObject newObject = streetStrategy.newInstance();
            newObject.setExternalId(sync.getExternalId());

            //name
            newObject.setStringValue(StreetStrategy.NAME, sync.getName());
            newObject.setStringValue(StreetStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());

            //CITY_ID
            newObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
            newObject.setParentId(sync.getParentId());

            //STREET_TYPE_ID
            Long streetTypeId = streetTypeStrategy.getObjectId(sync.getAdditionalExternalId());

            if (streetTypeId == null) {
                throw new RuntimeException("StreetType not found: " + sync);
            }
            newObject.setValue(StreetStrategy.STREET_TYPE, streetTypeId);

            streetStrategy.insert(newObject, sync.getDate());
            domainSyncBean.delete(sync.getId());
        }
    }

    @Override
    public void update(DomainSync sync) {
        DomainObject oldObject = streetStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setStringValue(StreetStrategy.NAME, sync.getName());
        newObject.setStringValue(StreetStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());

        Long streetTypeId = streetTypeStrategy.getObjectId(sync.getAdditionalExternalId());

        if (streetTypeId == null) {
            throw new RuntimeException("StreetType not found: " + sync);
        }
        newObject.setValue(StreetStrategy.STREET_TYPE, streetTypeId);

        streetStrategy.update(oldObject, newObject, sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(DomainSync sync) {
        streetStrategy.archive(streetStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        domainSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return streetStrategy.getName(object, Locales.getSystemLocale());
    }
}
