package org.complitex.address.service;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;

/**
 * @author Anatoly Ivanov
 *         Date: 008 08.07.14 16:12
 */
public interface IAddressSyncListener {
    void onBegin(DomainObject parent, AddressEntity type, Cursor<AddressSync> cursor);

    void onProcessed(AddressSync sync);

    void onError(String message);

    void onDone(AddressEntity type);
}
