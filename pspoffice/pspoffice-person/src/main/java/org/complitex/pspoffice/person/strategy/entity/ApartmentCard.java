/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import com.google.common.base.Predicate;
import org.complitex.common.entity.DomainObject;

import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.complitex.common.util.AttributeUtil.getIntegerValue;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;

/**
 *
 * @author Artem
 */
public class ApartmentCard extends DomainObject {

    private List<Registration> registrations = newArrayList();
    private Person owner;
    private DomainObject ownershipForm;

    public ApartmentCard(DomainObject copy) {
        super(copy);
    }

    public ApartmentCard() {
    }

    public String getPersonalAccount() {
        return getStringValue(PERSONAL_ACCOUNT);
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public DomainObject getOwnershipForm() {
        return ownershipForm;
    }

    public void setOwnershipForm(DomainObject ownershipForm) {
        this.ownershipForm = ownershipForm;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
    }

    public long getAddressId() {
        return getAttribute(ADDRESS_APARTMENT).getValueId();
    }

    public String getHousingRights() {
        return getStringValue(HOUSING_RIGHTS);
    }

    public int getRegisteredCount() {
        return newArrayList(filter(registrations, new Predicate<Registration>() {

            @Override
            public boolean apply(Registration registration) {
                return !registration.isFinished();
            }
        })).size();
    }

    public long getEditedByUserId() {
        return getIntegerValue(this, EDITED_BY_USER_ID);
    }

    public String getExplanation() {
        return getStringValue(EXPLANATION);
    }
}
