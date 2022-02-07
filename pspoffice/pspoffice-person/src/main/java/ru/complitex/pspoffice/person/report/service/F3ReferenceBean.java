/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.person.report.entity.F3Reference;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.NeighbourFamily;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Artem
 */
@Stateless
public class F3ReferenceBean extends AbstractBean {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;


    public F3Reference get(Registration registration, ApartmentCard apartmentCard) {
        F3Reference f3 = new F3Reference();

        //name
        f3.setPerson(registration.getPerson());

        //address
        f3.setAddressId(apartmentCard.getAddressId());
        f3.setAddressEntity(apartmentCardStrategy.getAddressEntity(apartmentCard));
        f3.setPersonalAccountOwner(apartmentCard.getOwner());
        f3.setOwnershipForm(apartmentCard.getOwnershipForm());

        for (Registration r : apartmentCard.getRegistrations()) {
            if (!r.isFinished()) {
                FamilyMember member = new FamilyMember();
                member.setPerson(r.getPerson());
                member.setRegistrationDate(r.getRegistrationDate());
                member.setRelation(r.getOwnerRelationship());
                f3.addFamilyMember(member);
            }
        }

        //neighbors
        for (ApartmentCard neighbourCard : apartmentCardStrategy.getNeighbourApartmentCards(apartmentCard)) {
            NeighbourFamily family = new NeighbourFamily();
            family.setPerson(neighbourCard.getOwner());
            family.setAmount(neighbourCard.getRegisteredCount());
            f3.addNeighbourFamily(family);
        }

        return f3;
    }
}
