package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:56
 */
public class DistrictCorrection extends Correction {
    private Long cityId;

    public DistrictCorrection() {
    }

    public DistrictCorrection(Long cityId, String externalId, Long objectId, String correction, Long organizationId,
                              Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.cityId = cityId;
    }

    @Override
    public String getEntity() {
        return AddressEntity.DISTRICT.getEntityName();
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}
