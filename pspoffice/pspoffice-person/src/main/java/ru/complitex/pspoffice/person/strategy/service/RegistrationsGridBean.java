/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.Lists;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.Registration;
import ru.complitex.pspoffice.person.strategy.entity.grid.RegistrationsGridEntity;
import ru.complitex.pspoffice.person.strategy.entity.grid.RegistrationsGridFilter;
import ru.complitex.pspoffice.person.util.PersonDateFormatter;
import ru.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationsGridBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;

    public RegistrationsGridFilter newFilter(ApartmentCard apartmentCard, Locale locale) {
        return new RegistrationsGridFilter(apartmentCard, locale);
    }

    public List<RegistrationsGridEntity> find(RegistrationsGridFilter filter) {
        final List<RegistrationsGridEntity> result = Lists.newArrayList();

        for (Registration registration : filter.getApartmentCard().getRegistrations()) {
            if (!registration.isFinished()) {
                //person name
                final String personName = personStrategy.displayDomainObject(registration.getPerson(), filter.getLocale());

                //person id
                final long personId = registration.getPerson().getObjectId();

                //person birth date
                final String personBirthDate = registration.getPerson().getBirthDate() != null
                        ? PersonDateFormatter.format(registration.getPerson().getBirthDate()) : null;

                //registration date
                final String registrationDate = registration.getRegistrationDate() != null
                        ? PersonDateFormatter.format(registration.getRegistrationDate()) : null;

                //registration type
                final String registrationType = registration.getRegistrationType() != null
                        ? registrationTypeStrategy.displayDomainObject(registration.getRegistrationType(), filter.getLocale())
                        : null;

                //owner relationship
                final String ownerRelationship = registration.getOwnerRelationship() != null
                        ? ownerRelationshipStrategy.displayDomainObject(registration.getOwnerRelationship(), filter.getLocale())
                        : null;

                result.add(new RegistrationsGridEntity(personName, personId, personBirthDate, registrationDate,
                        registrationType, ownerRelationship));
            }
        }
        return result;
    }
}
