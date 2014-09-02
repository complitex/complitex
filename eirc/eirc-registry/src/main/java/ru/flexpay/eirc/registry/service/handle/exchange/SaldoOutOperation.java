package ru.flexpay.eirc.registry.service.handle.exchange;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.exception.AbstractException;
import ru.flexpay.eirc.registry.entity.Container;
import ru.flexpay.eirc.registry.entity.ContainerType;
import ru.flexpay.eirc.registry.entity.Registry;
import ru.flexpay.eirc.registry.entity.RegistryRecordData;
import ru.flexpay.eirc.registry.service.handle.changing.ObjectChangingBean;
import ru.flexpay.eirc.service_provider_account.entity.SaldoOut;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountAttribute;
import ru.flexpay.eirc.service_provider_account.service.FinancialAttributeBean;
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
public class SaldoOutOperation extends BaseFinancialOperation<SaldoOut> {

    @EJB
    private SaldoOutBean saldoOutBean;

    @EJB
    private ServiceProviderAccountStrategy serviceProviderAccountStrategy;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private ObjectChangingBean objectChangingBean;

    @Override
    public void process(Registry registry, RegistryRecordData registryRecord, Container container, List<OperationResult> results) throws AbstractException {
        super.process(registry, registryRecord, container, results);
        if (results.size() > 0) {
            OperationResult result = results.get(results.size() - 1);
            Long containerId;
            // Проверяем, что исходящее сальдо нулевое, счет не закрыт и счет стоит на закрытие
            if (result.getCode().equals(getCode()) && ((SaldoOut)result.getNewObject()).getAmount().doubleValue() == 0. &&
                    ((SaldoOut) result.getNewObject()).getServiceProviderAccount().getEndDate() == null &&
                    (containerId = requestCloseFromContainer(((SaldoOut) result.getNewObject()).getServiceProviderAccount())) != null) {
                ServiceProviderAccount serviceProviderAccount = ((SaldoOut) result.getNewObject()).getServiceProviderAccount();
                // TODO Контейнер закрытия счета мог быть удален вместе с реестром
                serviceProviderAccount.setRegistryRecordContainerId(containerId);
                serviceProviderAccountBean.close(serviceProviderAccount);
            }
        }
    }

    private Long requestCloseFromContainer(ServiceProviderAccount serviceProviderAccount) {
        DomainObject domainObject = serviceProviderAccountStrategy.findById(serviceProviderAccount.getId(), true);
        ServiceProviderAccountAttribute toClose = (ServiceProviderAccountAttribute)domainObject.getAttribute(ServiceProviderAccountStrategy.TO_CLOSE);
        return toClose != null? toClose.getValueId() : null;
    }

    @Override
    public void rollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        SaldoOut saldoOut = getBean().getFinancialAttributeByRRContainerId(container.getId());
        ServiceProviderAccount serviceProviderAccount = null;
        // При нулевом исходящем сальдо могли закрыть счет
        if (saldoOut.getAmount().doubleValue() == 0.) {
            serviceProviderAccount = saldoOut.getServiceProviderAccount();
            // Счет был закрыт и стоял на закрытие
            if (serviceProviderAccount == null || serviceProviderAccount.getEndDate() == null ||
                    requestCloseFromContainer(serviceProviderAccount) == null) {
                serviceProviderAccount = null;
            }
        }
        super.rollback(operationResult, container);
        // После востановления старого значения надо проверить на != 0
        if (serviceProviderAccount != null) {
            List<SaldoOut> saldoOuts = getBean().getFinancialAttributes(new FilterWrapper<>(new SaldoOut(serviceProviderAccount)), true);
            if (saldoOuts.size() > 0 && saldoOuts.get(saldoOuts.size() - 1).getAmount().doubleValue() != 0.) {
                serviceProviderAccountBean.restore(serviceProviderAccount);
            }
        }
    }

    @Override
    protected FinancialAttributeBean<SaldoOut> getBean() {
        return saldoOutBean;
    }

    @Override
    public Long getCode() {
        return ContainerType.SALDO_OUT.getId();
    }
}
