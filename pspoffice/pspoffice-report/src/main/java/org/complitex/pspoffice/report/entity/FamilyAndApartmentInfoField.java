package org.complitex.pspoffice.report.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.06.11 16:56
 */
public enum FamilyAndApartmentInfoField implements IReportField{
    ADDRESS,
    NAME0, RELATION0, BIRTH_DATE0, REGISTRATION_DATE0,
    NAME1, RELATION1, BIRTH_DATE1, REGISTRATION_DATE1,
    NAME2, RELATION2, BIRTH_DATE2, REGISTRATION_DATE2,
    NAME3, RELATION3, BIRTH_DATE3, REGISTRATION_DATE3,
    NAME4, RELATION4, BIRTH_DATE4, REGISTRATION_DATE4,
    NAME5, RELATION5, BIRTH_DATE5, REGISTRATION_DATE5,
    NAME6, RELATION6, BIRTH_DATE6, REGISTRATION_DATE6,
    NAME7, RELATION7, BIRTH_DATE7, REGISTRATION_DATE7,
    COUNT,
    ROOMS, ROOMS_AREA, KITCHEN_AREA, BATHROOM_AREA,
    TOILET_AREA, HALL_AREA, VERANDA_AREA, EMBEDDED_AREA,
    BALCONY_AREA, LOGGIA_AREA, FULL_APARTMENT_AREA,
    STOREROOM_AREA, BARN_AREA, ANOTHER_BUILDINGS_INFO0,
    ANOTHER_BUILDINGS_INFO1, ADDITIONAL_INFORMATION, MAINTENANCE_YEAR;

    @Override
    public String getFieldName() {
        return name().toLowerCase();
    }
}
