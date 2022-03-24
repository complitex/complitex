package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Status;
import ru.complitex.common.exception.AbstractException;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.eirc.registry.entity.Container;
import ru.complitex.eirc.registry.entity.Registry;
import ru.complitex.eirc.registry.entity.RegistryRecordData;
import ru.complitex.eirc.registry.entity.changing.ObjectChanging;
import ru.complitex.eirc.registry.service.handle.changing.ObjectChangingBean;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;
import ru.complitex.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Pavel Sknar
 */
public abstract class ServiceProviderAccountAttrOperation extends GeneralAccountOperation {

    @EJB
    private ServiceProviderAccountStrategy serviceProviderAccountStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private ObjectChangingBean objectChangingBean;

    @Override
    public void process(Registry registry, RegistryRecordData registryRecord, Container container,
                        List<OperationResult> results) throws AbstractException {

        ServiceProviderAccount serviceProviderAccount = getServiceProviderAccount(registry, registryRecord);

        DomainObject newObject = serviceProviderAccountStrategy.getDomainObject(serviceProviderAccount.getId(), true);
        DomainObject oldObject = CloneUtil.cloneObject(newObject);

        ServiceProviderAccountAttribute newObjectAttribute = (ServiceProviderAccountAttribute)newObject.getAttribute(getAttributeId());
        ServiceProviderAccountAttribute oldObjectAttribute = CloneUtil.cloneObject(newObjectAttribute);

        BaseAccountOperationData data = getContainerData(container);

        newObjectAttribute.setStringValue(data.getNewValue(), stringLocaleBean.getSystemStringLocale().getId());

        serviceProviderAccountStrategy.update(oldObject, newObject, data.getChangeApplyingDate());

        objectChangingBean.create(
                new ObjectChanging(oldObjectAttribute.getPkId(), newObjectAttribute.getPkId(), container.getId())
        );
        results.add(new OperationResult<>(oldObjectAttribute, newObjectAttribute, getCode()));
    }

    protected abstract Long getAttributeId();

    @Override
    public boolean canRollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ObjectChanging changing = objectChangingBean.findChanging(container.getId());
        ServiceProviderAccountAttribute newAttribute = serviceProviderAccountStrategy.findByPkId(changing.getNewPkId());

        return newAttribute != null && newAttribute.getEndDate() == null;
    }

    @Override
    public void rollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        // find changing
        ObjectChanging changing = objectChangingBean.findChanging(container.getId());
        if (changing.getOldPkId() != null) {
            // old attribute set active
            ServiceProviderAccountAttribute oldAttribute = serviceProviderAccountStrategy.findByPkId(changing.getOldPkId());
            oldAttribute.setEndDate(null);
            oldAttribute.setStatus(Status.ACTIVE);
            serviceProviderAccountStrategy.updateAttribute(oldAttribute);
        }
        // delete new attribute
        ServiceProviderAccountAttribute newAttribute = serviceProviderAccountStrategy.findByPkId(changing.getNewPkId());
        serviceProviderAccountStrategy.deleteAttribute(newAttribute);
        // delete changing
        objectChangingBean.delete(changing);
    }
}
