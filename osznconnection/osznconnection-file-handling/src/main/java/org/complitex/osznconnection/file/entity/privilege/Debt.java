package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;

import static org.complitex.osznconnection.file.entity.RequestFileType.DEBT;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:39
 */
public class Debt extends AbstractAccountRequest<DebtDBF> {
    public Debt() {
        super(DEBT);
    }

    public Debt(Long requestFileId){
        super(DEBT);

        setRequestFileId(requestFileId);
    }

    @Override
    public String getStreetCode() {
        return getStringField(DebtDBF.CDUL);
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(DebtDBF.HOUSE, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        String corp = getUpStringField(DebtDBF.BUILD, "_CYR");

        return corp.matches("0*") ? "" : corp;
    }

    @Override
    public String getApartment() {
        return getUpStringField(DebtDBF.APT, "_CYR");
    }

    @Override
    public String getPassport() {
        return getUpStringField(DebtDBF.PASPPIL, "_CYR");
    }

    @Override
    public String getInn() {
        return getUpStringField(DebtDBF.IDPIL, "_CYR");
    }

    public String getFio(){
        return getStringField(DebtDBF.FIOPIL, "_CYR");
    }

    @Override
    public String getPuAccountNumber() {
        return getStringField(DebtDBF.RAH);
    }
}
