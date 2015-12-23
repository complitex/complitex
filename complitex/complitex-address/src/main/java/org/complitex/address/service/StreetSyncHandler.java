package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncStatus;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.complitex.address.strategy.street.StreetStrategy.STREET_CODE;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:46
 */
@Stateless
public class StreetSyncHandler implements IAddressSyncHandler {
    @EJB
    private ConfigBean configBean;

    @EJB
    private AddressSyncAdapter addressSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private AddressSyncBean addressSyncBean;

    @Override
    public Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return addressSyncAdapter.getStreetSyncs(cityStrategy.getName(parent),
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
    public boolean isEqualNames(AddressSync sync, DomainObject object) {
        return sync.getName().equals(streetStrategy.getName(object))
                && sync.getAdditionalName().equals(streetStrategy.getStreetTypeShortName(object));
    }

    @Override
    public Long getParentId(AddressSync sync, DomainObject parent) {
        return parent.getObjectId();
    }

    @Override
    public void insert(AddressSync sync, Locale locale) {
        if (sync.getStatus().equals(AddressSyncStatus.DUPLICATE)){
            DomainObject oldStreet = streetStrategy.getDomainObject(sync.getObjectId());
            DomainObject street = streetStrategy.getDomainObject(sync.getObjectId());

            Attribute code = new Attribute();
            code.setAttributeId((long) street.getAttributes(STREET_CODE).size() + 1); //todo add mass attribute add remove
            code.setAttributeTypeId(STREET_CODE);
            code.setValueId(Long.valueOf(sync.getExternalId()));
            code.setValueTypeId(STREET_CODE);

            street.addAttribute(code);

            streetStrategy.update(oldStreet, street, sync.getDate());
            addressSyncBean.delete(sync.getId());
        }else{
            DomainObject newObject = streetStrategy.newInstance();
            newObject.setExternalId(sync.getExternalId());

            //name
            newObject.setStringValue(StreetStrategy.NAME, sync.getName(), locale);

            //CITY_ID
            newObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
            newObject.setParentId(sync.getParentId());

            //STREET_TYPE_ID
            List<? extends DomainObject> streetTypes = streetTypeStrategy.getList(new DomainObjectFilter()
                    .addAttribute(StreetTypeStrategy.SHORT_NAME, sync.getAdditionalName()));
            if (streetTypes.isEmpty()) {
                throw new RuntimeException("StreetType not found: " + sync.getAdditionalName());
            }
            newObject.setLongValue(StreetStrategy.STREET_TYPE, streetTypes.get(0).getObjectId());

            streetStrategy.insert(newObject, sync.getDate());
            addressSyncBean.delete(sync.getId());
        }
    }

    @Override
    public void update(AddressSync sync, Locale locale) {
        DomainObject oldObject = streetStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        newObject.setStringValue(StreetStrategy.NAME, sync.getName(), locale);

        List<? extends DomainObject> streetTypes = streetTypeStrategy.getList(new DomainObjectFilter()
                .addAttribute(StreetTypeStrategy.SHORT_NAME, sync.getAdditionalName()));
        if (streetTypes.isEmpty()) {
            throw new RuntimeException("StreetType not found: " + sync.getAdditionalName());
        }
        newObject.getAttribute(StreetStrategy.STREET_TYPE).setValueId(streetTypes.get(0).getObjectId());

        streetStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(AddressSync sync) {
        streetStrategy.archive(streetStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return streetStrategy.getName(object, Locales.getSystemLocale());
    }
}
