package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

public class StreetCorrection extends Correction {
    private Long cityObjectId;
    private Long streetTypeObjectId;

    public StreetCorrection() {
    }

    public StreetCorrection(Long cityObjectId, Long streetTypeObjectId, String externalId, Long objectId,
                            String correction, Long organizationId, Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.cityObjectId = cityObjectId;
        this.streetTypeObjectId = streetTypeObjectId;
    }

    @Override
    public String getEntity() {
        return AddressEntity.STREET.getTable();
    }

    @Deprecated
    public Correction getStreetTypeCorrection() {
        return null;
    }

    public Long getCityObjectId() {
        return cityObjectId;
    }

    public void setCityObjectId(Long cityObjectId) {
        this.cityObjectId = cityObjectId;
    }

    public Long getStreetTypeObjectId() {
        return streetTypeObjectId;
    }

    public void setStreetTypeObjectId(Long streetTypeObjectId) {
        this.streetTypeObjectId = streetTypeObjectId;
    }
}
