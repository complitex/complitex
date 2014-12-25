package org.complitex.pspoffice.person.report.download;

import org.complitex.address.service.AddressRendererBean;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.util.ResourceUtil;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.person.report.entity.RegistrationCard;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.entity.RegistrationCardField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Locale;
import java.util.Map;

import static org.complitex.pspoffice.report.entity.RegistrationCardField.*;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

public class RegistrationCardDownload extends AbstractReportDownload<RegistrationCard> {

    private static final String RESOURCE_BUNDLE = RegistrationCardDownload.class.getName();

    public RegistrationCardDownload(RegistrationCard report) {
        super("RegistrationCard", RegistrationCardField.values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        RegistrationCard report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        RegistrationTypeStrategy registrationTypeStrategy = EjbBeanLocator.getBean(RegistrationTypeStrategy.class);
        MilitaryServiceRelationStrategy militaryServiceRelationStrategy = EjbBeanLocator.getBean(MilitaryServiceRelationStrategy.class);
        final Locale systemLocale = EjbBeanLocator.getBean(StringLocaleBean.class).getSystemLocale();

        Registration registration = report.getRegistration();
        Person person = registration.getPerson();
        map.put(FIRST_NAME, person.getFirstName(locale, systemLocale));
        map.put(LAST_NAME, person.getLastName(locale, systemLocale));
        map.put(MIDDLE_NAME, person.getMiddleName(locale, systemLocale));
        map.put(NATIONALITY, report.getNationality());
        map.put(BIRTH_DATE, person.getBirthDate());
        map.put(BIRTH_REGION, person.getBirthRegion());
        map.put(BIRTH_DISTRICT, person.getBirthDistrict());
        map.put(BIRTH_CITY, person.getBirthCity());
        map.put(ARRIVAL_REGION, registration.getArrivalRegion());
        map.put(ARRIVAL_DISTRICT, registration.getArrivalDistrict());
        map.put(ARRIVAL_CITY, registration.getArrivalCity());
        map.put(ARRIVAL_DATE, registration.getArrivalDate());
        map.put(ARRIVAL_STREET, registration.getArrivalStreet());
        map.put(ARRIVAL_BUILDING, registration.getArrivalBuildingNumber());
        map.put(ARRIVAL_CORP, registration.getArrivalBuildingCorp());
        map.put(ARRIVAL_APARTMENT, registration.getArrivalApartment());
        map.put(PASSPORT_SERIES, report.getPassportSeries());
        map.put(PASSPORT_NUMBER, report.getPassportNumber());
        map.put(PASSPORT_ISSUED, report.getPassportIssued());
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));

        int counter = 0;
        for (Person child : person.getChildren()) {
            String childrenBirthDateSuffix = ResourceUtil.getString(RESOURCE_BUNDLE, "children_birth_date_suffix", locale);
            switch (counter) {
                case 0: {
                    map.put(CHILD0, personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + childrenBirthDateSuffix);
                }
                break;
                case 1: {
                    map.put(CHILD1, personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + childrenBirthDateSuffix);
                }
                break;
                case 2: {
                    map.put(CHILD2, personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + childrenBirthDateSuffix);
                }
                break;
            }
            counter++;
            if (counter > 2) {
                break;
            }
        }
        final DomainObject militaryServiceRelation = person.getMilitaryServiceRelation();
        putMultilineValue(map, militaryServiceRelation != null
                ? militaryServiceRelationStrategy.displayDomainObject(militaryServiceRelation, locale) : null,
                50, MILITARY0, MILITARY1, MILITARY2);

        map.put(REGISTRATION_DATE, registration.getRegistrationDate());
        map.put(REGISTRATION_TYPE, registrationTypeStrategy.displayDomainObject(registration.getRegistrationType(), locale));
        map.put(DEPARTURE_REGION, registration.getDepartureRegion());
        map.put(DEPARTURE_DISTRICT, registration.getDepartureDistrict());
        map.put(DEPARTURE_CITY, registration.getDepartureCity());
        map.put(DEPARTURE_DATE, registration.getDepartureDate());
        map.put(DEPARTURE_REASON, registration.getDepartureReason());

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "RegistrationCard";
    }
}
