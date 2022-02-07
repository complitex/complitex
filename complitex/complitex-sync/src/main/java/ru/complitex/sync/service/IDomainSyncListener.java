package ru.complitex.sync.service;

import ru.complitex.address.entity.AddressEntity;
import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.sync.entity.DomainSync;

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
