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

/**
 * @author Anatoly Ivanov
 *         Date: 30.07.2014 0:08
 */
@Local
public interface IDomainSyncHandler {
    Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException;

    List<DomainSync> getParentDomainSyncs();

    List<? extends Correction> getCorrections(Long externalId, Long objectId, Long organizationId);

    boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId);

    boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId);

    boolean isCorresponds(Correction correction1, Correction correction2);

    List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId);

    Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId);

    void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId);

    IStrategy getStrategy();

    void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId);

    @Deprecated
    Long getParentObjectId(DomainObject parentDomainObject, DomainSync domainSync, Long organizationId);
}
