package org.complitex.pspoffice.person.report.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import static com.google.common.collect.Sets.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.DictionaryFwSession;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationStopCouponBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = RegistrationStopCouponBean.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private PersonNameBean personNameBean;
    @EJB
    private SessionBean sessionBean;
    @EJB
    private LocaleBean localeBean;

    @Transactional
    public RegistrationStopCoupon get(Registration registration, String addressEntity, long addressId,
            DictionaryFwSession session) {
        RegistrationStopCoupon coupon = new RegistrationStopCoupon();
        Person person = registration.getPerson();

        //name
        coupon.setRegistration(registration);
        coupon.setAddressEntity(addressEntity);
        coupon.setAddressId(addressId);
        coupon.setRegistrationOrganization(sessionBean.getMainUserOrganization(session));
        personStrategy.loadDocument(person);

        Document document = person.getDocument();
        if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT) {
            coupon.setPassportInfo(getDocumentInfo(document));
        } else if (document.getDocumentTypeId() == DocumentTypeStrategy.BIRTH_CERTIFICATE) {
            coupon.setBirthCertificateInfo(getDocumentInfo(document));
        }
        personStrategy.loadChildren(person);

        return coupon;
    }

    private String getDocumentInfo(Document document) {
        String info = document.getSeries() + " " + document.getNumber();
        Date dateIssued = document.getDateIssued();
        String organizationIssued = document.getOrganizationIssued();
        if (!Strings.isEmpty(organizationIssued)) {
            info += ", " + organizationIssued;
            if (dateIssued != null) {
                info += " " + format(dateIssued);
            }
        }
        return info;
    }

    @Transactional
    public String getPreviousNames(long personId, Locale locale) {
        final long localeId = localeBean.convert(locale).getId();
        List<Date> results = sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNameStartDates",
                ImmutableMap.of("personId", personId, "localeId", localeId));
        TreeSet<Date> previousNameStartDates = newTreeSet(results);
        if (previousNameStartDates.isEmpty()) {
            return null;
        }
        previousNameStartDates.remove(previousNameStartDates.last());
        StringBuilder previousNamesBuilder = new StringBuilder();
        int counter = 0;
        for (Date startDate : previousNameStartDates) {
            List<Attribute> nameAttributes = sqlSession().selectList(MAPPING_NAMESPACE + ".findPreviousNames",
                    ImmutableMap.<String, Object>of("personId", personId, "startDate", startDate, "localeId", localeId));

            //first name
            String firstName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.FIRST_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        firstName = personNameBean.findById(PersonNameType.FIRST_NAME, nameId).getName();
                        break;
                    }
                }
            }

            //last name
            String lastName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.LAST_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        lastName = personNameBean.findById(PersonNameType.LAST_NAME, nameId).getName();
                        break;
                    }
                }
            }

            //middle name
            String middleName = null;
            for (Attribute a : nameAttributes) {
                if (a.getAttributeTypeId().equals(PersonStrategy.MIDDLE_NAME)) {
                    Long nameId = a.getValueId();
                    if (nameId != null) {
                        middleName = personNameBean.findById(PersonNameType.MIDDLE_NAME, nameId).getName();
                        break;
                    }
                }
            }

            previousNamesBuilder.append(personStrategy.displayPerson(firstName, middleName, lastName)).
                    append(counter < previousNameStartDates.size() - 1 ? ", " : "");
            counter++;
        }
        return previousNamesBuilder.toString();
    }
}
