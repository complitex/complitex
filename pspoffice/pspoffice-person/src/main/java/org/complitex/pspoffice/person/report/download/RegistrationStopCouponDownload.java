package org.complitex.pspoffice.person.report.download;

import org.complitex.address.service.AddressRendererBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.util.ResourceUtil;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.report.service.RegistrationStopCouponBean;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.entity.RegistrationStopCouponField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.complitex.pspoffice.report.entity.RegistrationStopCouponField.*;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;

public class RegistrationStopCouponDownload extends AbstractReportDownload<RegistrationStopCoupon> {

    private static final String RESOURCE_BUNDLE = RegistrationStopCouponDownload.class.getName();

    public RegistrationStopCouponDownload(RegistrationStopCoupon report) {
        super("RegistrationStopCoupon", RegistrationStopCouponField.values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        RegistrationStopCoupon report = getReport();
        Map<IReportField, Object> map = new HashMap<IReportField, Object>();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        RegistrationStopCouponBean registrationStopCouponBean = EjbBeanLocator.getBean(RegistrationStopCouponBean.class);
        IStrategy organizationStrategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("organization");
        final Locale systemLocale = EjbBeanLocator.getBean(StringLocaleBean.class).getSystemLocale();

        Registration registration = report.getRegistration();
        Person person = registration.getPerson();
        map.put(FIRST_NAME, person.getFirstName(locale, systemLocale));
        map.put(LAST_NAME, person.getLastName(locale, systemLocale));
        map.put(MIDDLE_NAME, person.getMiddleName(locale, systemLocale));
        putMultilineValue(map, registrationStopCouponBean.getPreviousNames(person.getObjectId(), locale), 50,
                PREVIOUS_NAMES0, PREVIOUS_NAMES1);
        map.put(BIRTH_DATE, person.getBirthDate());
        map.put(BIRTH_COUNTRY, person.getBirthCountry());
        map.put(BIRTH_REGION, person.getBirthRegion());
        map.put(BIRTH_DISTRICT, person.getBirthDistrict());
        map.put(BIRTH_CITY, person.getBirthCity());
        map.put(GENDER, person.getGender() != null ? ResourceUtil.getString(RESOURCE_BUNDLE, person.getGender().name(), locale) : "");
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));
        putMultilineValue(map,
                report.getRegistrationOrganization() != null
                ? organizationStrategy.displayDomainObject(report.getRegistrationOrganization(), locale) : null,
                50, REGISTRATION_ORGANIZATION0, REGISTRATION_ORGANIZATION1);
        map.put(DEPARTURE_COUNTRY, registration.getDepartureCountry());
        map.put(DEPARTURE_REGION, registration.getDepartureRegion());
        map.put(DEPARTURE_DISTRICT, registration.getDepartureDistrict());
        map.put(DEPARTURE_CITY, registration.getDepartureCity());
        map.put(DEPARTURE_DATE, registration.getDepartureDate());
        putMultilineValue(map, report.getPassportInfo(), 50, PASSPORT0, PASSPORT1, PASSPORT2);
        putMultilineValue(map, report.getBirthCertificateInfo(), 50, BIRTH_CERTIFICATE0, BIRTH_CERTIFICATE1);

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
                case 3: {
                    map.put(CHILD3, personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + childrenBirthDateSuffix);
                }
                break;
                case 4: {
                    map.put(CHILD4, personStrategy.displayDomainObject(child, locale) + ", "
                            + format(child.getBirthDate()) + childrenBirthDateSuffix);
                }
                break;
            }
            counter++;
            if (counter > 4) {
                break;
            }
        }
        putMultilineValue(map, report.getAdditionalInfo(), 50, ADDITIONAL_INFO0, ADDITIONAL_INFO1, ADDITIONAL_INFO2, ADDITIONAL_INFO3);

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "RegistrationStopCoupon";
    }
}
