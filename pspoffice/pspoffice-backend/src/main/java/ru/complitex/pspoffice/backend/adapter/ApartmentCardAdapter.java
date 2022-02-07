package ru.complitex.pspoffice.backend.adapter;

import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.api.model.*;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 03.11.2017 15:27
 */
public class ApartmentCardAdapter {
    public static ApartmentCardModel adaptSimple(ApartmentCard apartmentCard){
        ApartmentCardModel apartmentCardModel = new ApartmentCardModel();

        apartmentCardModel.setId(apartmentCard.getObjectId());

        PersonModel personModel = new PersonModel();
        personModel.setLastName(PersonAdapter.adaptNames(apartmentCard.getOwner().getLastNames()));
        personModel.setFirstName(PersonAdapter.adaptNames(apartmentCard.getOwner().getFirstNames()));
        personModel.setMiddleName(PersonAdapter.adaptNames(apartmentCard.getOwner().getMiddleNames()));
        apartmentCardModel.setOwner(personModel);

        DomainModel ownershipForm = new DomainModel();
        DomainAttributeModel ownershipFormAttribute = new DomainAttributeModel();
        ownershipFormAttribute.setValues(DomainAdapter.adaptNames(apartmentCard.getOwnershipForm()
                .getAttribute(OwnershipFormStrategy.NAME).getStringValues()));
        ownershipForm.setAttributes(Collections.singletonList(ownershipFormAttribute));
        apartmentCardModel.setOwnershipForm(ownershipForm);

        apartmentCardModel.setRegistrations(apartmentCard.getRegistrations().stream()
                .map(r -> new RegistrationModel(r.getId())).collect(Collectors.toList()));

        return apartmentCardModel;
    }

}
