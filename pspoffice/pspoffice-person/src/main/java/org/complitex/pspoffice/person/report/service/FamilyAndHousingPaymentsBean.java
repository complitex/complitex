/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.person.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class FamilyAndHousingPaymentsBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;

    @Transactional
    public FamilyAndHousingPayments get(ApartmentCard apartmentCard) {
        FamilyAndHousingPayments payments = new FamilyAndHousingPayments();
        payments.setAddressEntity(ApartmentCardStrategy.getAddressEntity(apartmentCard));
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
                if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT) {
                    member.setPassport(documentStrategy.displayDomainObject(document, null));
                }
                member.setRelation(registration.getOwnerRelationship());
                payments.addFamilyMember(member);
            }
        }
        return payments;
    }
}
