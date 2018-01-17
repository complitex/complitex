package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

/**
 * Объект коррекции дома
 * @author Anatoly A. Ivanov java@inheaven.ru
 */
public class BuildingCorrection extends Correction {
    private Long streetId;
    private String correctionCorp;

    public BuildingCorrection() {
    }

    public BuildingCorrection(Long streetId, Long externalId, Long objectId, String correction, String correctionCorp,
                              Long organizationId, Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.streetId = streetId;
        this.correctionCorp = correctionCorp;
    }

    @Override
    public String getEntity() {
        return AddressEntity.BUILDING.getEntity();
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public String getCorrectionCorp() {
        return correctionCorp;
    }

    public void setCorrectionCorp(String correctionCorp) {
        this.correctionCorp = correctionCorp;
    }

    @Override
    public String toString() {
        return getToStringHelper()
                .add("streetId", streetId)
                .add("correctionCorp", correctionCorp)
                .toString();
    }
}
