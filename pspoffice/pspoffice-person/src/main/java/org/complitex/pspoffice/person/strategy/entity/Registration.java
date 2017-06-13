/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Date;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.Status;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.common.util.AttributeUtil.*;

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
        return getStringValue(ARRIVAL_COUNTRY);
    }

    public String getArrivalRegion() {
        return getStringValue(ARRIVAL_REGION);
    }

    public String getArrivalDistrict() {
        return getStringValue(ARRIVAL_DISTRICT);
    }

    public String getArrivalCity() {
        return getStringValue(ARRIVAL_CITY);
    }

    public String getArrivalStreet() {
        return getStringValue(ARRIVAL_STREET);
    }

    public String getArrivalBuildingNumber() {
        return getStringValue(ARRIVAL_BUILDING_NUMBER);
    }

    public String getArrivalBuildingCorp() {
        return getStringValue(ARRIVAL_BUILDING_CORP);
    }

    public String getArrivalApartment() {
        return getStringValue(ARRIVAL_APARTMENT);
    }

    public Date getArrivalDate() {
        return getDateValue(this, ARRIVAL_DATE);
    }

    public String getDepartureCountry() {
        return getStringValue(DEPARTURE_COUNTRY);
    }

    public String getDepartureRegion() {
        return getStringValue(DEPARTURE_REGION);
    }

    public String getDepartureDistrict() {
        return getStringValue(DEPARTURE_DISTRICT);
    }

    public String getDepartureCity() {
        return getStringValue(DEPARTURE_CITY);
    }

    public String getDepartureStreet() {
        return getStringValue(DEPARTURE_STREET);
    }

    public String getDepartureBuildingNumber() {
        return getStringValue(DEPARTURE_BUILDING_NUMBER);
    }

    public String getDepartureBuildingCorp() {
        return getStringValue(DEPARTURE_BUILDING_CORP);
    }

    public String getDepartureApartment() {
        return getStringValue(DEPARTURE_APARTMENT);
    }

    public Date getDepartureDate() {
        return getDateValue(this, DEPARTURE_DATE);
    }

    public String getDepartureReason() {
        return getStringValue(DEPARTURE_REASON);
    }

    public Date getRegistrationDate() {
        return getDateValue(this, REGISTRATION_DATE);
    }

    public boolean isFinished() {
        return getStatus() != Status.ACTIVE;
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
        return getStringValue(EXPLANATION);
    }
}
