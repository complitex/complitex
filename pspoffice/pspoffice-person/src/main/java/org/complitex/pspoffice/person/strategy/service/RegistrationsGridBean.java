/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.grid.RegistrationsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.RegistrationsGridFilter;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;

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
                final long personId = registration.getPerson().getId();

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
