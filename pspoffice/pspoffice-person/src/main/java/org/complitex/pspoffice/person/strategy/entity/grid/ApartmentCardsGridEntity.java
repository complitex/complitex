/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class ApartmentCardsGridEntity implements Serializable {

    private final String apartmentCard;
    private final long apartmentCardId;
    private final String ownershipForm;
    private final int registered;
    private final DomainObject organization;
    private final String owner;

    public ApartmentCardsGridEntity(String apartmentCard, long apartmentCardId, String ownershipForm, int registered,
            DomainObject organization, String owner) {
        this.apartmentCard = apartmentCard;
        this.apartmentCardId = apartmentCardId;
        this.ownershipForm = ownershipForm;
        this.registered = registered;
        this.organization = organization;
        this.owner = owner;
    }

    public String getApartmentCard() {
        return apartmentCard;
    }

    public long getApartmentCardId() {
        return apartmentCardId;
    }

    public DomainObject getOrganization() {
        return organization;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnershipForm() {
        return ownershipForm;
    }

    public int getRegistered() {
        return registered;
    }
}
