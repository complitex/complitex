package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Date;

public class FacilityServiceType extends AbstractAccountRequest<FacilityServiceTypeDBF> {
    public FacilityServiceType() {
        super(RequestFileType.FACILITY_SERVICE_TYPE);
    }

    public FacilityServiceType(String city, Date date) {
        super(RequestFileType.FACILITY_SERVICE_TYPE);

        setCity(city);
        setDate(date);
    }

    @Override
    public String getStreetCode() {
        return getStringField(FacilityServiceTypeDBF.CDUL);
    }

    @Override
    public String getBuildingCorp() {
        String corp = getUpStringField(FacilityServiceTypeDBF.BUILD, "_CYR");

        return corp.matches("0*") ? "" : corp;
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
