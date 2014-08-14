/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class RegistrationsGridEntity implements Serializable {

    private final String personName;
    private final long personId;
    private final String personBirthDate;
    private final String registrationDate;
    private final String registrationType;
    private final String ownerRelationship;

    public RegistrationsGridEntity(String personName, long personId, String personBirthDate,
            String registrationDate, String registrationType, String ownerRelationship) {
        this.personName = personName;
        this.personId = personId;
        this.personBirthDate = personBirthDate;
        this.registrationDate = registrationDate;
        this.registrationType = registrationType;
        this.ownerRelationship = ownerRelationship;
    }

    public String getOwnerRelationship() {
        return ownerRelationship;
    }

    public String getPersonBirthDate() {
        return personBirthDate;
    }

    public long getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getRegistrationType() {
        return registrationType;
    }
}
