/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.person.report.entity.FamilyAndApartmentInfo;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Artem
 */
@Stateless
public class FamilyAndApartmentInfoBean extends AbstractBean {
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public FamilyAndApartmentInfo get(ApartmentCard apartmentCard) {
        FamilyAndApartmentInfo info = new FamilyAndApartmentInfo();
        info.setAddressEntity(apartmentCardStrategy.getAddressEntity(apartmentCard));
        info.setAddressId(apartmentCard.getAddressId());

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person person = registration.getPerson();
                member.setPerson(person);
                member.setRegistrationDate(registration.getRegistrationDate());
                member.setRelation(registration.getOwnerRelationship());
                info.addFamilyMember(member);
            }
        }
        return info;
    }
}
