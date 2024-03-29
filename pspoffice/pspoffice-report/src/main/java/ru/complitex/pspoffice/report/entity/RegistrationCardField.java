package ru.complitex.pspoffice.report.entity;

public enum RegistrationCardField implements IReportField {
    FIRST_NAME, LAST_NAME, MIDDLE_NAME, NATIONALITY,
    BIRTH_DATE, BIRTH_REGION, BIRTH_DISTRICT, BIRTH_CITY,
    ARRIVAL_REGION, ARRIVAL_DISTRICT, ARRIVAL_CITY, ARRIVAL_DATE,
    ARRIVAL_STREET, ARRIVAL_BUILDING, ARRIVAL_CORP, ARRIVAL_APARTMENT,
    PASSPORT_SERIES, PASSPORT_NUMBER, PASSPORT_ISSUED,
    ADDRESS,
    CHILD0, CHILD1, CHILD2, MILITARY0, MILITARY1, MILITARY2,
    REGISTRATION_DATE, REGISTRATION_TYPE,
    DEPARTURE_REGION, DEPARTURE_DISTRICT, DEPARTURE_CITY, DEPARTURE_DATE, DEPARTURE_REASON;

    @Override
    public String getFieldName() {
        return name().toLowerCase();
    }
}
