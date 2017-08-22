package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

/**
 * @author Pavel Sknar
 */
public class RoomCorrection extends Correction {
    private Long buildingId;
    private Long apartmentId;

    public RoomCorrection() {
    }

    public RoomCorrection(Long buildingId, Long apartmentId, String externalId, Long objectId, String correction, Long organizationId,
                          Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.buildingId = buildingId;
        this.apartmentId = apartmentId;
    }

    @Override
    public String getEntity() {
        return AddressEntity.ROOM.getEntity();
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }
}
