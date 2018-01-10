package org.complitex.sync.service;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.sync.entity.DomainSync;

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 *         Date: 30.07.2014 0:08
 */
@Local
public interface IDomainSyncHandler {
    Long NOT_FOUND_ID = -1L;

    Cursor<DomainSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException;

    List<? extends DomainObject> getObjects(DomainObject parent);

    List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map);

    boolean isEqualNames(DomainSync sync, DomainObject object);

    default boolean hasEqualNames(DomainSync sync, DomainObject object){
        return isEqualNames(sync, object);
    }

    Long getParentId(DomainSync sync, DomainObject parent);

    void insert(DomainSync sync);

    void update(DomainSync sync);

    void archive(DomainSync sync);

    String getName(DomainObject object);

    default void bind(Long parentId){
        //todo implement
    }
}
