package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Date;

public class DwellingCharacteristics extends AbstractAccountRequest<DwellingCharacteristicsDBF> {
    public DwellingCharacteristics() {
        super(RequestFileType.DWELLING_CHARACTERISTICS);
    }

    public DwellingCharacteristics(String city, Date date) {
        super(RequestFileType.DWELLING_CHARACTERISTICS);

        setCity(city);
        setDate(date);
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
        String corp = getUpStringField(DwellingCharacteristicsDBF.BUILD, "_CYR");

        return corp.matches("0*") ? "" : corp;
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
        return getStringField(DwellingCharacteristicsDBF.FIO, "_CYR");
    }
}
