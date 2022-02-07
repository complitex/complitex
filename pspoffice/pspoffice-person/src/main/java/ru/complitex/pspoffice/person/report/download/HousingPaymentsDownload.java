package ru.complitex.pspoffice.person.report.download;

import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.report.entity.FamilyMember;
import ru.complitex.pspoffice.person.report.entity.HousingPayments;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.report.entity.IReportField;
import ru.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Locale;
import java.util.Map;

import static ru.complitex.pspoffice.report.entity.HousingPaymentsField.*;

public class HousingPaymentsDownload extends AbstractReportDownload<HousingPayments> {

    public HousingPaymentsDownload(HousingPayments report) {
        super("HousingPayments", values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        HousingPayments report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        OwnershipFormStrategy ownershipFormStrategy = EjbBeanLocator.getBean(OwnershipFormStrategy.class);
        OwnerRelationshipStrategy ownerRelationshipStrategy = EjbBeanLocator.getBean(OwnerRelationshipStrategy.class);

        map.put(NAME, personStrategy.displayDomainObject(report.getOwner(), locale));
        map.put(PERSONAL_ACCOUNT, report.getPersonalAccount());
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));
        map.put(FORM_OF_OWNERSHIP, ownershipFormStrategy.displayDomainObject(report.getOwnershipForm(), locale));

        int counter = 0;
        for (FamilyMember member : report.getFamilyMembers()) {
            switch (counter) {
                case 0: {
                    map.put(NAME0, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION0, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE0, member.getPerson().getBirthDate());
                    map.put(PASSPORT0, member.getPassport());
                }
                break;
                case 1: {
                    map.put(NAME1, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION1, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE1, member.getPerson().getBirthDate());
                    map.put(PASSPORT1, member.getPassport());
                }
                break;
                case 2: {
                    map.put(NAME2, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION2, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE2, member.getPerson().getBirthDate());
                    map.put(PASSPORT2, member.getPassport());
                }
                break;
                case 3: {
                    map.put(NAME3, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION3, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE3, member.getPerson().getBirthDate());
                    map.put(PASSPORT3, member.getPassport());
                }
                break;
                case 4: {
                    map.put(NAME4, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION4, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE4, member.getPerson().getBirthDate());
                    map.put(PASSPORT4, member.getPassport());
                }
                break;
                case 5: {
                    map.put(NAME5, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION5, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE5, member.getPerson().getBirthDate());
                    map.put(PASSPORT5, member.getPassport());
                }
                break;
                case 6: {
                    map.put(NAME6, personStrategy.displayDomainObject(member.getPerson(), locale));
                    map.put(RELATION6, member.getRelation() != null ? ownerRelationshipStrategy.displayDomainObject(member.getRelation(), locale) :  "");
                    map.put(BIRTH_DATE6, member.getPerson().getBirthDate());
                    map.put(PASSPORT6, member.getPassport());
                }
                break;
            }
            counter++;
            if (counter > 6) {
                break;
            }
        }
        map.put(COUNT, String.valueOf(counter));

        map.put(FLOORS, report.getFloors());
        map.put(LIFT, report.getLift());
        map.put(HOSTEL, report.getHostel());
        map.put(FLOOR, report.getFloor());
        map.put(STOVE_TYPE, report.getStoveType());
        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(BALCONY_AREA, report.getBalconyArea());
        map.put(BENEFIT_PERSONS, report.getBenefitPersons());
        map.put(NORMATIVE_AREA, report.getNormativeArea());
        map.put(ROOMS, report.getRooms());
        map.put(BENEFITS, report.getBenefits());
        map.put(PAYMENTS_ADJUSTED_FOR_BENEFITS, report.getPaymentsAdjustedForBenefits());
        map.put(NORMATIVE_PAYMENTS, report.getNormativePayments());
        map.put(APARTMENT_PAYMENTS, report.getApartmentPayments());
        map.put(APARTMENT_TARIFF, report.getApartmentTariff());
        map.put(HEAT_PAYMENTS, report.getHeatPayments());
        map.put(HEAT_TARIFF, report.getHeatTariff());
        map.put(GAS_PAYMENTS, report.getGasPayments());
        map.put(GAS_TARIFF, report.getGasTariff());
        map.put(COLD_WATER_PAYMENTS, report.getColdWaterPayments());
        map.put(COLD_WATER_TARIFF, report.getColdWaterTariff());
        map.put(HOT_WATER_PAYMENTS, report.getHotWaterPayments());
        map.put(HOT_WATER_TARIFF, report.getHotWaterTariff());
        map.put(OUTLET_PAYMENTS, report.getOutletPayments());
        map.put(OUTLET_TARIFF, report.getOutletTariff());
        map.put(DEBT, report.getDebt());
        map.put(DEBT_MONTH, report.getDebtMonth());

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "HousingPayments";
    }
}
