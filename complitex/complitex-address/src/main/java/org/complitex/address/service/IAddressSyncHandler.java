package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 *         Date: 30.07.2014 0:08
 */
@Local
public interface IAddressSyncHandler {
    Long NOT_FOUND_ID = -1L;

    Cursor<AddressSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException;

    List<? extends DomainObject> getObjects(DomainObject parent);

    List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map);

    boolean isEqualNames(AddressSync sync, DomainObject object);

    default boolean hasEqualNames(AddressSync sync, DomainObject object){
        return isEqualNames(sync, object);
    }

    Long getParentId(AddressSync sync, DomainObject parent);

    void insert(AddressSync sync, Locale locale);

    void update(AddressSync sync, Locale locale);

    void archive(AddressSync sync);

    String getName(DomainObject object);
}
