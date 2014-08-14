package org.complitex.pspoffice.person.report.download;

import java.util.Locale;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.strategy.PersonStrategy;

import static org.complitex.pspoffice.report.entity.FamilyAndApartmentInfoField.*;

public class FamilyAndApartmentInfoDownload extends AbstractReportDownload<FamilyAndApartmentInfo> {

    public FamilyAndApartmentInfoDownload(FamilyAndApartmentInfo report) {
        super("FamilyAndApartmentInfo", values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        FamilyAndApartmentInfo report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        OwnerRelationshipStrategy ownerRelationshipStrategy = EjbBeanLocator.getBean(OwnerRelationshipStrategy.class);

        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));

        int counter = 0;
        for (FamilyMember member : report.getFamilyMembers()) {
            switch (counter) {
                case 0: {
                    map.put(NAME0, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION0, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE0, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE0, member.getRegistrationDate());
                }
                break;
                case 1: {
                    map.put(NAME1, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION1, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE1, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE1, member.getRegistrationDate());
                }
                break;
                case 2: {
                    map.put(NAME2, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION2, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE2, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE2, member.getRegistrationDate());
                }
                break;
                case 3: {
                    map.put(NAME3, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION3, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE3, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE3, member.getRegistrationDate());
                }
                break;
                case 4: {
                    map.put(NAME4, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION4, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE4, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE4, member.getRegistrationDate());
                }
                break;
                case 5: {
                    map.put(NAME5, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION5, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE5, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE5, member.getRegistrationDate());
                }
                break;
                case 6: {
                    map.put(NAME6, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION6, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE6, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE6, member.getRegistrationDate());
                }
                break;
                case 7: {
                    map.put(NAME7, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION7, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE7, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE7, member.getRegistrationDate());
                }
                break;
            }
            counter++;
            if (counter > 7) {
                break;
            }
        }
        map.put(COUNT, String.valueOf(counter));

        map.put(ROOMS, report.getRooms());
        map.put(ROOMS_AREA, report.getRoomsArea());
        map.put(KITCHEN_AREA, report.getKitchenArea());
        map.put(BATHROOM_AREA, report.getBathroomArea());
        map.put(TOILET_AREA, report.getToiletArea());
        map.put(HALL_AREA, report.getHallArea());
        map.put(VERANDA_AREA, report.getVerandaArea());
        map.put(EMBEDDED_AREA, report.getEmbeddedArea());
        map.put(BALCONY_AREA, report.getBalconyArea());
        map.put(LOGGIA_AREA, report.getLoggiaArea());
        map.put(FULL_APARTMENT_AREA, report.getFullApartmentArea());
        map.put(STOREROOM_AREA, report.getStoreroomArea());
        map.put(BARN_AREA, report.getBarnArea());
        putMultilineValue(map, report.getAnotherBuildingsInfo(), 50, ANOTHER_BUILDINGS_INFO0, ANOTHER_BUILDINGS_INFO1);
        map.put(ADDITIONAL_INFORMATION, report.getAdditionalInformation());
        map.put(MAINTENANCE_YEAR, report.getMaintenanceYear());

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "FamilyAndApartmentInfo";
    }
}
