/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.document.strategy.DocumentStrategy;
import ru.complitex.pspoffice.document.strategy.entity.Document;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.HousingPayments;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Artem
 */
@Stateless
public class HousingPaymentsBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private DocumentStrategy documentStrategy;

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public HousingPayments get(ApartmentCard apartmentCard) {
        HousingPayments payments = new HousingPayments();
        payments.setAddressEntity(apartmentCardStrategy.getAddressEntity(apartmentCard));
        payments.setAddressId(apartmentCard.getAddressId());
        payments.setOwner(apartmentCard.getOwner());
        payments.setPersonalAccount("");
        payments.setOwnershipForm(apartmentCard.getOwnershipForm());

        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                FamilyMember member = new FamilyMember();
                Person person = registration.getPerson();
                member.setPerson(person);
                personStrategy.loadDocument(person);
                Document document = person.getDocument();
                if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT
                        || document.getDocumentTypeId() == DocumentTypeStrategy.BIRTH_CERTIFICATE) {
                    member.setPassport(documentStrategy.displayDomainObject(document, null));
                }
                member.setRelation(registration.getOwnerRelationship());
                payments.addFamilyMember(member);
            }
        }

        return payments;
    }
}
