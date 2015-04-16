package org.complitex.address.entity;

import java.io.Serializable;

/**
 * @author inheaven on 002 02.04.15 17:45
 */
public class ExternalAddress implements Serializable{
    private String city;
    private String district;
    private String streetType;
    private String street;
    private String buildingNumber;
    private String buildingCorp;
    private String apartment;
    private String room;

    private String streetTypeCode;
    private String streetCode;

    private Long organizationId;
    private Long userOrganizationId;

    public ExternalAddress() {
    }

    public ExternalAddress(String city, String streetType, String street, String buildingNumber,
                           String buildingCorp, String apartment, String room,
                           Long organizationId, Long userOrganizationId) {
        this.city = city;
        this.streetType = streetType;
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.buildingCorp = buildingCorp;
        this.apartment = apartment;
        this.room = room;
        this.organizationId = organizationId;
        this.userOrganizationId = userOrganizationId;
    }

    public ExternalAddress(String city, String streetType, String street, String buildingNumber) {
        this.city = city;
        this.streetType = streetType;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    public static ExternalAddress of(String city, String streetType, String street, String buildingNumber,
                                     Long organizationId, Long userOrganizationId){
        return new ExternalAddress(city, streetType, street, buildingNumber, null, null, null, organizationId, userOrganizationId);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getBuildingCorp() {
        return buildingCorp;
    }

    public void setBuildingCorp(String buildingCorp) {
        this.buildingCorp = buildingCorp;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStreetTypeCode() {
        return streetTypeCode;
    }

    public void setStreetTypeCode(String streetTypeCode) {
        this.streetTypeCode = streetTypeCode;
    }

    public String getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(String streetCode) {
        this.streetCode = streetCode;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserOrganizationId() {
        return userOrganizationId;
    }

    public void setUserOrganizationId(Long userOrganizationId) {
        this.userOrganizationId = userOrganizationId;
    }
}
