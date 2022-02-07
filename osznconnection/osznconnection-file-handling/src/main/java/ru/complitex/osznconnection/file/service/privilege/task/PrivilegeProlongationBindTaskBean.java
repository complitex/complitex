package ru.complitex.osznconnection.file.service.privilege.task;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.AddressService;
import ru.complitex.osznconnection.file.service.PersonAccountService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.BindException;
import ru.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.warning.RequestWarningBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileType.PRIVILEGE_PROLONGATION;
import static ru.complitex.osznconnection.file.entity.RequestStatus.*;

/**
 * @author inheaven on 02.07.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeProlongationBindTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(PrivilegeProlongationBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestWarningBean requestWarningBean;


    private void resolveAddress(PrivilegeProlongation privilegeProlongation, Long billingId) {
        addressService.resolveAddress(privilegeProlongation, billingId);
    }

    @SuppressWarnings("Duplicates")
    private void resolveLocalAccount(PrivilegeProlongation privilegeProlongation) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(privilegeProlongation, privilegeProlongation.getInn());

            if (!Strings.isNullOrEmpty(accountNumber)) {
                privilegeProlongation.setAccountNumber(accountNumber);
                privilegeProlongation.setStatus(ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            privilegeProlongation.setStatus(MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    private void resolveRemoteAccountNumber(String serviceProviderCode, PrivilegeProlongation privilegeProlongation) throws MoreOneAccountException {
        serviceProviderAdapter.acquireFacilityPersonAccount(privilegeProlongation,
                privilegeProlongation.getOutgoingDistrict(), serviceProviderCode, privilegeProlongation.getOutgoingStreetType(),
                privilegeProlongation.getOutgoingStreet(),
                privilegeProlongation.getOutgoingBuildingNumber(), privilegeProlongation.getOutgoingBuildingCorp(),
                privilegeProlongation.getOutgoingApartment(), privilegeProlongation.getDate(),
                privilegeProlongation.getInn(),
                privilegeProlongation.getPassport());

        if (privilegeProlongation.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            personAccountService.save(privilegeProlongation, !Strings.isNullOrEmpty(privilegeProlongation.getPuAccountNumber())
                    ? privilegeProlongation.getPuAccountNumber() : privilegeProlongation.getInn());
        }
    }

    public void bind(String serviceProviderCode, Long billingId, PrivilegeProlongation privilegeProlongation) throws MoreOneAccountException {
        String puAccountNumber = privilegeProlongation.getPuAccountNumber();

        privilegeProlongation.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true));

        //resolve local account number
        personAccountService.localResolveAccountNumber(privilegeProlongation, puAccountNumber, billingId, true
        );

        if (privilegeProlongation.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            personAccountService.localResolveAccountNumber(privilegeProlongation, privilegeProlongation.getInn(),
                    billingId, true);
        }

        boolean checkLodgerPerson = true;

        //noinspection Duplicates
        if (privilegeProlongation.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
            //resolve address
            resolveAddress(privilegeProlongation, billingId);

            if (privilegeProlongation.getStatus().isAddressResolved()) {
                resolveLocalAccount(privilegeProlongation);

                if (privilegeProlongation.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    personAccountService.resolveAccountNumber(privilegeProlongation, puAccountNumber,
                            serviceProviderCode, billingId, false);
                }

                if (privilegeProlongation.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    resolveRemoteAccountNumber(serviceProviderCode, privilegeProlongation);
                    checkLodgerPerson = false;

                    if (privilegeProlongation.getAccountNumber() == null &&
                            BENEFIT_OWNER_NOT_ASSOCIATED.equals(privilegeProlongation.getStatus())){
                        privilegeProlongation.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
                    }
                }
            }
        }

        if (checkLodgerPerson && privilegeProlongation.getAccountNumber() != null){
            serviceProviderAdapter.checkLodgerPerson(privilegeProlongation, privilegeProlongation.getAccountNumber(),
                    privilegeProlongation.getDate(), privilegeProlongation.getInn(), privilegeProlongation.getPassport());
        }

        // обновляем запись
        privilegeProlongationBean.updatePrivilegeProlongation(privilegeProlongation);
    }

    private void bindPrivilegeProlongationFile(RequestFile requestFile)
            throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла и доставать записи порциями по BATCH_SIZE штук.
        List<Long> prolongationIds = privilegeProlongationBean.getPrivilegeProlongationIds(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (prolongationIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, prolongationIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(prolongationIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<PrivilegeProlongation> privilegeProlongations =
                    privilegeProlongationBean.getPrivilegeProlongationForOperation(requestFile.getId(), batch);
            for (PrivilegeProlongation privilegeProlongation : privilegeProlongations) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать запись
                try {
                    bind(serviceProviderCode, billingId, privilegeProlongation);
                    onRequest(privilegeProlongation, ProcessType.BIND_PRIVILEGE_PROLONGATION);
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

            privilegeProlongationBean.clearPrivilegeProlongationBound(requestFile.getId());

            //clear warning
            requestWarningBean.delete(requestFile.getId(), PRIVILEGE_PROLONGATION);

            //связывание файла
            try {
                bindPrivilegeProlongationFile(requestFile);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в файле связались
            if (!privilegeProlongationBean.isPrivilegeProlongationBound(requestFile.getId())) {
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