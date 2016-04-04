package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Date;

public class DwellingCharacteristics extends AbstractAccountRequest<DwellingCharacteristicsDBF> {
    public DwellingCharacteristics() {
    }

    public DwellingCharacteristics(String city, Date date) {
        setCity(city);
        setDate(date);
    }

    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.DWELLING_CHARACTERISTICS;
    }

    @Override
    public String getStreetCode() {
        return getStringField(DwellingCharacteristicsDBF.CDUL);
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(DwellingCharacteristicsDBF.HOUSE, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        return getUpStringField(DwellingCharacteristicsDBF.BUILD, "_CYR");
    }

    @Override
    public String getApartment() {
        return getUpStringField(DwellingCharacteristicsDBF.APT, "_CYR");
    }

    @Override
    public String getPassport() {
        return getUpStringField(DwellingCharacteristicsDBF.PASPPIL, "_CYR");
    }

    @Override
    public String getInn() {
        return getUpStringField(DwellingCharacteristicsDBF.IDPIL, "_CYR");
    }

    public String getFio(){
        return getStringField(DwellingCharacteristicsDBF.FIO);
    }
}
