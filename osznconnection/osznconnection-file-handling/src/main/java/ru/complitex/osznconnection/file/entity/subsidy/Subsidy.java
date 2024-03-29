package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.address.util.AddressRenderer;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Subsidy extends AbstractAccountRequest<SubsidyDBF> {
    public final static List<String> CYR = Collections.unmodifiableList(Arrays.asList(
            "FIO", "NP_NAME", "CAT_V", "NAME_V", "BLD", "CORP", "FLAT"
    ));

    public Subsidy() {
        super(RequestFileType.SUBSIDY);
    }

    public String getAddress(Locale locale){
        return AddressRenderer.displayAddress(getStreetType(), getStreet(), getBuildingNumber(), getBuildingCorp(),
                getApartment(), locale);
    }

    public String getFio(){
        return getUpStringField(SubsidyDBF.FIO, "_CYR");
    }

    @Override
    public String getCity() {
        return super.getCity() == null ? getUpStringField(SubsidyDBF.NP_NAME, "_CYR") : super.getCity();
    }

    @Override
    public String getStreetType() {
        return getUpStringField(SubsidyDBF.CAT_V, "_CYR");
    }

    @Override
    public String getStreetCode() {
        return null; //code is not used for correction
    }

    @Override
    public String getStreet() {
        return getUpStringField(SubsidyDBF.NAME_V, "_CYR");
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(SubsidyDBF.BLD, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        String corp = getUpStringField(SubsidyDBF.CORP, "_CYR");

        return corp.matches("0*") ? "" : corp;
    }

    @Override
    public String getApartment() {
        return getUpStringField(SubsidyDBF.FLAT, "_CYR");
    }

    @Override
    public String getPuAccountNumber() {
        return getStringField(SubsidyDBF.RASH);
    }
}
