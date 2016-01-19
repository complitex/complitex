package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anatoly Ivanov
 *         Date: 23.07.2014 22:57
 */
@Stateless
public class StreetTypeSyncHandler implements IAddressSyncHandler {
    @EJB
    private ConfigBean configBean;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private AddressSyncAdapter addressSyncAdapter;

    @EJB
    private AddressSyncBean addressSyncBean;

    @Override
    public Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return addressSyncAdapter.getStreetTypeSyncs();
    }

    @Override
    public List<? extends DomainObject> getObjects(DomainObject parent) {
        return streetTypeStrategy.getList(new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return null;
    }

    @Override
    public boolean isEqualNames(AddressSync sync, DomainObject object) {
        return Objects.equals(sync.getName(), streetTypeStrategy.getName(object))
                && Objects.equals(sync.getAdditionalName(), streetTypeStrategy.getShortName(object))
                && Objects.equals(sync.getAltName(), streetTypeStrategy.getName(object))
                && Objects.equals(sync.getAltAdditionalName(), streetTypeStrategy.getShortName(object, Locales.getAlternativeLocale()));
    }

    @Override
    public boolean hasEqualNames(AddressSync sync, DomainObject object) {
        return sync.getName().equals(streetTypeStrategy.getName(object))
                || sync.getAdditionalName().equals(streetTypeStrategy.getShortName(object));
    }

    @Override
    public Long getParentId(AddressSync sync, DomainObject parent) {
        return null;
    }

    @Override
    public void insert(AddressSync sync) {
        DomainObject domainObject = streetTypeStrategy.newInstance();

        //external id
        domainObject.setExternalId(sync.getExternalId());

        //name
        domainObject.setStringValue(StreetTypeStrategy.NAME, sync.getName());
        domainObject.setStringValue(StreetTypeStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());

        //short name
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, sync.getAdditionalName());
        domainObject.setStringValue(StreetTypeStrategy.SHORT_NAME, sync.getAltAdditionalName(), Locales.getAlternativeLocale());


        streetTypeStrategy.insert(domainObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(AddressSync sync) {
        DomainObject oldObject = streetTypeStrategy.getDomainObject(sync.getObjectId(), true);
        DomainObject newObject = CloneUtil.cloneObject(oldObject);

        //external id
        newObject.setExternalId(sync.getExternalId());

        //name
        newObject.setStringValue(StreetTypeStrategy.NAME, sync.getName());
        newObject.setStringValue(StreetTypeStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());

        //short name
        newObject.setStringValue(StreetTypeStrategy.SHORT_NAME, sync.getAdditionalName());
        newObject.setStringValue(StreetTypeStrategy.SHORT_NAME, sync.getAltAdditionalName(), Locales.getAlternativeLocale());

        streetTypeStrategy.update(oldObject, newObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public void archive(AddressSync sync) {
        streetTypeStrategy.archive(streetTypeStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return streetTypeStrategy.getName(object);
    }
}
