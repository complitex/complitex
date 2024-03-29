package ru.complitex.eirc.registry.service.handle.exchange;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.exception.AbstractException;
import ru.complitex.common.util.AttributeUtil;
import ru.complitex.eirc.dictionary.entity.Address;
import ru.complitex.eirc.dictionary.entity.Person;
import ru.complitex.eirc.eirc_account.entity.EircAccount;
import ru.complitex.eirc.eirc_account.service.EircAccountBean;
import ru.complitex.eirc.registry.entity.Container;
import ru.complitex.eirc.registry.entity.ContainerType;
import ru.complitex.eirc.registry.entity.Registry;
import ru.complitex.eirc.registry.entity.RegistryRecordData;
import ru.complitex.eirc.service.correction.entity.ServiceCorrection;
import ru.complitex.eirc.service.correction.service.ServiceCorrectionBean;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.complitex.eirc.service_provider_account.service.ServiceProviderAccountBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
public class OpenAccountOperation extends GeneralAccountOperation {

    @EJB
    private EircAccountBean eircAccountBean;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private ServiceBean serviceBean;

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    @Override
    public Long getCode() {
        return Long.valueOf(ContainerType.OPEN_ACCOUNT.getId());
    }

    @Override
    public void process(Registry registry, RegistryRecordData registryRecord, Container container,
                        List<OperationResult> results) throws AbstractException {
        Address address = registryRecord.getAddress();
        Person person = registryRecord.getPerson();
        Long cityId = registryRecord.getCityId();
        String serviceProviderAccountNumber = registryRecord.getPersonalAccountExt();
        Long organizationId = registry.getSenderOrganizationId();
        String serviceCode = registryRecord.getServiceCode();

        if (address == null) {
            throw new DataNotFoundException("Address empty in registry record: {0}", registryRecord);
        }
        EircAccount eircAccount = eircAccountBean.getEircAccount(address);
        if (eircAccount == null) {
            DomainObject city = cityStrategy.getDomainObject(cityId, true);
            String cityPrefix = AttributeUtil.getStringValue(city, 402L);
            if (cityPrefix == null) {
                throw new DataNotFoundException("Not found EIRC prefix for city {0}", cityId);
            }
            String eircAccountNumber;
            try {
                eircAccountNumber = eircAccountBean.generateEircAccountNumber(cityPrefix);
            } catch (CheckDigitException e) {
                throw new ContainerDataException(e, "Failed generate eirc account number");
            }
            if (eircAccountNumber == null) {
                throw new ContainerDataException("Failed generate eirc account number: it is null");
            }
            eircAccount = new EircAccount();
            eircAccount.setAddress(address);
            eircAccount.setAccountNumber(eircAccountNumber);
            eircAccount.setPerson(person);
            eircAccount.setCreatedFromRegistry(true);
            eircAccountBean.save(eircAccount);

            results.add(new OperationResult<>(null, eircAccount, getCode()));
        }

        Service service = null;
        if (StringUtils.startsWith(serviceCode, "#")) {
            String extServiceCode = StringUtils.remove(serviceCode, '#');
            List<ServiceCorrection> corrections = serviceCorrectionBean.getServiceCorrections(
                    new FilterWrapper<>(new ServiceCorrection(null, null, extServiceCode,
                            registry.getSenderOrganizationId(), registry.getRecipientOrganizationId(), null)));
            if (corrections.size() > 1) {
                throw new ContainerDataException("Found several service corrections by code {0}", serviceCode);
            }

            if (corrections.size() == 1) {
                service = serviceBean.getService(corrections.get(0).getObjectId());
            }

        }

        if (service == null) {
            FilterWrapper<Service> filter = FilterWrapper.of(new Service(serviceCode));
            filter.setSortProperty(null);
            List<Service> services = serviceBean.getServices(filter);
            if (services.size() == 0) {
                throw new DataNotFoundException("Not found service by code {0}", serviceCode);
            }
            service = services.get(0);
        }

        ServiceProviderAccount serviceProviderAccount = new ServiceProviderAccount();
        serviceProviderAccount.setAccountNumber(serviceProviderAccountNumber);
        serviceProviderAccount.setEircAccount(eircAccount);
        serviceProviderAccount.setOrganizationId(organizationId);
        serviceProviderAccount.setService(service);
        serviceProviderAccount.setPerson(person);
        serviceProviderAccount.setBeginDate(getContainerData(container).getChangeApplyingDate());
        serviceProviderAccount.setRegistryRecordContainerId(container.getId());

        serviceProviderAccountBean.save(serviceProviderAccount);

        results.add(new OperationResult<>(null, serviceProviderAccount, getCode()));
    }

    @Override
    public void rollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ServiceProviderAccount serviceProviderAccount = serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(container.getId());
        // TODO Здесь возможен эксепшен на удаление при существующих внешних ключах
        serviceProviderAccountBean.delete(serviceProviderAccount);
        if (serviceProviderAccount.getEircAccount().isCreatedFromRegistry2() &&
                !serviceProviderAccountBean.serviceProviderAccountsExists(serviceProviderAccount.getEircAccount().getId()) &&
                !eircAccountBean.hasHistory(serviceProviderAccount.getEircAccount().getId())) {
            eircAccountBean.delete(serviceProviderAccount.getEircAccount());
        }
    }

    @Override
    public boolean canRollback(OperationResult<?> operationResult, Container container) throws AbstractException {
        ServiceProviderAccount serviceProviderAccount = serviceProviderAccountBean.getServiceProviderAccountByRRContainerId(container.getId());
        //TODO Необходимо проверить есть ли ссылки на созданную запись, например, оплаты
        return serviceProviderAccount != null && serviceProviderAccount.getEndDate() == null;
    }
}
