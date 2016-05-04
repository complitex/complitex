package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.address.util.AddressRenderer;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class Subsidy extends AbstractAccountRequest<SubsidyDBF> {
    public final static List<String> CYR = Collections.unmodifiableList(Arrays.asList(
            "FIO", "NP_NAME", "CAT_V", "NAME_V", "BLD", "CORP", "FLAT"
    ));

    private List<SubsidyMasterData> masterDataList;

    public String getAddress(Locale locale){
        return AddressRenderer.displayAddress(getStreetType(), getStreet(), getBuildingNumber(), getBuildingCorp(),
                getApartment(), locale);
    }

    public List<SubsidyMasterData> getMasterDataList() {
        return masterDataList;
    }

    public void setMasterDataList(List<SubsidyMasterData> masterDataList) {
        this.masterDataList = masterDataList;
    }

    public String getFio(){
        return getUpStringField(SubsidyDBF.FIO, "_CYR");
    }

    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.SUBSIDY;
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
}
