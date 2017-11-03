package ru.complitex.pspoffice.api.model;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 03.11.2017 15:16
 */
public class RegistrationModel implements Serializable {
    private Long id;

    private PersonModel personModel;
    private DomainModel ownerRelationship;
    private DomainModel registrationType;

    public RegistrationModel() {
    }

    public RegistrationModel(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonModel getPersonModel() {
        return personModel;
    }

    public void setPersonModel(PersonModel personModel) {
        this.personModel = personModel;
    }

    public DomainModel getOwnerRelationship() {
        return ownerRelationship;
    }

    public void setOwnerRelationship(DomainModel ownerRelationship) {
        this.ownerRelationship = ownerRelationship;
    }

    public DomainModel getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(DomainModel registrationType) {
        this.registrationType = registrationType;
    }
}
