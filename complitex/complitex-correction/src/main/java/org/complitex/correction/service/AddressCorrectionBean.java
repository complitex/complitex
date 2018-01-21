package org.complitex.correction.service;

import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.correction.entity.Correction;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.01.2018 14:06
 */
@Stateless
public class AddressCorrectionBean {
    public List<Correction> getCorrections(ExternalAddress externalAddress){
        return null;
    }

    public List<Correction> getCorrections(LocalAddress localAddress, ExternalAddress externalAddress){
        return null;
    }

    public List<Correction> getStreetCorrectionsByBuilding(Long streetId, Long buildingId, Long organizationId) {
        return null; //todo
    }
}
