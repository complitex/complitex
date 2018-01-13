package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.correction.entity.Correction;
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

    List<? extends Correction> getCorrections(Long parentObjectId, Long externalId, Long objectId, Long organizationId);

    void update(Correction correction);

    boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId);

    List<? extends DomainObject> getDomainObjects(DomainSync domainSync);

    Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId);

    IStrategy getStrategy();

    void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId);
}
