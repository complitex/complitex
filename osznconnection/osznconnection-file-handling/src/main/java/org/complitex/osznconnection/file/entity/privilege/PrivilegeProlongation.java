package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author inheaven on 23.06.2016.
 */
public class PrivilegeProlongation extends AbstractAccountRequest<PrivilegeProlongationDBF>{
    public enum TYPE {S, P}

    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.PRIVILEGE_PROLONGATION;
    }


    @Override
    public String getStreetCode() {
        return getStringField(PrivilegeProlongationDBF.CDUL);
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(PrivilegeProlongationDBF.HOUSE, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        String corp = getUpStringField(PrivilegeProlongationDBF.BUILD, "_CYR");

        return corp.matches("0*") ? "" : corp;
    }

    @Override
    public String getApartment() {
        return getUpStringField(PrivilegeProlongationDBF.APT, "_CYR");
    }

    @Override
    public String getPassport() {
        return getUpStringField(PrivilegeProlongationDBF.PASPPIL, "_CYR");
    }

    @Override
    public String getInn() {
        return getUpStringField(PrivilegeProlongationDBF.IDPIL, "_CYR");
    }

    public String getFio(){
        return getStringField(PrivilegeProlongationDBF.FIOPIL, "_CYR");
    }
}
