package ru.complitex.osznconnection.file.service.subsidy.task;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.Log;
import ru.complitex.common.entity.Log.EVENT;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.AddressService;
import ru.complitex.osznconnection.file.service.PersonAccountService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.BindException;
import ru.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.file.web.pages.util.GlobalOptions;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.*;

import static ru.complitex.osznconnection.file.entity.RequestStatus.ACCOUNT_NUMBER_RESOLVED;
import static ru.complitex.osznconnection.file.entity.RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ActualPaymentBindTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(ActualPaymentBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private ActualPaymentBean actualPaymentBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    private boolean resolveAddress(ActualPayment actualPayment, Long billingId) {
        addressService.resolveAddress(actualPayment, billingId);

        return actualPayment.getStatus().isAddressResolved();
    }

    private void resolveLocalAccount(ActualPayment actualPayment) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(actualPayment,
                    actualPayment.getStringField(ActualPaymentDBF.OWN_NUM));

            if (!Strings.isEmpty(accountNumber)) {
                actualPayment.setAccountNumber(accountNumber);
                actualPayment.setStatus(ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            actualPayment.setStatus(MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    private boolean resolveRemoteAccountNumber(String serviceProviderCode, ActualPayment actualPayment, Date date,
                                               Boolean updatePuAccount) throws MoreOneAccountException {
        serviceProviderAdapter.acquireAccountDetail(actualPayment,
                actualPayment.getStringField(ActualPaymentDBF.SUR_NAM),
                actualPayment.getStringField(ActualPaymentDBF.OWN_NUM), actualPayment.getOutgoingDistrict(),
                serviceProviderCode, actualPayment.getOutgoingStreetType(), actualPayment.getOutgoingStreet(),
                actualPayment.getOutgoingBuildingNumber(), actualPayment.getOutgoingBuildingCorp(),
                actualPayment.getOutgoingApartment(), date, updatePuAccount);

        if (actualPayment.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            personAccountService.save(actualPayment, actualPayment.getStringField(ActualPaymentDBF.OWN_NUM));
        }

        return actualPayment.getStatus() == ACCOUNT_NUMBER_RESOLVED;
    }

    private void bind(String serviceProviderCode, Long billingId, ActualPayment actualPayment, Date date,
                      Boolean updatePuAccount) throws MoreOneAccountException {
        //resolve address
        resolveAddress(actualPayment, billingId);

        if (actualPayment.getStatus().isAddressResolved()){
            //resolve local account.
            resolveLocalAccount(actualPayment);

            if (actualPayment.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                resolveRemoteAccountNumber(serviceProviderCode, actualPayment, date, updatePuAccount);
            }
        }

        // обновляем actualPayment запись
        actualPaymentBean.update(actualPayment);
    }

    private void bindActualPaymentFile(RequestFile requestFile, Boolean updatePuAccount) throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла actualPayment и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedPaymentIds = actualPaymentBean.findIdsForBinding(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedPaymentIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedPaymentIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedPaymentIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<ActualPayment> actualPayments = actualPaymentBean.findForOperation(requestFile.getId(), batch);
            for (ActualPayment actualPayment : actualPayments) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать actualPayment запись
                try {
                    bind(serviceProviderCode, billingId, actualPayment, actualPaymentBean.getFirstDay(actualPayment, requestFile),
                            updatePuAccount);
                    onRequest(actualPayment, ProcessType.BIND_ACTUAL_PAYMENT);
                } catch (MoreOneAccountException e) {
                    throw new BindException(e, true, requestFile);
                }
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            // ищем в параметрах комманды опцию "Переписывать номер л/с ПУ номером л/с МН"
            final Boolean updatePuAccount = commandParameters.containsKey(GlobalOptions.UPDATE_PU_ACCOUNT)
                    ? (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT) : false;

            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BINDING);
            requestFileBean.save(requestFile);

            //получаем информацию о текущем контексте вычислений
            Set<Long> serviceIds = new HashSet<>(); //todo get services by user organization

            actualPaymentBean.clearBeforeBinding(requestFile.getId(), serviceIds);

            //связывание файла actualPayment
            try {
                bindActualPaymentFile(requestFile, updatePuAccount);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в actualPayment файле связались
            if (!actualPaymentBean.isActualPaymentFileBound(requestFile.getId())) {
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

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
