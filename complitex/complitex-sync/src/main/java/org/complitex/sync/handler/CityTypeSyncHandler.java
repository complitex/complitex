package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.correction.entity.Correction;
import org.complitex.sync.entity.DomainSync;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 17:18
 */
public class CityTypeSyncHandler implements IDomainSyncHandler{
    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainSync parentDomainSync, Date date) throws RemoteCallException {
        return null;
    }

    @Override
    public List<DomainSync> getParentDomainSyncs() {
        return null;
    }

    @Override
    public boolean isCorresponds(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return false;
    }

    @Override
    public boolean isCorresponds(Correction correction, DomainSync domainSync, Long organizationId) {
        return false;
    }

    @Override
    public boolean isCorresponds(Correction correction1, Correction correction2) {
        return false;
    }

    @Override
    public List<? extends DomainObject> getDomainObjects(DomainSync domainSync, Long organizationId) {
        return null;
    }

    @Override
    public Correction insertCorrection(DomainObject domainObject, DomainSync domainSync, Long organizationId) {
        return null;
    }

    @Override
    public void updateCorrection(Correction correction, DomainSync domainSync, Long organizationId) {

    }

    @Override
    public IStrategy getStrategy() {
        return null;
    }

    @Override
    public void updateValues(DomainObject domainObject, DomainSync domainSync, Long organizationId) {

    }
}
