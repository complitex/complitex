package org.complitex.sync.service;

import org.complitex.address.entity.AddressEntity;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.sync.entity.DomainSync;

/**
 * @author Anatoly Ivanov
 *         Date: 008 08.07.14 16:12
 */
public interface IDomainSyncListener {
    void onBegin(DomainObject parent, AddressEntity type, Cursor<DomainSync> cursor);

    void onProcessed(DomainSync sync);

    void onError(String message);

    void onDone(AddressEntity type);
}
