package ru.complitex.correction.service;

import ru.complitex.correction.entity.Correction;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.01.2018 14:06
 */
@Stateless
public class AddressCorrectionBean {
    public List<Correction> getStreetCorrectionsByBuilding(Long streetId, Long buildingId, Long organizationId) {
        return null; //todo
    }
}
