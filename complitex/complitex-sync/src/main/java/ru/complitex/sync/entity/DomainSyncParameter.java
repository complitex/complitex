package ru.complitex.sync.entity;

import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class DomainSyncParameter {
    private String catalogName;
    private String countryName;
    private String regionName;
    private String cityTypeName;
    private String cityName;
    private String districtName;
    private String streetTypeName;
    private String streetName;
    private Date date;

    public DomainSyncParameter() {
    }

    public DomainSyncParameter(Date date) {
        this.date = date;
    }

    private DomainSyncParameter(String catalogName, String countryName, String regionName,
                                String cityTypeName, String cityName, String districtName,
                                String streetTypeName, String streetName, Date date) {
        this.catalogName = catalogName;
        this.countryName = countryName;
        this.regionName = regionName;
        this.cityTypeName = cityTypeName;
        this.cityName = cityName;
        this.districtName = districtName;
        this.streetTypeName = streetTypeName;
        this.streetName = streetName;
        this.date = date;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCityTypeName() {
        return cityTypeName;
    }

    public void setCityTypeName(String cityTypeName) {
        this.cityTypeName = cityTypeName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStreetTypeName() {
        return streetTypeName;
    }

    public void setStreetTypeName(String streetTypeName) {
        this.streetTypeName = streetTypeName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DomainSyncParameter catalog(String catalogName) {
        setCatalogName(catalogName);

        return this;
    }
}
