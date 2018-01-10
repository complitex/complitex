package org.complitex.correction.entity;

import org.complitex.address.entity.AddressEntity;

public class StreetCorrection extends Correction {
    private Long cityId;
    private Long streetTypeId;

    public StreetCorrection() {
    }

    public StreetCorrection(Long cityId, Long streetTypeId, Long externalId, Long objectId,
                            String correction, Long organizationId, Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);

        this.cityId = cityId;
        this.streetTypeId = streetTypeId;
    }

    @Override
    public String getEntity() {
        return AddressEntity.STREET.getEntity();
    }

    @Deprecated
    public Correction getStreetTypeCorrection() {
        return null;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getStreetTypeId() {
        return streetTypeId;
    }

    public void setStreetTypeId(Long streetTypeId) {
        this.streetTypeId = streetTypeId;
    }
}
