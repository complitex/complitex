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
public class RegisterChildrenCard implements Serializable {

    private Date registrationDate;
    private DomainObject registrationType;
    private long apartmentCardId;

    public long getApartmentCardId() {
        return apartmentCardId;
    }

    public void setApartmentCardId(long apartmentCardId) {
        this.apartmentCardId = apartmentCardId;
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
}
