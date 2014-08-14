package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class BuildingsGridFilter implements Serializable {

    private final long cityId;
    private Long districtId;
    private Long streetId;
    private String buildingNumber;
    private final String buildingPermissionString;
    private final String apartmentPermissionString;
    private final String roomPermissionString;
    private long start;
    private long size;
    private final Locale locale;

    public BuildingsGridFilter(long cityId, Long streetId, String buildingPermissionString, String apartmentPermissionString,
            String roomPermissionString, Locale locale) {
        this.cityId = cityId;
        this.streetId = streetId;
        this.buildingPermissionString = buildingPermissionString;
        this.apartmentPermissionString = apartmentPermissionString;
        this.roomPermissionString = roomPermissionString;
        this.locale = locale;
    }

    public void reset(boolean resetStreet) {
        districtId = null;
        if (resetStreet) {
            streetId = null;
        }
        buildingNumber = null;
    }

    public String getApartmentPermissionString() {
        return apartmentPermissionString;
    }

    public String getBuildingPermissionString() {
        return buildingPermissionString;
    }

    public String getRoomPermissionString() {
        return roomPermissionString;
    }

    public long getCityId() {
        return cityId;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public Locale getLocale() {
        return locale;
    }
}
