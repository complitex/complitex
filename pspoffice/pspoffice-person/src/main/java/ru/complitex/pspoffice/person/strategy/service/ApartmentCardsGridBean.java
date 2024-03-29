/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.Lists;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridEntity;
import ru.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridFilter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardsGridBean extends AbstractBean {
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    public ApartmentCardsGridFilter newFilter(long apartmentId, Locale locale) {
        return new ApartmentCardsGridFilter(apartmentId, locale);
    }

    public List<ApartmentCardsGridEntity> find(ApartmentCardsGridFilter filter) {
        final List<ApartmentCardsGridEntity> result = Lists.newArrayList();
        for (ApartmentCard apartmentCard :
                apartmentCardStrategy.findByAddress("apartment", filter.getApartmentId(), 0,
                apartmentCardStrategy.countByAddress("apartment", filter.getApartmentId()))) {
            //apartment card
            final String apartmentCardNumber = String.valueOf(apartmentCard.getObjectId());

            //owner full name
            final String owner = personStrategy.displayDomainObject(apartmentCard.getOwner(), filter.getLocale());

            //ownership form
            final DomainObject ownershipFormObject = apartmentCard.getOwnershipForm();
            final String ownerhipForm = ownershipFormStrategy.displayDomainObject(ownershipFormObject, filter.getLocale());

            //registered
            final int registered = apartmentCard.getRegisteredCount();

            //organization
            final Set<Long> organizationIds = apartmentCard.getSubjectIds();
            if (organizationIds.size() > 1) {
                throw new IllegalStateException("Apartment card cannot belong to more one organizations.");
            }
            long organizationId = organizationIds.iterator().next();
            final DomainObject organization = organizationId > 0 ? organizationStrategy.getDomainObject(organizationId, true) : null;

            result.add(new ApartmentCardsGridEntity(apartmentCardNumber, apartmentCard.getObjectId(), ownerhipForm,
                    registered, organization, owner));
        }
        return result;
    }
}
