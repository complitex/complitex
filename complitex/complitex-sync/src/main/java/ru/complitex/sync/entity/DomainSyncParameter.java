package ru.complitex.sync.entity;

import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class DomainSyncParameter {
    private String catalog;
    private String country;
    private String region;
    private String cityType;
    private String city;
    private String district;
    private String streetType;
    private String street;
    private Date date;

    public DomainSyncParameter() {
    }

    public DomainSyncParameter(Date date) {
        this.date = date;
    }

    private DomainSyncParameter(String catalog, String country, String region,
                                String cityType, String city, String district,
                                String streetType, String street, Date date) {
        this.catalog = catalog;
        this.country = country;
        this.region = region;
        this.cityType = cityType;
        this.city = city;
        this.district = district;
        this.streetType = streetType;
        this.street = street;
        this.date = date;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DomainSyncParameter catalog(String catalogName) {
        setCatalog(catalogName);

        return this;
    }
}
