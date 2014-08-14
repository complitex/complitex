/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.importing.legacy.entity.ApartmentCardCorrection;
import org.complitex.pspoffice.importing.legacy.entity.PersonCorrection;
import org.complitex.pspoffice.importing.legacy.entity.LegacyDataImportFile;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = RegistrationCorrectionBean.class.getName();
    public static final String REGISTRATION_FILE_NAME = "peop_registration.csv";
    public static final String REGISTRATION_FILE_HEADER = LegacyDataImportFile.PERSON.getCsvHeader();
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public void updateApartmentCard(ApartmentCardCorrection apartmentCardCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".updateApartmentCard", apartmentCardCorrection);
    }

    public void updatePerson(PersonCorrection personCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".updatePerson", personCorrection);
    }

    public int countForProcessing() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing");
    }

    public List<ApartmentCardCorrection> findApartmentCardsForProcessing(int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findApartmentCardsForProcessing", size);
    }

    public static boolean isRegistrationDateValid(String registrationDate) {
        return DateUtil.asDate(registrationDate, Utils.DATE_PATTERN) != null;
    }

    public Registration newRegistration(PersonCorrection p, boolean isFinished, long personId, long ownerRelationshipId,
            long registrationTypeId, Date registrationDate,
            Date arrivalDate, String arrivalStreet,
            Date departureDate, String departureStreet, String departureReason) {
        Registration r = registrationStrategy.newInstance();

        //person
        r.setPerson(personStrategy.findById(personId, true, false, false, false, false));
        r.getAttribute(RegistrationStrategy.PERSON).setValueId(personId);

        //owner relationship
        r.getAttribute(RegistrationStrategy.OWNER_RELATIONSHIP).setValueId(ownerRelationshipId);

        //registration date
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.REGISTRATION_DATE),
                new DateConverter().toString(registrationDate));

        //registration type
        r.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(registrationTypeId);

        //arrival
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_COUNTRY), p.getPkra());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_REGION), p.getPobl());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_DISTRICT), p.getPrayon());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_CITY), p.getPmisto());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_STREET), arrivalStreet);
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_NUMBER), p.getPbud());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_CORP), p.getPkorp());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_APARTMENT), p.getPkv());
        Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.ARRIVAL_DATE), new DateConverter().toString(arrivalDate));

        if (isFinished) {
            //departure
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_COUNTRY), p.getVkra());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_REGION), p.getVobl());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_DISTRICT), p.getVrayon());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_CITY), p.getVmisto());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_STREET), departureStreet);
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_NUMBER), p.getVbud());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_CORP), p.getVkorp());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_APARTMENT), p.getVkv());
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_DATE),
                    new DateConverter().toString(departureDate));
            Utils.setSystemLocaleValue(r.getAttribute(RegistrationStrategy.DEPARTURE_REASON), departureReason);
        }

        return r;
    }

    public long addRegistration(ApartmentCard apartmentCard, Registration registration, Date createDate) {
        apartmentCardStrategy.addRegistration(apartmentCard, registration, createDate);
        return registration.getId();
    }
}
