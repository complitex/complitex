/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.person.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;

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

    @Transactional
    public FamilyAndCommunalApartmentInfo get(ApartmentCard apartmentCard) {
        FamilyAndCommunalApartmentInfo info = new FamilyAndCommunalApartmentInfo();
        info.setAddressEntity(ApartmentCardStrategy.getAddressEntity(apartmentCard));
        info.setAddressId(apartmentCard.getAddressId());
        info.setOwner(apartmentCard.getOwner());

        Long apartmentId = apartmentCardStrategy.getApartmentId(apartmentCard);
        DomainObject apartment = apartmentId != null ? strategyFactory.getStrategy("apartment").findById(apartmentId, true) : null;
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
