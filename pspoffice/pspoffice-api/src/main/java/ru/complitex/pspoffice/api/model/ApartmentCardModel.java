package ru.complitex.pspoffice.api.model;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 03.11.2017 14:56
 */
public class ApartmentCardModel extends DomainModel{
    private PersonModel owner;
    private DomainModel ownershipForm;
    private List<RegistrationModel> registrations;

    public PersonModel getOwner() {
        return owner;
    }

    public void setOwner(PersonModel owner) {
        this.owner = owner;
    }

    public DomainModel getOwnershipForm() {
        return ownershipForm;
    }

    public void setOwnershipForm(DomainModel ownershipForm) {
        this.ownershipForm = ownershipForm;
    }

    public List<RegistrationModel> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<RegistrationModel> registrations) {
        this.registrations = registrations;
    }
}
