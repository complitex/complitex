package ru.flexpay.eirc.registry.service.handle.exchange;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Status;
import ru.complitex.common.exception.AbstractException;
import ru.complitex.common.util.CloneUtil;
import ru.flexpay.eirc.eirc_account.service.EircAccountBean;
import ru.flexpay.eirc.registry.entity.Container;
import ru.flexpay.eirc.registry.entity.ContainerType;
import ru.flexpay.eirc.registry.entity.Registry;
import ru.flexpay.eirc.registry.entity.RegistryRecordData;
import ru.flexpay.eirc.registry.entity.changing.ObjectChanging;
import ru.flexpay.eirc.registry.service.handle.changing.ObjectChangingBean;
import ru.flexpay.eirc.service_provider_account.entity.SaldoOut;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;
import ru.flexpay.eirc.service_provider_account.service.SaldoOutBean;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;
import ru.flexpay.eirc.service_provider_account.strategy.ServiceProviderAccountStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
public class CloseAccountOperation extends GeneralAccountOperation {

    @EJB
    private ServiceProviderAccountStrategy serviceProviderAccountStrategy;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private EircAccountBean eircAccountBean;

    @EJB
    private SaldoOutBean saldoOutBean;

    @EJB
    private ObjectChangingBean objectChangingBean;

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.CLOSE_ACCOUNT.getId());
    }

    @Override
    public void process(Registry registry, RegistryRecordData registryRecord, Container container, List<OperationResult> results) throws AbstractException {
        ServiceProviderAccount serviceProviderAccount = getServiceProviderAccount(registry, registryRecord);
        List<SaldoOut> saldoOuts = saldoOutBean.getFinancialAttributes(FilterWrapper.of(new SaldoOut(serviceProviderAccount)), true);
        // Сальдо на счету должно быть нулевым, если нет, то устанавливаем атрибут "На закрытие" с идом контейнера
        if (saldoOuts.size() > 0 && saldoOuts.get(0).getAmount() != null && saldoOuts.get(0).getAmount().doubleValue() != 0.) {
            DomainObject newObject = serviceProviderAccountStrategy.getDomainObject(serviceProviderAccount.getId(), true);
            DomainObject oldObject = CloneUtil.cloneObject(newObject);

            ServiceProviderAccountAttribute newObjectAttribute = (ServiceProviderAccountAttribute)newObject.getAttribute(ServiceProviderAccountStrategy.TO_CLOSE);
            ServiceProviderAccountAttribute oldObjectAttribute = CloneUtil.cloneObject(newObjectAttribute);

            BaseAccountOperationData data = getContainerData(container);

            newObjectAttribute.setValueId(container.getId());

            serviceProviderAccountStrategy.update(oldObject, newObject, data.getChangeApplyingDate());

            objectChangingBean.create(
                    new ObjectChanging(oldObjectAttribute.getPkId(), newObjectAttribute.getPkId(), container.getId())
            );
            results.add(new OperationResult<>(oldObjectAttribute, newObjectAttribute, getCode()));
            return;
        }
        ServiceProviderAccount oldObject = CloneUtil.cloneObject(serviceProviderAccount);
        // Сальдо нулевое - закрываем счет
        serviceProviderAccount.setRegistryRecordContainerId(container.getId());
        serviceProviderAccountBean.close(serviceProviderAccount);
        results.add(new OperationResult<>(oldObject, serviceProviderAccount, getCode()));
    }

    @Override
    public void rollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ServiceProviderAccount serviceProviderAccount = serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(container.getId());

        if (serviceProviderAccount == null) {
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
            return;
        }
        serviceProviderAccountBean.restore(serviceProviderAccount);
    }

    @Override
    public boolean canRollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ServiceProviderAccount serviceProviderAccount = serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(container.getId());
        if (serviceProviderAccount != null) {
            return true;
        }
        ObjectChanging changing = objectChangingBean.findChanging(container.getId());
        if (changing == null) {
            return false;
        }
        ServiceProviderAccountAttribute newAttribute = serviceProviderAccountStrategy.findByPkId(changing.getNewPkId());

        return newAttribute != null && newAttribute.getEndDate() == null;
    }
}
