package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class ActualPayment extends AbstractAccountRequest<ActualPaymentDBF> {
    public ActualPayment() {
        super(RequestFileType.ACTUAL_PAYMENT);
    }

    @Override
    public String getCity() {
        return getStringField(ActualPaymentDBF.N_NAME);
    }

    @Override
    public String getStreetType() {
        return getStringField(ActualPaymentDBF.VUL_CAT);
    }

    @Override
    public String getStreetCode() {
        return getStringField(ActualPaymentDBF.VUL_CODE);
    }

    @Override
    public String getStreet() {
        return getStringField(ActualPaymentDBF.VUL_NAME);
    }

    @Override
    public String getBuildingNumber() {
        return getStringField(ActualPaymentDBF.BLD_NUM);
    }

    @Override
    public String getBuildingCorp() {
        String corp = getStringField(ActualPaymentDBF.CORP_NUM);

        return corp.matches("0*") ? "" : corp;
    }

    @Override
    public String getApartment() {
        return getStringField(ActualPaymentDBF.FLAT);
    }
}
