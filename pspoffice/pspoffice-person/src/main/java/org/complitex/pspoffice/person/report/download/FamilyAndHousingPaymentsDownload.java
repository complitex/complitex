package org.complitex.pspoffice.person.report.download;

import java.util.Locale;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.strategy.PersonStrategy;

import static org.complitex.pspoffice.report.entity.FamilyAndHousingPaymentsField.*;

public class FamilyAndHousingPaymentsDownload extends AbstractReportDownload<FamilyAndHousingPayments> {

    public FamilyAndHousingPaymentsDownload(FamilyAndHousingPayments report) {
        super("FamilyAndHousingPayments", values(), report);
    }

    @Override
    public Map<IReportField, Object> getValues(Locale locale) {
        FamilyAndHousingPayments report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        PersonStrategy personStrategy = EjbBeanLocator.getBean(PersonStrategy.class);
        AddressRendererBean addressRendererBean = EjbBeanLocator.getBean(AddressRendererBean.class);
        OwnershipFormStrategy ownershipFormStrategy = EjbBeanLocator.getBean(OwnershipFormStrategy.class);
        OwnerRelationshipStrategy ownerRelationshipStrategy = EjbBeanLocator.getBean(OwnerRelationshipStrategy.class);

        map.put(NAME, personStrategy.displayDomainObject(report.getOwner(), locale));
        map.put(PERSONAL_ACCOUNT, report.getPersonalAccount());
        map.put(ADDRESS, addressRendererBean.displayAddress(report.getAddressEntity(), report.getAddressId(), locale));
        map.put(FORM_OF_OWNERSHIP, ownershipFormStrategy.displayDomainObject(report.getOwnershipForm(), locale));
        map.put(STOVE_TYPE, report.getStoveType());

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

        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(HEATED_AREA, report.getHeatedArea());
        map.put(NORMATIVE_AREA, report.getNormativeArea());
        map.put(ROOMS, report.getRooms());
        map.put(BENEFITS, report.getBenefits());
        map.put(PAYMENTS_ADJUSTED_FOR_BENEFITS, report.getPaymentsAdjustedForBenefits());
        map.put(NORMATIVE_PAYMENTS, report.getNormativePayments());
        map.put(APARTMENT_PAYMENTS, report.getApartmentPayments());
        map.put(HEAT_PAYMENTS, report.getHeatPayments());
        map.put(GAS_PAYMENTS, report.getGasPayments());
        map.put(COLD_WATER_PAYMENTS, report.getColdWaterPayments());
        map.put(HOT_WATER_PAYMENTS, report.getHotWaterPayments());
        map.put(DEBT, report.getDebt());
        map.put(DEBT_MONTH, report.getDebtMonth());

        return map;
    }

    @Override
    public String getFileName(Locale locale) {
        return "FamilyAndHousingPayments";
    }
}
