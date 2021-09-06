package ru.complitex.pspoffice.address.sync.entity;

import ru.complitex.catalog.entity.Locale;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class SyncCatalog extends SyncParameter {
    private String country;
    private String region;
    private String city;
    private String cityType;
    private String district;
    private String street;
    private String streetType;
    private String house;
    private String part;

    public SyncCatalog() {
    }

    public SyncCatalog(LocalDate date, int locale) {
        super(date, getLocale(locale));
    }

    public static String getLocale(int locale) {
        return switch (locale) {
            case Locale.RU -> "RU";
            case Locale.UA -> "UA";
            default -> throw new IllegalStateException();
        };
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @Override
    public String toString() {
        return "SyncCatalog{" +
                (country != null ? "country='" + country + '\'' : "") +
                (region != null ? ", region='" + region + '\'' : "") +
                (city != null ? ", city='" + city + '\'' : "") +
                (cityType != null ? ", cityType='" + cityType + '\'' : "") +
                (district != null ? ", district='" + district + '\'' : "") +
                (street != null ? ", street='" + street + '\'' : "") +
                (streetType != null ? ", streetType='" + streetType + '\'' : "") +
                (house != null ? ", house='" + house + '\'' : "") +
                (part != null ? ", part='" + part + '\'' : "") +
                "} " + super.toString();
    }
}
