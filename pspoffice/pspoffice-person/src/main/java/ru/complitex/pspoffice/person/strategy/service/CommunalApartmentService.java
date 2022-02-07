/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Artem
 */
@Stateless
public class CommunalApartmentService extends AbstractBean {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;


    public boolean isCommunalApartmentCard(ApartmentCard apartmentCard) {
        if (apartmentCard.getObjectId() == null) {
            return false;
        }

        Long apartmentId = apartmentCardStrategy.getApartmentId(apartmentCard);

        if (apartmentId == null) {
            return false;
        }
        return apartmentCardStrategy.countByAddress("apartment", apartmentId) > 1;
    }
}
