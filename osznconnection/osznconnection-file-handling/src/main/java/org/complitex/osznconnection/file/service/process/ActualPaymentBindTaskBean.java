package org.complitex.osznconnection.file.service.process;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.Log;
import org.complitex.common.entity.Log.EVENT;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import org.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import org.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.web.pages.util.GlobalOptions;
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
import java.util.*;

import static org.complitex.osznconnection.file.entity.RequestStatus.ACCOUNT_NUMBER_RESOLVED;
import static org.complitex.osznconnection.file.entity.RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ActualPaymentBindTaskBean implements ITaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(ActualPaymentBindTaskBean.class);

    @Resource
    private UserTransaction userTransaction;

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

    private boolean resolveAddress(ActualPayment actualPayment) {
        addressService.resolveAddress(actualPayment);

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
                                               Boolean updatePuAccount) throws DBException {
        serviceProviderAdapter.acquireAccountDetail(actualPayment,
                actualPayment.getStringField(ActualPaymentDBF.SUR_NAM),
                actualPayment.getStringField(ActualPaymentDBF.OWN_NUM), actualPayment.getOutgoingDistrict(),
                serviceProviderCode, actualPayment.getOutgoingStreetType(), actualPayment.getOutgoingStreet(),
                actualPayment.getOutgoingBuildingNumber(), actualPayment.getOutgoingBuildingCorp(),
                actualPayment.getOutgoingApartment(), date, updatePuAccount);

        if (actualPayment.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
            try {
                personAccountService.save(actualPayment, actualPayment.getStringField(ActualPaymentDBF.OWN_NUM));
            } catch (MoreOneAccountException e) {
                throw new DBException(e);
            }
        }

        return actualPayment.getStatus() == ACCOUNT_NUMBER_RESOLVED;
    }

    private void bind(String serviceProviderCode, ActualPayment actualPayment, Date date, Boolean updatePuAccount) throws DBException {
        //resolve address
        resolveAddress(actualPayment);

        if (actualPayment.getStatus().isAddressResolved()){
            //resolve local account.
            resolveLocalAccount(actualPayment);

            if (actualPayment.getStatus().isNotIn(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                resolveRemoteAccountNumber(serviceProviderCode, actualPayment, date, updatePuAccount);
            }
        }

        // обновляем actualPayment запись
        actualPaymentBean.update(actualPayment);
    }

    private void bindActualPaymentFile(RequestFile requestFile, Boolean updatePuAccount) throws BindException, DBException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

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
                    userTransaction.begin();
                    bind(serviceProviderCode, actualPayment, actualPaymentBean.getFirstDay(actualPayment, requestFile), updatePuAccount);
                    userTransaction.commit();
                } catch (Exception e) {
                    log.error("The actual payment item ( id = " + actualPayment.getId() + ") was bound with error: ", e);

                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        log.error("Couldn't rollback transaction for binding actual payment item.", e1);
                    }
                }
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
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
        } catch (DBException e) {
            throw new RuntimeException(e);
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
    public Class getControllerClass() {
        return ActualPaymentBindTaskBean.class;
    }

    @Override
    public EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
