package ru.flexpay.eirc.service_provider_account.entity;

/**
 * @author Pavel Sknar
 */
public class CashPayment extends PaymentAttribute {
    public CashPayment() {
    }

    public CashPayment(ServiceProviderAccount serviceProviderAccount) {
        super(serviceProviderAccount);
    }
}
