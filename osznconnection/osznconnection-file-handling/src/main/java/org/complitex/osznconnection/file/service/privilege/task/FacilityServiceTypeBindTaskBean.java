package org.complitex.osznconnection.file.service.privilege.task;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeBindTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(FacilityServiceTypeBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestWarningBean requestWarningBean;

    private boolean resolveAddress(FacilityServiceType facilityServiceType, Long billingId) {
        addressService.resolveAddress(facilityServiceType, billingId);

        return facilityServiceType.getStatus().isAddressResolved();
    }

    private void resolveLocalAccount(FacilityServiceType facilityServiceType) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(facilityServiceType,
                    facilityServiceType.getInn());

            if (!Strings.isEmpty(accountNumber)) {
                facilityServiceType.setAccountNumber(accountNumber);
                facilityServiceType.setStatus(ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            facilityServiceType.setStatus(MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    private boolean resolveRemoteAccountNumber(String serviceProviderCode, FacilityServiceType facilityServiceType) throws MoreOneAccountException {
        serviceProviderAdapter.acquireFacilityPersonAccount(facilityServiceType,
                facilityServiceType.getOutgoingDistrict(), serviceProviderCode, facilityServiceType.getOutgoingStreetType(),
                facilityServiceType.getOutgoingStreet(),
                facilityServiceType.getOutgoingBuildingNumber(), facilityServiceType.getOutgoingBuildingCorp(),
                facilityServiceType.getOutgoingApartment(), facilityServiceType.getDate(),
                facilityServiceType.getInn(),
                facilityServiceType.getPassport());


        if (facilityServiceType.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            personAccountService.save(facilityServiceType, facilityServiceType.getInn());
        }

        return facilityServiceType.getStatus() == ACCOUNT_NUMBER_RESOLVED;
    }

    @SuppressWarnings("Duplicates")
    public void bind(String serviceProviderCode, Long billingId, FacilityServiceType facilityServiceType) throws MoreOneAccountException {
        facilityServiceType.setAccountNumber(null);

        //resolve local account number
        personAccountService.localResolveAccountNumber(facilityServiceType, facilityServiceType.getInn(),
                billingId, true);

//        boolean checkLodgerPerson = true;

        //noinspection Duplicates
        if (facilityServiceType.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            //resolve address
            resolveAddress(facilityServiceType, billingId);

            if (facilityServiceType.getStatus().isAddressResolved()){
                //resolve local account.
                resolveLocalAccount(facilityServiceType);

                if (facilityServiceType.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    resolveRemoteAccountNumber(serviceProviderCode, facilityServiceType);
//                    checkLodgerPerson = false;

                    if (facilityServiceType.getAccountNumber() == null &&
                            BENEFIT_OWNER_NOT_ASSOCIATED.equals(facilityServiceType.getStatus())){
                        facilityServiceType.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
                    }
                }
            }
        }

//        if (checkLodgerPerson && facilityServiceType.getAccountNumber() != null){
//            serviceProviderAdapter.checkLodgerPerson(facilityServiceType, facilityServiceType.getAccountNumber(),
//                    facilityServiceType.getDate(), facilityServiceType.getInn(), facilityServiceType.getPassport());
//        }

        // обновляем facility service type запись
        facilityServiceTypeBean.update(facilityServiceType);
    }

    private void bindFacilityServiceTypeFile(RequestFile requestFile) throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла facility service type и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedFacilityServiceTypeIds = facilityServiceTypeBean.findIdsForBinding(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedFacilityServiceTypeIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedFacilityServiceTypeIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedFacilityServiceTypeIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<FacilityServiceType> facilityServiceTypes = facilityServiceTypeBean.findForOperation(requestFile.getId(), batch);
            for (FacilityServiceType facilityServiceType : facilityServiceTypes) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать dwelling characteristics запись
                try {
                    bind(serviceProviderCode, billingId, facilityServiceType);
                    onRequest(facilityServiceType, ProcessType.LOAD_FACILITY_STREET_TYPE_REFERENCE);
                } catch (MoreOneAccountException e) {
                    throw new BindException(e, true, requestFile);
                }
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            //проверяем что не обрабатывается в данный момент
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
                throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BINDING);
            requestFileBean.save(requestFile);

            facilityServiceTypeBean.clearBeforeBinding(requestFile.getId(), null);

            //clear warning
            requestWarningBean.delete(requestFile.getId(), FACILITY_SERVICE_TYPE);

            //связывание файла facility service type
            try {
                bindFacilityServiceTypeFile(requestFile);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в facility service type файле связались
            if (!facilityServiceTypeBean.isFacilityServiceTypeFileBound(requestFile.getId())) {
                throw new BindException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.BIND_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }
}
