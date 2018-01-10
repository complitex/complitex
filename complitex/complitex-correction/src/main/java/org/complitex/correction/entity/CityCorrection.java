package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:35
 */
public class CityCorrection extends Correction {
    public CityCorrection() {
    }

    public CityCorrection(Long externalId, Long objectId, String correction, Long organizationId,
                          Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);
    }

    @Override
    public String getEntity() {
        return AddressEntity.CITY.getEntity();
    }
}
