/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Date;
import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class RegisterOwnerCard implements Serializable {

    private Date registrationDate;
    private DomainObject registrationType;
    private boolean registerChildren;
    private String country;
    private String region;
    private String district;
    private String city;
    private String street;
    private String buildingNumber;
    private String buildingCorp;
    private String apartment;
    private Date arrivalDate;

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getBuildingCorp() {
        return buildingCorp;
    }

    public void setBuildingCorp(String buildingCorp) {
        this.buildingCorp = buildingCorp;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isRegisterChildren() {
        return registerChildren;
    }

    public void setRegisterChildren(boolean registerChildren) {
        this.registerChildren = registerChildren;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public DomainObject getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(DomainObject registrationType) {
        this.registrationType = registrationType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
