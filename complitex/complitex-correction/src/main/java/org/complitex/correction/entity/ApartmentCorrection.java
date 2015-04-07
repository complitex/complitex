package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

/**
 * @author Pavel Sknar
 */
public class ApartmentCorrection extends Correction {
    private Long buildingId;

    public ApartmentCorrection() {
    }

    public ApartmentCorrection(Long buildingId, String externalId, Long objectId, String correction, Long organizationId,
                          Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.buildingId = buildingId;
    }

    @Override
    public String getEntity() {
        return AddressEntity.APARTMENT.getEntityName();
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }
}
