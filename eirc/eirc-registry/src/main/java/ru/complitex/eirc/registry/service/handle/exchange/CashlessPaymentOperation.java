package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.eirc.registry.entity.ContainerType;
import ru.complitex.eirc.service_provider_account.entity.CashlessPayment;
import ru.complitex.eirc.service_provider_account.service.CashlessPaymentBean;
import ru.complitex.eirc.service_provider_account.service.PaymentAttributeBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class CashlessPaymentOperation extends BasePaymentOperation<CashlessPayment> {

    @EJB
    private CashlessPaymentBean cashlessPaymentBean;

    @Override
    protected PaymentAttributeBean<CashlessPayment> getBean() {
        return cashlessPaymentBean;
    }

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.CASHLESS_PAYMENT.getId());
    }
}
