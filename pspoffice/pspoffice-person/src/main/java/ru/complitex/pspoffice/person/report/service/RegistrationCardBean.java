/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.service;

import org.apache.wicket.util.string.Strings;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.pspoffice.document.strategy.entity.Document;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.pspoffice.person.report.entity.RegistrationCard;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;

import static ru.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationCardBean extends AbstractBean {

    @EJB
    private PersonStrategy personStrategy;


    public RegistrationCard get(Registration registration, String addressEntity, long addressId) {
        RegistrationCard card = new RegistrationCard();
        Person person = registration.getPerson();
        card.setRegistration(registration);
        card.setNationality("");
        personStrategy.loadDocument(person);
        Document document = person.getDocument();
        if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT) {
            card.setPassportSeries(document.getSeries());
            card.setPassportNumber(document.getNumber());
            Date dateIssued = document.getDateIssued();
            String organizationIssued = document.getOrganizationIssued();
            String issued = "";
            if (!Strings.isEmpty(organizationIssued)) {
                issued += organizationIssued;
                if (dateIssued != null) {
                    issued += ", ";
                    issued += format(dateIssued);
                }
            }
            card.setPassportIssued(issued);
        }
        card.setAddressEntity(addressEntity);
        card.setAddressId(addressId);
        personStrategy.loadMilitaryServiceRelation(person);
        personStrategy.loadChildren(person);

        return card;
    }
}
