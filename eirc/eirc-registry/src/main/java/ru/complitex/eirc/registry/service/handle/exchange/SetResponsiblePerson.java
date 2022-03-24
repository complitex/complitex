package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.eirc.registry.entity.ContainerType;
import ru.complitex.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class SetResponsiblePerson extends ServiceProviderAccountAttrOperation {

    @Override
    protected Long getAttributeId() {
        return ServiceProviderAccountStrategy.RESPONSIBLE_PERSON;
    }

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.SET_RESPONSIBLE_PERSON.getId());
    }
}
