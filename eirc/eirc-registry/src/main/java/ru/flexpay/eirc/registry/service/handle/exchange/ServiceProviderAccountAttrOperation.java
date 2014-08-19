package ru.flexpay.eirc.registry.service.handle.exchange;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StatusType;
import org.complitex.common.service.LocaleBean;
import org.complitex.common.service.exception.AbstractException;
import org.complitex.common.util.CloneUtil;
import ru.flexpay.eirc.registry.entity.Container;
import ru.flexpay.eirc.registry.entity.Registry;
import ru.flexpay.eirc.registry.entity.RegistryRecordData;
import ru.flexpay.eirc.registry.entity.changing.ServiceProviderAccountAttrChanging;
import ru.flexpay.eirc.registry.service.handle.changing.ServiceProviderAccountAttrChangingBean;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;
import ru.flexpay.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Pavel Sknar
 */
public abstract class ServiceProviderAccountAttrOperation extends GeneralAccountOperation {

    @EJB
    private ServiceProviderAccountStrategy serviceProviderAccountStrategy;

    @EJB
    private LocaleBean localeBean;

    @EJB
    private ServiceProviderAccountAttrChangingBean serviceProviderAccountAttrChangingBean;

    @Override
    public void process(Registry registry, RegistryRecordData registryRecord, Container container,
                        List<OperationResult> results) throws AbstractException {

        ServiceProviderAccount serviceProviderAccount = getServiceProviderAccount(registry, registryRecord);

        DomainObject newObject = serviceProviderAccountStrategy.findById(serviceProviderAccount.getId(), true);
        DomainObject oldObject = CloneUtil.cloneObject(newObject);

        ServiceProviderAccountAttribute newObjectAttribute = (ServiceProviderAccountAttribute)newObject.getAttribute(getAttributeId());
        ServiceProviderAccountAttribute oldObjectAttribute = CloneUtil.cloneObject(newObjectAttribute);

        BaseAccountOperationData data = getContainerData(container);

        newObjectAttribute.setStringValue(data.getNewValue(), localeBean.getSystemLocaleObject().getId());

        serviceProviderAccountStrategy.update(oldObject, newObject, data.getChangeApplyingDate());

        serviceProviderAccountAttrChangingBean.create(
                new ServiceProviderAccountAttrChanging(oldObjectAttribute.getPkId(), newObjectAttribute.getPkId(), container.getId())
        );
        results.add(new OperationResult<>(oldObjectAttribute, newObjectAttribute, getCode()));
    }

    protected abstract Long getAttributeId();

    @Override
    public boolean canRollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ServiceProviderAccountAttrChanging changing = serviceProviderAccountAttrChangingBean.findChanging(container.getId());
        ServiceProviderAccountAttribute newAttribute = serviceProviderAccountStrategy.findByPkId(changing.getSpaNewAttributePkId());

        return newAttribute != null && newAttribute.getEndDate() == null;
    }

    @Override
    public void rollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        // find changing
        ServiceProviderAccountAttrChanging changing = serviceProviderAccountAttrChangingBean.findChanging(container.getId());
        if (changing.getSpaOldAttributePkId() != null) {
            // old attribute set active
            ServiceProviderAccountAttribute oldAttribute = serviceProviderAccountStrategy.findByPkId(changing.getSpaOldAttributePkId());
            oldAttribute.setEndDate(null);
            oldAttribute.setStatus(StatusType.ACTIVE);
            serviceProviderAccountStrategy.updateAttribute(oldAttribute);
        }
        // delete new attribute
        ServiceProviderAccountAttribute newAttribute = serviceProviderAccountStrategy.findByPkId(changing.getSpaNewAttributePkId());
        serviceProviderAccountStrategy.deleteAttribute(newAttribute);
        // delete changing
        serviceProviderAccountAttrChangingBean.delete(changing);
    }
}
