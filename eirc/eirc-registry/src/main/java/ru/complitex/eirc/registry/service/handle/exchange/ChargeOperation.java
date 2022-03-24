package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.eirc.registry.entity.ContainerType;
import ru.complitex.eirc.service_provider_account.entity.Charge;
import ru.complitex.eirc.service_provider_account.service.ChargeBean;
import ru.complitex.eirc.service_provider_account.service.FinancialAttributeBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ChargeOperation extends BaseFinancialOperation<Charge> {

    @EJB
    private ChargeBean chargeBean;

    @Override
    protected FinancialAttributeBean<Charge> getBean() {
        return chargeBean;
    }

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.CHARGE.getId());
    }
}
