package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *
 * Запись файла запроса начислений.
 * @see org.complitex.osznconnection.file.entity.AbstractRequest
 *
 * Имена полей фиксированы в <code>Enum<code> перечислении <code>PaymentDBF</code>
 * @see PaymentDBF
 */
public class Payment extends AbstractAccountRequest<PaymentDBF> {
    public Payment() {
        super(RequestFileType.PAYMENT);
    }

    @Override
    public String getLastName() {
        return getUpStringField(PaymentDBF.SUR_NAM, "_CYR");
    }

    @Override
    public String getFirstName() {
        return getUpStringField(PaymentDBF.F_NAM, "_CYR");
    }

    @Override
    public String getMiddleName() {
        return getUpStringField(PaymentDBF.M_NAM, "_CYR");
    }

    @Override
    public String getCity() {
        return getUpStringField(PaymentDBF.N_NAME, "_CYR");
    }

    @Override
    public String getStreet() {
        return getUpStringField(PaymentDBF.VUL_NAME, "_CYR");
    }

    @Override
    public String getBuildingNumber() {
        return getUpStringField(PaymentDBF.BLD_NUM, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        String corp = getUpStringField(PaymentDBF.CORP_NUM, "_CYR");

        return corp.matches("0*") ? "" : corp;
    }

    @Override
    public String getApartment() {
        return getUpStringField(PaymentDBF.FLAT, "_CYR");
    }

    public String getFio(){
        return getLastName() + " " + getFirstName() + " " + getMiddleName();
    }
}
