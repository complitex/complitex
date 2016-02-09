package org.complitex.address.entity;

import java.io.Serializable;

/**
 * @author inheaven on 002 02.04.15 16:26
 */
public class LocalAddress implements Serializable{
    private Long cityId;
    private Long districtId;
    private Long streetTypeId;
    private Long streetId;
    private Long buildingId;
    private Long apartmentId;
    private Long roomId;

    public LocalAddress() {
    }

    public LocalAddress(Long cityId, Long streetTypeId, Long streetId, Long buildingId, Long apartmentId,
                        Long roomId) {
        this.cityId = cityId;
        this.streetTypeId = streetTypeId;
        this.streetId = streetId;
        this.buildingId = buildingId;
        this.apartmentId = apartmentId;
        this.roomId = roomId;
    }


    public AddressEntity getFirstEmptyAddressEntity(){
        return getFirstEmptyAddressEntity(true);
    }

    public AddressEntity getFirstEmptyAddressEntity(boolean streetType){
        if (cityId == null) {
            return AddressEntity.CITY;
        }

        if (streetType && streetTypeId == null) {
            return AddressEntity.STREET_TYPE;
        }

        if (streetId == null) {
            return AddressEntity.STREET;
        }

        if (buildingId == null) {
            return AddressEntity.BUILDING;
        }

        if (apartmentId == null) {
            return AddressEntity.APARTMENT;
        }

        if (roomId == null){
            return AddressEntity.ROOM;
        }

        return null;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getStreetTypeId() {
        return streetTypeId;
    }

    public void setStreetTypeId(Long streetTypeId) {
        this.streetTypeId = streetTypeId;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
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

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
