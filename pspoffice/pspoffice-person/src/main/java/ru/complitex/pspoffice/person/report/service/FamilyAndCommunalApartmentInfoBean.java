/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.service;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.pspoffice.person.report.entity.FamilyAndCommunalApartmentInfo;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.NeighbourFamily;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Artem
 */
@Stateless
public class FamilyAndCommunalApartmentInfoBean extends AbstractBean {
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @EJB
    private StrategyFactory strategyFactory;

    public FamilyAndCommunalApartmentInfo get(ApartmentCard apartmentCard) {
        FamilyAndCommunalApartmentInfo info = new FamilyAndCommunalApartmentInfo();
        info.setAddressEntity(apartmentCardStrategy.getAddressEntity(apartmentCard));
        info.setAddressId(apartmentCard.getAddressId());
        info.setOwner(apartmentCard.getOwner());

        Long apartmentId = apartmentCardStrategy.getApartmentId(apartmentCard);
        DomainObject apartment = apartmentId != null ? strategyFactory.getStrategy("apartment").getDomainObject(apartmentId, true) : null;
        //neighbors
        List<ApartmentCard> allApartmentCards = new ArrayList<ApartmentCard>();
        allApartmentCards.add(apartmentCard);
        allApartmentCards.addAll(apartmentCardStrategy.getNeighbourApartmentCards(apartmentCard));
        for (ApartmentCard neighbourCard : allApartmentCards) {
            NeighbourFamily family = new NeighbourFamily();
            family.setPerson(neighbourCard.getOwner());
            family.setApartment(apartment);
            info.addNeighbourFamily(family);
        }

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                member.setPerson(registration.getPerson());
                member.setRelation(registration.getOwnerRelationship());
                member.setRegistrationDate(registration.getRegistrationDate());
                info.addFamilyMember(member);
            }
        }

        return info;
    }
}
