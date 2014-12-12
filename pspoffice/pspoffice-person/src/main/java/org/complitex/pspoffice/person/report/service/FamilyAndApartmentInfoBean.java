/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import org.complitex.common.service.AbstractBean;
import org.complitex.pspoffice.person.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

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
