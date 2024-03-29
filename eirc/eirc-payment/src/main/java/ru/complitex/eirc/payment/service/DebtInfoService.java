package ru.complitex.eirc.payment.service;

import com.google.common.collect.Lists;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.apartment.ApartmentStrategy;
import ru.complitex.address.strategy.room.RoomStrategy;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.eirc.dictionary.entity.Address;
import ru.complitex.eirc.dictionary.entity.EircConfig;
import ru.complitex.eirc.dictionary.strategy.ModuleInstanceStrategy;
import ru.complitex.eirc.eirc_account.entity.EircAccount;
import ru.complitex.eirc.eirc_account.service.EircAccountBean;
import ru.complitex.eirc.organization.entity.EircOrganization;
import ru.complitex.eirc.organization.strategy.EircOrganizationStrategy;
import ru.complitex.eirc.payment.entity.DebtInfo;
import ru.complitex.eirc.payment.entity.ResponseStatus;
import ru.complitex.eirc.payment.entity.SearchType;
import ru.complitex.eirc.payment.entity.ServiceDetails;
import ru.complitex.eirc.service.correction.entity.ServiceCorrection;
import ru.complitex.eirc.service.correction.service.ServiceCorrectionBean;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;
import ru.complitex.eirc.service_provider_account.entity.SaldoOut;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.complitex.eirc.service_provider_account.service.SaldoOutBean;
import ru.complitex.eirc.service_provider_account.service.ServiceProviderAccountBean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
@LocalBean
@Path("/debtInfo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@RolesAllowed(SecurityRole.AUTHORIZED)
public class DebtInfoService extends RestAuthorizationService<DebtInfo> {

    @EJB
    private SaldoOutBean saldoOutBean;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private ApartmentStrategy apartmentStrategy;

    @EJB
    private RoomStrategy roomStrategy;

    @EJB
    private ServiceBean serviceBean;

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    @EJB
    private ModuleInstanceStrategy moduleInstanceStrategy;

    @EJB
    private EircOrganizationStrategy organizationStrategy;

    @EJB
    private ConfigBean configBean;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private EircAccountBean eircAccountBean;

    private Logger logger = LoggerFactory.getLogger(DebtInfoService.class);

    @Override
    protected DebtInfo getAllAuthorized(String moduleUniqueIndex) {
        return buildResponseContent(ResponseStatus.OK);
    }

    @Override
    protected DebtInfo geConstrainedAuthorized(String searchCriteria, long searchType, String moduleUniqueIndex) {
        Integer eircModuleId = configBean.getInteger(EircConfig.MODULE_ID, true);
        if (eircModuleId == null || eircModuleId < 0) {
            logger.error("Inner error: EIRC module did not configure");
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }

        DomainObject eircModule = moduleInstanceStrategy.getDomainObject(eircModuleId.longValue(), true);
        if (eircModule == null) {
            logger.error("Inner error: EIRC module instance not found by id '{}'", eircModuleId);
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }

        Attribute attribute = eircModule.getAttribute(ModuleInstanceStrategy.ORGANIZATION);
        final Long eircOrganizationId;
        if (attribute == null ||
                (eircOrganizationId = Long.parseLong(attribute.getStringValue(EjbBeanLocator.getBean(StringLocaleBean.class).getSystemLocaleId()).getValue())) == null) {
            logger.error("Inner error: EIRC module '{}' did not content own organization", eircModuleId);
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }

        Long moduleId = moduleInstanceStrategy.getModuleInstanceObjectId(moduleUniqueIndex);
        if (moduleId == null) {
            logger.error("Inner error: module instance not found by index '{}'", moduleUniqueIndex);
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }
        Long moduleOrganizationId = getOrganizationId(moduleId);
        if (moduleOrganizationId == null) {
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }

        SearchType type = SearchType.getSearchType((int) searchType);

        String[] reqMas = searchCriteria.split(":");
        Service service = null;
        if (reqMas.length > 1) {
            service = serviceBean.getService(Long.parseLong(reqMas[0]));
            if (service == null) {
                return buildResponseContent(ResponseStatus.SERVICE_NOT_FOUND);
            }
        }

        String searchString = reqMas.length > 1 ? reqMas[1] : reqMas[0];
        switch (type) {
            case TYPE_BUILDING_NUMBER:

                return getByAddressMasterIndex(searchString, service, new AddressDataProvider<Correction>() {
                    @Override
                    protected List<Correction> getAddressCorrection(String addressId, Long organizationId) {
                        return correctionBean.getCorrections(FilterWrapper.of(
                                new Correction("building", null, null, null,
                                        organizationId, eircOrganizationId)));
                    }

                    @Override
                    protected Address getAddress(Long id) {
                        DomainObject domainObject = null;//buildingAddressStrategy.getDomainObject(id, true);
                        if (domainObject == null) {
                            return null;
                        }
                        return new Address(id, AddressEntity.BUILDING);
                    }
                }, moduleOrganizationId, eircOrganizationId);
            case TYPE_APARTMENT_NUMBER:
                return getByAddressMasterIndex(searchString, service, new AddressDataProvider<Correction>() {
                    @Override
                    protected List<Correction> getAddressCorrection(String addressId, Long organizationId) {
                        return correctionBean.getCorrections(AddressEntity.APARTMENT,  null, organizationId, eircOrganizationId);
                    }

                    @Override
                    protected Address getAddress(Long id) {
                        DomainObject domainObject = apartmentStrategy.getDomainObject(id, true);
                        if (domainObject == null) {
                            return null;
                        }
                        return new Address(id, AddressEntity.APARTMENT);
                    }
                }, moduleOrganizationId, eircOrganizationId);
            case TYPE_ROOM_NUMBER:
                return getByAddressMasterIndex(searchString, service, new AddressDataProvider<Correction>() {
                    @Override
                    protected List<Correction> getAddressCorrection(String addressId, Long organizationId) {
                        return correctionBean.getCorrections(AddressEntity.ROOM, null, organizationId, eircOrganizationId);
                    }

                    @Override
                    protected Address getAddress(Long id) {
                        DomainObject domainObject = roomStrategy.getDomainObject(id, true);
                        if (domainObject == null) {
                            return null;
                        }
                        return new Address(id, AddressEntity.ROOM);
                    }
                }, moduleOrganizationId, eircOrganizationId);
            case TYPE_ACCOUNT_NUMBER:
                return getByEircAccountNumber(searchString, service, moduleOrganizationId, eircOrganizationId);
            case TYPE_SERVICE_PROVIDER_ACCOUNT_NUMBER:
                return getBySPAAccountNumber(searchString, service, moduleOrganizationId, eircOrganizationId);
            case TYPE_ADDRESS:
                return getByAddressInSPAAccount(searchString, service, moduleOrganizationId, eircOrganizationId);
            case UNKNOWN_TYPE:
                break;
        }
        return buildResponseContent(ResponseStatus.UNKNOWN_REQUEST);
    }

    @Override
    protected DebtInfo buildResponseContent(ResponseStatus responseStatus) {
        return new DebtInfo(responseStatus);
    }

    private <T extends Correction> DebtInfo getByAddressMasterIndex(String searchString, Service service,
                                                                    AddressDataProvider<T> dataProvider,
                                                                    Long moduleOrganizationId, Long eircOrganizationId) {
        String[] moduleData = searchString.split("-", 2);
        String addressId = moduleData.length > 1 ? moduleData[1] : moduleData[0];

        Long moduleId = null;
        if (moduleData.length > 1) {
            moduleId = moduleInstanceStrategy.getModuleInstanceObjectId(moduleData[0]);
            if (moduleId == null) {
                logger.error("Inner error: module instance not found by index '{}'", moduleData[0]);
                return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
            }
            Integer eircModuleId = configBean.getInteger(EircConfig.MODULE_ID, true);
            if (eircModuleId != null && eircModuleId.longValue() == moduleId) {
                moduleId = null;
            } else {
                logger.warn("Own module did not configure");
            }
        }

        Long organizationId = null;
        if (moduleId != null && (organizationId = getOrganizationId(moduleId)) == null) {
            return buildResponseContent(ResponseStatus.INTERNAL_ERROR);
        }

        Address address = dataProvider.getAddress(addressId, organizationId);
        if (address == null) {
            logger.error("Address not found addressId={} organizationId={}", addressId, organizationId);
            return buildResponseContent(ResponseStatus.ADDRESS_NOT_FOUND);
        }
        SaldoOut filterObject = new SaldoOut();
        filterObject.setServiceProviderAccount(new ServiceProviderAccount(new EircAccount(address), service));
        FilterWrapper<SaldoOut> filterWrapper = FilterWrapper.of(filterObject);

        return new DebtInfo(ResponseStatus.OK, getServiceDetails(filterWrapper, moduleOrganizationId, eircOrganizationId));
    }

    private DebtInfo getByEircAccountNumber(String searchString, Service service, Long moduleOrganizationId, Long eircOrganizationId) {

        if (!eircAccountBean.eircAccountExists(null, searchString)) {
            return buildResponseContent(ResponseStatus.ACCOUNT_NOT_FOUND);
        }

        SaldoOut filterObject = new SaldoOut();
        filterObject.setServiceProviderAccount(new ServiceProviderAccount(new EircAccount(searchString), service));
        FilterWrapper<SaldoOut> filterWrapper = FilterWrapper.of(filterObject);
        filterWrapper.setLike(false);

        return new DebtInfo(ResponseStatus.OK, getServiceDetails(filterWrapper, moduleOrganizationId, eircOrganizationId));
    }

    private DebtInfo getBySPAAccountNumber(String searchString, Service service, Long moduleOrganizationId, Long eircOrganizationId) {

        ServiceProviderAccount spa = new ServiceProviderAccount(searchString, null, service);
        if (serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(spa)).isEmpty()) {
            logger.error("Service provider account not found by service={} and number={}", service, searchString);
            return buildResponseContent(ResponseStatus.ACCOUNT_NOT_FOUND);
        }
        FilterWrapper<SaldoOut> filterWrapper = FilterWrapper.of(new SaldoOut(spa));
        filterWrapper.setLike(false);

        return new DebtInfo(ResponseStatus.OK, getServiceDetails(filterWrapper, moduleOrganizationId, eircOrganizationId));
    }

    private DebtInfo getByAddressInSPAAccount(String searchString, Service service,
                                              Long moduleOrganizationId, Long eircOrganizationId) {

        ServiceProviderAccount spa = new ServiceProviderAccount(searchString, null, service);
        List<ServiceProviderAccount> serviceProviderAccounts = serviceProviderAccountBean.getServiceProviderAccounts(FilterWrapper.of(spa));
        if (serviceProviderAccounts.isEmpty()) {
            logger.error("Service provider account not found by service={} and number={}", service, searchString);
            return buildResponseContent(ResponseStatus.ACCOUNT_NOT_FOUND);
        }

        List<ServiceDetails> result = Lists.newArrayList();
        FilterWrapper<SaldoOut> filterWrapper = FilterWrapper.of(new SaldoOut());
        filterWrapper.setLike(false);
        for (ServiceProviderAccount serviceProviderAccount : serviceProviderAccounts) {
            filterWrapper.getObject().setServiceProviderAccount(new ServiceProviderAccount(new EircAccount(serviceProviderAccount.getEircAccount().getAddress())));
            result.addAll(getServiceDetails(filterWrapper, moduleOrganizationId, eircOrganizationId));
        }

        return new DebtInfo(ResponseStatus.OK, result);
    }

    private Long getOrganizationId(Long moduleId) {
        DomainObject module = moduleInstanceStrategy.getDomainObject(moduleId, true);
        Attribute attribute = module.getAttribute(ModuleInstanceStrategy.ORGANIZATION);
        if (attribute == null) {
            logger.error("Inner error: Module '{}' did not content organization", moduleId);
            return null;
        }
        Long organizationId = Long.parseLong(attribute.getStringValue(EjbBeanLocator.getBean(StringLocaleBean.class).getSystemLocaleId()).getValue());
        EircOrganization organization = organizationStrategy.getDomainObject(organizationId, true);
        if (organization == null) {
            logger.error("Inner error: organization not found by id {}", organizationId);
            return null;
        }
        return organizationId;

    }

    private List<ServiceDetails> getServiceDetails(FilterWrapper<SaldoOut> filterWrapper,
                                                   Long moduleOrganizationId, Long eircOrganizationId) {
        Service service;
        List<SaldoOut> saldoOuts = saldoOutBean.getFinancialAttributes(filterWrapper, true);
        if (saldoOuts.isEmpty()) {
            return Collections.emptyList();
        }
        List<ServiceDetails> result = Lists.newArrayList();
        for (SaldoOut saldoOut : saldoOuts) {
            ServiceProviderAccount serviceProviderAccount = saldoOut.getServiceProviderAccount();
            service = serviceProviderAccount.getService();
            List<ServiceCorrection> serviceCorrections = serviceCorrectionBean.getServiceCorrections(FilterWrapper.of(
                    new ServiceCorrection(null, service.getId(), null, moduleOrganizationId, eircOrganizationId, null)
            ));

            String serviceMasterIndex = null;
            if (serviceCorrections.size() == 0) {
                logger.warn("Service correction not found for service by id {}: organization={}, userOrganization={}",
                        service.getId(), moduleOrganizationId, eircOrganizationId);
            } else if (serviceCorrections.size() > 1) {
                logger.warn("Multiply service corrections for service by id {}: organization={}, userOrganization={}",
                        service.getId(), moduleOrganizationId, eircOrganizationId);
            } else {
//                serviceMasterIndex = serviceCorrections.get(0).getExternalId();
            }

            ServiceDetails serviceDetails = new ServiceDetails();

            serviceDetails.setAmount(saldoOut.getAmount());
            serviceDetails.setOutgoingBalance(saldoOut.getAmount());
            serviceDetails.setServiceCode(service.getCode());
            serviceDetails.setServiceId(service.getId());
            serviceDetails.setServiceMasterIndex(serviceMasterIndex);
            serviceDetails.setServiceName(service.getName());
            serviceDetails.setEircAccount(serviceProviderAccount.getEircAccount().getAccountNumber());
            serviceDetails.setServiceProviderAccount(serviceProviderAccount.getAccountNumber());

            result.add(serviceDetails);
        }
        return result;
    }

    private abstract class AddressDataProvider<T extends Correction> {
        public Address getAddress(String externalId, Long organizationId) {
            Long internalId = null;
            if (organizationId != null) {
                List<T> corrections = getAddressCorrection(externalId, organizationId);
                if (corrections.size() > 1) {
                    logger.error("Several corrections on one address externalId={} and organizationId={}", externalId, organizationId);
                    return null;
                } else if (corrections.size() == 0) {
                    logger.error("Not found address`s correction externalId={} and organizationId={}", externalId, organizationId);
                    return null;
                }
                internalId = corrections.get(0).getObjectId();
            } else {
                internalId = Long.parseLong(externalId);
            }
            return getAddress(internalId);
        }

        protected abstract List<T> getAddressCorrection(String addressId, Long organizationId);

        protected abstract Address getAddress(Long id);
    }
}
