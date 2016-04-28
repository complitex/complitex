package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

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
        return getUpStringField(FacilityServiceTypeDBF.IDPIL, "_CYR");
    }

    @Override
    public String getPassport() {
        return getUpStringField(FacilityServiceTypeDBF.PASPPIL, "_CYR");
    }

    public String getFio(){
        return getStringField(FacilityServiceTypeDBF.FIO, "_CYR");
    }
}
