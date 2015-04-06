package org.complitex.address.entity;

/**
 * @author inheaven on 002 02.04.15 16:26
 */
public class LocalAddress {
    private Long cityObjectId;
    private Long streetTypeObjectId;
    private Long streetObjectId;
    private Long buildingObjectId;
    private Long apartmentObjectId;

    private Long organizationId;

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

    public Long getStreetObjectId() {
        return streetObjectId;
    }

    public void setStreetObjectId(Long streetObjectId) {
        this.streetObjectId = streetObjectId;
    }

    public Long getBuildingObjectId() {
        return buildingObjectId;
    }

    public void setBuildingObjectId(Long buildingObjectId) {
        this.buildingObjectId = buildingObjectId;
    }

    public Long getApartmentObjectId() {
        return apartmentObjectId;
    }

    public void setApartmentObjectId(Long apartmentObjectId) {
        this.apartmentObjectId = apartmentObjectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
