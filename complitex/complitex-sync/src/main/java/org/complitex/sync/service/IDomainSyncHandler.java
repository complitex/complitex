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
    Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException;

    List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map);

    default void sync(Long parentId){

    }
}
