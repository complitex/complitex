/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class ApartmentCardsGridFilter implements Serializable {

    private final long apartmentId;
    private final Locale locale;

    public ApartmentCardsGridFilter(long apartmentId, Locale locale) {
        this.apartmentId = apartmentId;
        this.locale = locale;
    }

    public long getApartmentId() {
        return apartmentId;
    }

    public Locale getLocale() {
        return locale;
    }
}
