package org.complitex.osznconnection.file.service.privilege.task;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
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

import static org.complitex.osznconnection.file.entity.RequestFileType.DEBT;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;

/**
 * @author Anatoly Ivanov
 * 18.09.2020 23:20
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DebtBindTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(DebtBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private DebtBean debtBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestWarningBean requestWarningBean;


    private void resolveAddress(Debt debt, Long billingId) {
        addressService.resolveAddress(debt, billingId);
    }

    @SuppressWarnings("Duplicates")
    private void resolveLocalAccount(Debt debt) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(debt, debt.getInn());

            if (!Strings.isNullOrEmpty(accountNumber)) {
                debt.setAccountNumber(accountNumber);
                debt.setStatus(ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            debt.setStatus(MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    private void resolveRemoteAccountNumber(String serviceProviderCode, Debt debt) throws MoreOneAccountException {
        serviceProviderAdapter.acquireFacilityPersonAccount(debt,
                debt.getOutgoingDistrict(), serviceProviderCode, debt.getOutgoingStreetType(),
                debt.getOutgoingStreet(),
                debt.getOutgoingBuildingNumber(), debt.getOutgoingBuildingCorp(),
                debt.getOutgoingApartment(), debt.getDate(),
                debt.getInn(),
                debt.getPassport());

        if (debt.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            personAccountService.save(debt, !Strings.isNullOrEmpty(debt.getPuAccountNumber())
                    ? debt.getPuAccountNumber() : debt.getInn());
        }
    }

    public void bind(String serviceProviderCode, Long billingId, Debt debt) throws MoreOneAccountException {
        String puAccountNumber = debt.getPuAccountNumber();

        debt.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true));

        //resolve local account number
        personAccountService.localResolveAccountNumber(debt, puAccountNumber, billingId, true
        );

        if (debt.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            personAccountService.localResolveAccountNumber(debt, debt.getInn(),
                    billingId, true);
        }

        boolean checkLodgerPerson = true;

        //noinspection Duplicates
        if (debt.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
            //resolve address
            resolveAddress(debt, billingId);

            if (debt.getStatus().isAddressResolved()) {
                resolveLocalAccount(debt);

                if (debt.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    personAccountService.resolveAccountNumber(debt, puAccountNumber,
                            serviceProviderCode, billingId, false);
                }

                if (debt.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    resolveRemoteAccountNumber(serviceProviderCode, debt);
                    checkLodgerPerson = false;

                    if (debt.getAccountNumber() == null &&
                            BENEFIT_OWNER_NOT_ASSOCIATED.equals(debt.getStatus())){
                        debt.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
                    }
                }
            }
        }

        if (checkLodgerPerson && debt.getAccountNumber() != null){
            serviceProviderAdapter.checkLodgerPerson(debt, debt.getAccountNumber(),
                    debt.getDate(), debt.getInn(), debt.getPassport());
        }

        // обновляем запись
        debtBean.update(debt);
    }

    private void bindDebtFile(RequestFile requestFile)
            throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла и доставать записи порциями по BATCH_SIZE штук.
        List<Long> debtIds = debtBean.getDebtIds(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (debtIds.size() > 0) {
            batch.clear();

            int toRemoveCount = Math.min(batchSize, debtIds.size());

            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(debtIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<Debt> debts = debtBean.getDebtForOperation(requestFile.getId(), batch);

            for (Debt debt : debts) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать запись
                try {
                    bind(serviceProviderCode, billingId, debt);
                    onRequest(debt, ProcessType.BIND_DEBT);
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

            debtBean.clearDebtBound(requestFile.getId());

            //clear warning
            requestWarningBean.delete(requestFile.getId(), DEBT);

            //связывание файла
            try {
                bindDebtFile(requestFile);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в файле связались
            if (!debtBean.isDebtBound(requestFile.getId())) {
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
