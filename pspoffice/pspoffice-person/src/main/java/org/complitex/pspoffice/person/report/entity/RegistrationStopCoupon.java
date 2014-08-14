/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
public class RegistrationStopCoupon implements Serializable {

    private Registration registration;
    private long addressId;
    private String addressEntity;
    private DomainObject registrationOrganization;
    private String passportInfo;
    private String birthCertificateInfo;
    private String additionalInfo;

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

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

    public String getBirthCertificateInfo() {
        return birthCertificateInfo;
    }

    public void setBirthCertificateInfo(String birthCertificateInfo) {
        this.birthCertificateInfo = birthCertificateInfo;
    }

    public String getPassportInfo() {
        return passportInfo;
    }

    public void setPassportInfo(String passportInfo) {
        this.passportInfo = passportInfo;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public DomainObject getRegistrationOrganization() {
        return registrationOrganization;
    }

    public void setRegistrationOrganization(DomainObject registrationOrganization) {
        this.registrationOrganization = registrationOrganization;
    }
}
