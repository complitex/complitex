/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;

/**
 *
 * @author Artem
 */
@Stateless
public class CommunalApartmentService extends AbstractBean {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @Transactional
    public boolean isCommunalApartmentCard(ApartmentCard apartmentCard) {
        if (apartmentCard.getId() == null) {
            return false;
        }

        Long apartmentId = apartmentCardStrategy.getApartmentId(apartmentCard);

        if (apartmentId == null) {
            return false;
        }
        return apartmentCardStrategy.countByAddress("apartment", apartmentId) > 1;
    }
}
