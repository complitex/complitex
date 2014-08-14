/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Date;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StatusType;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class Registration extends DomainObject {

    private Person person;
    private DomainObject ownerRelationship;
    private DomainObject registrationType;

    public Registration() {
    }

    public Registration(DomainObject object) {
        super(object);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DomainObject getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(DomainObject registrationType) {
        this.registrationType = registrationType;
    }

    public String getArrivalCountry() {
        return getStringValue(this, ARRIVAL_COUNTRY);
    }

    public String getArrivalRegion() {
        return getStringValue(this, ARRIVAL_REGION);
    }

    public String getArrivalDistrict() {
        return getStringValue(this, ARRIVAL_DISTRICT);
    }

    public String getArrivalCity() {
        return getStringValue(this, ARRIVAL_CITY);
    }

    public String getArrivalStreet() {
        return getStringValue(this, ARRIVAL_STREET);
    }

    public String getArrivalBuildingNumber() {
        return getStringValue(this, ARRIVAL_BUILDING_NUMBER);
    }

    public String getArrivalBuildingCorp() {
        return getStringValue(this, ARRIVAL_BUILDING_CORP);
    }

    public String getArrivalApartment() {
        return getStringValue(this, ARRIVAL_APARTMENT);
    }

    public Date getArrivalDate() {
        return getDateValue(this, ARRIVAL_DATE);
    }

    public String getDepartureCountry() {
        return getStringValue(this, DEPARTURE_COUNTRY);
    }

    public String getDepartureRegion() {
        return getStringValue(this, DEPARTURE_REGION);
    }

    public String getDepartureDistrict() {
        return getStringValue(this, DEPARTURE_DISTRICT);
    }

    public String getDepartureCity() {
        return getStringValue(this, DEPARTURE_CITY);
    }

    public String getDepartureStreet() {
        return getStringValue(this, DEPARTURE_STREET);
    }

    public String getDepartureBuildingNumber() {
        return getStringValue(this, DEPARTURE_BUILDING_NUMBER);
    }

    public String getDepartureBuildingCorp() {
        return getStringValue(this, DEPARTURE_BUILDING_CORP);
    }

    public String getDepartureApartment() {
        return getStringValue(this, DEPARTURE_APARTMENT);
    }

    public Date getDepartureDate() {
        return getDateValue(this, DEPARTURE_DATE);
    }

    public String getDepartureReason() {
        return getStringValue(this, DEPARTURE_REASON);
    }

    public Date getRegistrationDate() {
        return getDateValue(this, REGISTRATION_DATE);
    }

    public boolean isFinished() {
        return getStatus() != StatusType.ACTIVE;
    }

    public DomainObject getOwnerRelationship() {
        return ownerRelationship;
    }

    public void setOwnerRelationship(DomainObject ownerRelationship) {
        this.ownerRelationship = ownerRelationship;
    }

    public long getEditedByUserId() {
        return getIntegerValue(this, EDITED_BY_USER_ID);
    }

    public String getExplanation() {
        return getStringValue(this, EXPLANATION);
    }
}
