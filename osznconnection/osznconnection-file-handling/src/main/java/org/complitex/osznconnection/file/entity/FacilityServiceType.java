package org.complitex.osznconnection.file.entity;

import java.util.Date;

public class FacilityServiceType extends AbstractAccountRequest<FacilityServiceTypeDBF> {
    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.FACILITY_SERVICE_TYPE;
    }

    public FacilityServiceType() {
    }

    public FacilityServiceType(String city, Date date) {
        setCity(city);
        setDate(date);
    }

    @Override
    public String getStreetCode() {
        return getUpStringField(FacilityServiceTypeDBF.CDUL, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        return getUpStringField(FacilityServiceTypeDBF.BUILD, "_CYR");
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(FacilityServiceTypeDBF.HOUSE, "_CYR");
    }

    @Override
    public String getApartment() {
        return getUpStringField(FacilityServiceTypeDBF.APT, "_CYR");
    }

    @Override
    public String getInn() {
        return getStringField(FacilityServiceTypeDBF.IDPIL);
    }

    @Override
    public String getPassport() {
        return getStringField(FacilityServiceTypeDBF.PASPPIL);
    }
}
