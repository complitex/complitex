package org.complitex.osznconnection.file.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *
 * Запись файла запроса начислений.
 * @see org.complitex.osznconnection.file.entity.AbstractRequest
 *
 * Имена полей фиксированы в <code>Enum<code> перечислении <code>PaymentDBF</code>
 * @see org.complitex.osznconnection.file.entity.PaymentDBF
 */
public class Payment extends AbstractAccountRequest<PaymentDBF> {
    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.PAYMENT;
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
        return getUpStringField(PaymentDBF.CORP_NUM, "_CYR");
    }

    @Override
    public String getApartment() {
        return getUpStringField(PaymentDBF.FLAT, "_CYR");
    }


}
