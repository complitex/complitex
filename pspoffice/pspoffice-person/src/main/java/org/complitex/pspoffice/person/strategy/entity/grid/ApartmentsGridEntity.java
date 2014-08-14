/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity.grid;

import java.io.Serializable;
import java.util.List;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;

/**
 *
 * @author Artem
 */
public class ApartmentsGridEntity implements Serializable {

    private final String number;
    private final String entity;
    private final long objectId;
    private final List<DomainObject> rooms;
    private final List<ApartmentCard> apartmentCards;
    private final int registered;
    private final List<DomainObject> organizations;

    public ApartmentsGridEntity(String number, String entity, long objectId, List<DomainObject> rooms,
            List<ApartmentCard> apartmentCards, int registered, List<DomainObject> organizations) {
        this.number = number;
        this.entity = entity;
        this.objectId = objectId;
        this.rooms = rooms;
        this.apartmentCards = apartmentCards;
        this.registered = registered;
        this.organizations = organizations;
    }

    public String getEntity() {
        return entity;
    }

    public String getNumber() {
        return number;
    }

    public long getObjectId() {
        return objectId;
    }

    public List<ApartmentCard> getApartmentCards() {
        return apartmentCards;
    }

    public List<DomainObject> getOrganizations() {
        return organizations;
    }

    public int getRegistered() {
        return registered;
    }

    public List<DomainObject> getRooms() {
        return rooms;
    }
}
