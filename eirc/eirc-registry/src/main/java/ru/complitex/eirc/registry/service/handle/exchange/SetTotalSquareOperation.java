package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.eirc.registry.entity.ContainerType;
import ru.complitex.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class SetTotalSquareOperation extends ServiceProviderAccountAttrOperation {

    @Override
    protected Long getAttributeId() {
        return ServiceProviderAccountStrategy.TOTAL_SQUARE;
    }

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.SET_WARM_SQUARE.getId());
    }
}
