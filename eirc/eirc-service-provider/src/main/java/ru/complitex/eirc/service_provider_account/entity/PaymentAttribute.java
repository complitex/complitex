package ru.complitex.eirc.service_provider_account.entity;

/**
 * @author Pavel Sknar
 */
public abstract class PaymentAttribute extends FinancialAttribute {
    protected PaymentAttribute() {
    }

    protected PaymentAttribute(ServiceProviderAccount serviceProviderAccount) {
        super(serviceProviderAccount);
    }

    private String numberQuittance;

    private Long paymentCollectorId;

    public String getNumberQuittance() {
        return numberQuittance;
    }

    public void setNumberQuittance(String numberQuittance) {
        this.numberQuittance = numberQuittance;
    }

    public Long getPaymentCollectorId() {
        return paymentCollectorId;
    }

    public void setPaymentCollectorId(Long paymentCollectorId) {
        this.paymentCollectorId = paymentCollectorId;
    }
}
