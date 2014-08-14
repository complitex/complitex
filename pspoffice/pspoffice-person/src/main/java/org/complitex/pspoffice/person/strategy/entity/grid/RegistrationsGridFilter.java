/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.Locale;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;

/**
 *
 * @author Artem
 */
public class RegistrationsGridFilter implements Serializable {

    private final ApartmentCard apartmentCard;
    private final Locale locale;

    public RegistrationsGridFilter(ApartmentCard apartmentCard, Locale locale) {
        this.apartmentCard = apartmentCard;
        this.locale = locale;
    }

    public ApartmentCard getApartmentCard() {
        return apartmentCard;
    }

    public Locale getLocale() {
        return locale;
    }
}
