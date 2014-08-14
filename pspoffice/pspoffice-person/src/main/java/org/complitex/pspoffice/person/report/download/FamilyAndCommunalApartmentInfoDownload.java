package org.complitex.pspoffice.person.report.download;

import java.util.Locale;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.strategy.PersonStrategy;

import static org.complitex.pspoffice.report.entity.FamilyAndCommunalApartmentInfoField.*;

public class FamilyAndCommunalApartmentInfoDownload extends AbstractReportDownload<FamilyAndCommunalApartmentInfo> {

    public FamilyAndCommunalApartmentInfoDownload(FamilyAndCommunalApartmentInfo report) {
        super("FamilyAndCommunalApartmentInfo", values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        FamilyAndCommunalApartmentInfo report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        OwnerRelationshipStrategy ownerRelationshipStrategy = EjbBeanLocator.getBean(OwnerRelationshipStrategy.class);
        IStrategy apartmentStrategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("apartment");

        map.put(OWNER_COUNT1, report.getNeighbourFamilies().size());
        map.put(OWNER_COUNT2, report.getNeighbourFamilies().size());
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));

        int counter = 0;
        for (NeighbourFamily family : report.getNeighbourFamilies()) {
            switch (counter) {
                case 0: {
                    map.put(APARTMENT0, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME0, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO0, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO0, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO0, family.getLoggiaAndAreaInfo());
                }
                break;
                case 1: {
                    map.put(APARTMENT1, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME1, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO1, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO1, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO1, family.getLoggiaAndAreaInfo());
                }
                break;
                case 2: {
                    map.put(APARTMENT2, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME2, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO2, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO2, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO2, family.getLoggiaAndAreaInfo());
                }
                break;
                case 3: {
                    map.put(APARTMENT3, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME3, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO3, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO3, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO3, family.getLoggiaAndAreaInfo());
                }
                break;
                case 4: {
                    map.put(APARTMENT4, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME4, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO4, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO4, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO4, family.getLoggiaAndAreaInfo());
                }
                break;
                case 5: {
                    map.put(APARTMENT5, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME5, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO5, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO5, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO5, family.getLoggiaAndAreaInfo());
                }
                break;
                case 6: {
                    map.put(APARTMENT6, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME6, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO6, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO6, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO6, family.getLoggiaAndAreaInfo());
                }
                break;
                case 7: {
                    map.put(APARTMENT7, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME7, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO7, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO7, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO7, family.getLoggiaAndAreaInfo());
                }
                break;
                case 8: {
                    map.put(APARTMENT8, family.getApartment() != null
                            ? apartmentStrategy.displayDomainObject(family.getApartment(), locale) : "");
                    map.put(OWNER_NAME8, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(ROOMS_AND_AREA_INFO8, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO8, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO8, family.getLoggiaAndAreaInfo());
                }
                break;
            }
            counter++;
            if (counter > 8) {
                break;
            }
        }
        map.put(KITCHEN_AREA, report.getKitchenArea());
        map.put(BATHROOM_AREA, report.getBathroomArea());
        map.put(TOILET_AREA, report.getToiletArea());
        map.put(HALL_AREA, report.getHallArea());
        map.put(OTHER_SPACE_INFO, report.getOtherSpaceInfo());
        map.put(SHARED_AREA, report.getSharedArea());
        map.put(FLOOR, report.getFloor());
        map.put(NUMBER_OF_STOREYS, report.getNumberOfStoreys());

        String ownerName = personStrategy.displayDomainObject(report.getOwner(), locale);
        map.put(OWNER_NAME_1, ownerName);
        map.put(OWNER_NAME_2, ownerName);

        counter = 0;
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
            }
            counter++;
            if (counter > 5) {
                break;
            }
        }

        map.put(STOREROOM_AREA, report.getStoreroomArea());
        map.put(BARN_AREA, report.getBarnArea());
        putMultilineValue(map, report.getOtherBuildings(), 50, OTHER_BUILDINGS0, OTHER_BUILDINGS1);

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "FamilyAndCommunalApartmentInfo";
    }
}
