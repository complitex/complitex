package ru.complitex.eirc.service_provider_account.entity;

/**
 * @author Pavel Sknar
 */
public class CashlessPayment extends PaymentAttribute {
    public CashlessPayment() {
    }

    public CashlessPayment(ServiceProviderAccount serviceProviderAccount) {
        super(serviceProviderAccount);
    }

}
