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
        return getStringField(PaymentDBF.SUR_NAM, "_CYR");
    }

    @Override
    public String getFirstName() {
        return getStringField(PaymentDBF.F_NAM, "_CYR");
    }

    @Override
    public String getMiddleName() {
        return getStringField(PaymentDBF.M_NAM, "_CYR");
    }

    @Override
    public String getCity() {
        return getStringField(PaymentDBF.N_NAME, "_CYR");
    }

    @Override
    public String getStreet() {
        return getStringField(PaymentDBF.VUL_NAME, "_CYR");
    }

    @Override
    public String getBuildingNumber() {
        return getStringField(PaymentDBF.BLD_NUM, "_CYR");
    }

    @Override
    public String getBuildingCorp() {
        return getStringField(PaymentDBF.CORP_NUM, "_CYR");
    }

    @Override
    public String getApartment() {
        return getStringField(PaymentDBF.FLAT, "_CYR");
    }


}
