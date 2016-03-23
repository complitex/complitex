package org.complitex.osznconnection.file.service.process;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.Log;
import org.complitex.common.entity.Log.EVENT;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestStatus.ACCOUNT_NUMBER_RESOLVED;
import static org.complitex.osznconnection.file.entity.RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeBindTaskBean extends AbstractTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(FacilityServiceTypeBindTaskBean.class);

    @Resource
    private UserTransaction userTransaction;

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

    private boolean resolveAddress(FacilityServiceType facilityServiceType) {
        addressService.resolveAddress(facilityServiceType);

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

    private boolean resolveRemoteAccountNumber(String serviceProviderCode, FacilityServiceType facilityServiceType, boolean benefit) throws DBException {
        serviceProviderAdapter.acquireFacilityPersonAccount(facilityServiceType,
                facilityServiceType.getOutgoingDistrict(), serviceProviderCode, facilityServiceType.getOutgoingStreetType(),
                facilityServiceType.getOutgoingStreet(),
                facilityServiceType.getOutgoingBuildingNumber(), facilityServiceType.getOutgoingBuildingCorp(),
                facilityServiceType.getOutgoingApartment(), facilityServiceType.getDate(),
                facilityServiceType.getInn(),
                facilityServiceType.getPassport(), benefit);


        if (facilityServiceType.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            try {
                personAccountService.save(facilityServiceType, facilityServiceType.getInn());
            } catch (MoreOneAccountException e) {
                throw new DBException(e);
            }
        }

        return facilityServiceType.getStatus() == ACCOUNT_NUMBER_RESOLVED;
    }

    public void bind(String serviceProviderCode, FacilityServiceType facilityServiceType, boolean benefit)
            throws DBException {
        String inn = facilityServiceType.getStringField(FacilityServiceTypeDBF.IDPIL);

        //resolve local account number
        personAccountService.localResolveAccountNumber(facilityServiceType, inn, true);

        //noinspection Duplicates
        if (facilityServiceType.getStatus().isNotIn(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            //resolve address
            resolveAddress(facilityServiceType);

            if (facilityServiceType.getStatus().isAddressResolved()){
                //resolve local account.
                resolveLocalAccount(facilityServiceType);

                if (facilityServiceType.getStatus().isNotIn(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    resolveRemoteAccountNumber(serviceProviderCode, facilityServiceType, benefit);
                }
            }
        }

        // обновляем facility service type запись
        facilityServiceTypeBean.update(facilityServiceType);
    }

    private void bindFacilityServiceTypeFile(RequestFile requestFile) throws BindException, DBException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

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
                    userTransaction.begin();
                    bind(serviceProviderCode, facilityServiceType, true);
                    userTransaction.commit();
                } catch (Exception e) {
                    log.error("The facility service type item ( id = " + facilityServiceType.getId() + ") was bound with error: ", e);

                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        log.error("Couldn't rollback transaction for binding facility service type item.", e1);
                    }
                }
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        //проверяем что не обрабатывается в данный момент
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
            throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.BINDING);
        requestFileBean.save(requestFile);

        facilityServiceTypeBean.clearBeforeBinding(requestFile.getId(), null);

        //связывание файла facility service type
        try {
            bindFacilityServiceTypeFile(requestFile);
        } catch (DBException e) {
            throw new RuntimeException(e);
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
    }

    @Override
    public void onError(RequestFile requestFile) {
        requestFile.setStatus(RequestFileStatus.BIND_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return FacilityServiceTypeBindTaskBean.class;
    }

    @Override
    public EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
