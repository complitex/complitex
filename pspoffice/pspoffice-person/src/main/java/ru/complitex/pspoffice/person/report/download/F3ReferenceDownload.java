/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.report.download;

import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.report.entity.F3Reference;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.NeighbourFamily;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.report.entity.IReportField;
import ru.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Locale;
import java.util.Map;

import static ru.complitex.pspoffice.report.entity.F3ReferenceField.ADDRESS;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.APARTMENT_AREA;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.BIRTH_DATE7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.COUNT;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FACILITIES;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY8;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY9;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA8;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_AREA9;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS8;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_ROOMS9;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE8;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FAMILY_SIZE9;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FLOOR;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FLOORS;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.FORM_OWNERSHIP;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.LIVING_AREA;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.NAME7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.PERSONAL_ACCOUNT_OWNER;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.REGISTRATION_DATE7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION0;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION1;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION2;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION3;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION4;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION5;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION6;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.RELATION7;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.ROOMS;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.TAKES_ROOMS;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.TECHNICAL_STATE;
import static ru.complitex.pspoffice.report.entity.F3ReferenceField.values;

/**
 *
 * @author Artem
 */
public class F3ReferenceDownload extends AbstractReportDownload<F3Reference> {

    public F3ReferenceDownload(F3Reference report) {
        super("F3Reference", values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        F3Reference report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        OwnershipFormStrategy ownershipFormStrategy = EjbBeanLocator.getBean(OwnershipFormStrategy.class);
        OwnerRelationshipStrategy ownerRelationshipStrategy = EjbBeanLocator.getBean(OwnerRelationshipStrategy.class);

        map.put(NAME, personStrategy.displayDomainObject(report.getPerson(), locale));
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));
        map.put(LIVING_AREA, report.getLivingArea());
        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(TAKES_ROOMS, report.getTakesRooms());
        map.put(ROOMS, report.getRooms());
        map.put(FLOOR, report.getFloor());
        map.put(FLOORS, report.getFloors());
        map.put(PERSONAL_ACCOUNT_OWNER, personStrategy.displayDomainObject(report.getPersonalAccountOwner(), locale));
        map.put(FORM_OWNERSHIP, ownershipFormStrategy.displayDomainObject(report.getOwnershipForm(), locale));
        map.put(FACILITIES, report.getFacilities());
        map.put(TECHNICAL_STATE, report.getTechnicalState());

        int counter = 0;
        for (FamilyMember member : report.getFamilyMembers()) {
            switch (counter) {
                case 0: {
                    map.put(NAME0, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION0, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE0, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE0, member.getRegistrationDate());
                }
                break;
                case 1: {
                    map.put(NAME1, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION1, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE1, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE1, member.getRegistrationDate());
                }
                break;
                case 2: {
                    map.put(NAME2, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION2, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE2, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE2, member.getRegistrationDate());
                }
                break;
                case 3: {
                    map.put(NAME3, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION3, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE3, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE3, member.getRegistrationDate());
                }
                break;
                case 4: {
                    map.put(NAME4, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION4, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE4, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE4, member.getRegistrationDate());
                }
                break;
                case 5: {
                    map.put(NAME5, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION5, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE5, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE5, member.getRegistrationDate());
                }
                break;
                case 6: {
                    map.put(NAME6, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION6, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
                    map.put(BIRTH_DATE6, member.getPerson().getBirthDate());
                    map.put(REGISTRATION_DATE6, member.getRegistrationDate());
                }
                break;
                case 7: {
                    map.put(NAME7, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION7, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) : "");
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

        counter = 0;
        for (NeighbourFamily family : report.getNeighbourFamilies()) {
            switch (counter) {
                case 0: {
                    map.put(FAMILY0, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE0, family.getAmount());
                    map.put(FAMILY_ROOMS0, family.getTakeRooms());
                    map.put(FAMILY_AREA0, family.getTakeArea());
                }
                break;
                case 1: {
                    map.put(FAMILY1, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE1, family.getAmount());
                    map.put(FAMILY_ROOMS1, family.getTakeRooms());
                    map.put(FAMILY_AREA1, family.getTakeArea());
                }
                break;
                case 2: {
                    map.put(FAMILY2, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE2, family.getAmount());
                    map.put(FAMILY_ROOMS2, family.getTakeRooms());
                    map.put(FAMILY_AREA2, family.getTakeArea());
                }
                break;
                case 3: {
                    map.put(FAMILY3, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE3, family.getAmount());
                    map.put(FAMILY_ROOMS3, family.getTakeRooms());
                    map.put(FAMILY_AREA3, family.getTakeArea());
                }
                break;
                case 4: {
                    map.put(FAMILY4, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE4, family.getAmount());
                    map.put(FAMILY_ROOMS4, family.getTakeRooms());
                    map.put(FAMILY_AREA4, family.getTakeArea());
                }
                break;
                case 5: {
                    map.put(FAMILY5, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE5, family.getAmount());
                    map.put(FAMILY_ROOMS5, family.getTakeRooms());
                    map.put(FAMILY_AREA5, family.getTakeArea());
                }
                break;
                case 6: {
                    map.put(FAMILY6, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE6, family.getAmount());
                    map.put(FAMILY_ROOMS6, family.getTakeRooms());
                    map.put(FAMILY_AREA6, family.getTakeArea());
                }
                break;
                case 7: {
                    map.put(FAMILY7, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE7, family.getAmount());
                    map.put(FAMILY_ROOMS7, family.getTakeRooms());
                    map.put(FAMILY_AREA7, family.getTakeArea());
                }
                break;
                case 8: {
                    map.put(FAMILY8, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE8, family.getAmount());
                    map.put(FAMILY_ROOMS8, family.getTakeRooms());
                    map.put(FAMILY_AREA8, family.getTakeArea());
                }
                break;
                case 9: {
                    map.put(FAMILY9, personStrategy.displayDomainObject(family.getPerson(), locale));
                    map.put(FAMILY_SIZE9, family.getAmount());
                    map.put(FAMILY_ROOMS9, family.getTakeRooms());
                    map.put(FAMILY_AREA9, family.getTakeArea());
                }
                break;
            }
            counter++;
            if (counter > 9) {
                break;
            }
        }

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "F3Reference";
    }
}
