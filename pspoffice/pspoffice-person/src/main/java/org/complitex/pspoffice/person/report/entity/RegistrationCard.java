/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
public class RegistrationCard implements Serializable {

    private Registration registration;
    private long addressId;
    private String addressEntity;
    private String nationality;
    private String passportSeries;
    private String passportNumber;
    private String passportIssued;

    public String getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(String addressEntity) {
        this.addressEntity = addressEntity;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassportIssued() {
        return passportIssued;
    }

    public void setPassportIssued(String passportIssued) {
        this.passportIssued = passportIssued;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
}
