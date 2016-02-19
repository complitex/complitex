package org.complitex.osznconnection.file.service.process;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.*;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.web.pages.util.GlobalOptions;
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

import static org.complitex.osznconnection.file.entity.PaymentDBF.OWN_NUM_SR;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:56
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GroupBindTaskBean implements ITaskBean {
    private final Logger log = LoggerFactory.getLogger(GroupBindTaskBean.class);

    @Resource
    private UserTransaction userTransaction;

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        // ищем в параметрах комманды опцию "Переписывать номер л/с ПУ номером л/с МН"
        final Boolean updatePuAccount = commandParameters.containsKey(GlobalOptions.UPDATE_PU_ACCOUNT)
                ? (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT) : false;

        RequestFileGroup group = (RequestFileGroup) executorObject;

        group.setStatus(requestFileGroupBean.getRequestFileStatus(group)); //обновляем статус из базы данных

        if (group.isProcessing()) { //проверяем что не обрабатывается в данный момент
            throw new BindException(new AlreadyProcessingException(group), true, group);
        }

        group.setStatus(RequestFileStatus.BINDING);
        requestFileGroupBean.save(group);

        //очищаем колонки которые заполняются во время связывания и обработки для записей в таблицах payment и benefit
        //paymentBean.clearBeforeBinding(group.getPaymentFile().getId(), billingContext.getServiceIds());
        benefitBean.clearBeforeBinding(group.getBenefitFile().getId());

        //связывание файла payment
        RequestFile paymentFile = group.getPaymentFile();
        try {
            bindPaymentFile(paymentFile, updatePuAccount);
        } catch (DBException e) {
            throw new RuntimeException(e);
        } catch (CanceledByUserException e) {
            throw new BindException(e, true, group);
        }

        //связывание файла benefit
        RequestFile benefitFile = group.getBenefitFile();
        bindBenefitFile(benefitFile);

        //проверить все ли записи в payment файле связались
        if (!paymentBean.isPaymentFileBound(paymentFile.getId())) {
            throw new BindException(true, paymentFile);
        }

        //проверить все ли записи в benefit файле связались
        if (!benefitBean.isBenefitFileBound(benefitFile.getId())) {
            throw new BindException(true, benefitFile);
        }

        group.setStatus(RequestFileStatus.BOUND);
        requestFileGroupBean.save(group);

        return true;
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFileGroup group = (RequestFileGroup) executorObject;

        group.setStatus(RequestFileStatus.BIND_ERROR);
        requestFileGroupBean.save(group);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class getControllerClass() {
        return GroupBindTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.EDIT;
    }

    /**
     * Разрешить адрес по схеме "ОСЗН адрес -> локальная адресная база -> адрес центра начислений"
     * @param payment Запись запроса начислений
     * @return Разрешен ли адрес
     */
    private boolean resolveAddress(Payment payment) {
        addressService.resolveAddress(payment);

        return payment.getStatus().isAddressResolved();
    }

    /**
     * Разрешить номер личного счета из локальной таблицы person_account
     * @param payment Запись запроса начислений
     * @return Разрешен ли номер л/с
     */
    private void resolveLocalAccount(Payment payment) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(payment, payment.getStringField(OWN_NUM_SR));

            if (!Strings.isEmpty(accountNumber)) {
                payment.setAccountNumber(accountNumber);
                payment.setStatus(ACCOUNT_NUMBER_RESOLVED);
                benefitBean.updateAccountNumber(payment.getId(), accountNumber);
            }
        } catch (MoreOneAccountException e) {
            payment.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    /*
     * Связать payment запись
     * Алгоритм связывания для payment записи:
     * Попытаться разрешить л/c локально.
     * Если не успешно, то попытаться разрешить адрес по схеме "ОСЗН адрес -> локальная адресная база -> адрес центра начислений".
     * Если адрес разрешен, то пытаемся разрешить номер л/c в ЦН.
     */
    private void bind(Payment payment, Boolean updatePuAccount) throws DBException {
        String accountNumber = payment.getStringField(PaymentDBF.OWN_NUM_SR);

        //resolve local account number
        personAccountService.localResolveAccountNumber(payment, accountNumber, true);

        if (!ACCOUNT_NUMBER_RESOLVED.equals(payment.getStatus()) || !MORE_ONE_ACCOUNTS_LOCALLY.equals(payment.getStatus())){
            //resolve address
            addressService.resolveAddress(payment);

            //resolve account number
            if (payment.getStatus().isAddressResolved()){
                personAccountService.resolveAccountNumber(payment, accountNumber, null, updatePuAccount);

                if (MORE_ONE_ACCOUNTS.equals(payment.getStatus())){
                    personAccountService.forceResolveAccountNumber(payment, addressService.resolveOutgoingDistrict(
                            payment.getOrganizationId(), payment.getUserOrganizationId()), accountNumber);
                }
            }else{
                // Соответствие для дома не может быть установлено
                if (STREET_AND_BUILDING_UNRESOLVED_LOCALLY.equals(payment.getStatus()) || MORE_ONE_LOCAL_STREET_CORRECTION.equals(payment.getStatus())){
                    personAccountService.forceResolveAccountNumber(payment, addressService.resolveOutgoingDistrict(
                            payment.getOrganizationId(), payment.getUserOrganizationId()), accountNumber);
                }
            }
        }

        if (ACCOUNT_NUMBER_RESOLVED.equals(payment.getStatus())) {
            benefitBean.updateAccountNumber(payment.getId(), payment.getAccountNumber());
        }

        // обновляем payment запись
        paymentBean.update(payment);
    }

    /**
     * Связать payment файл.
     * @param paymentFile Файл запроса начислений
     * @throws BindException Ошибка связывания
     */
    private void bindPaymentFile(RequestFile paymentFile, Boolean updatePuAccount)
            throws BindException, DBException, CanceledByUserException {
        //извлечь из базы все id подлежащие связыванию для файла payment и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedPaymentIds = paymentBean.findIdsForBinding(paymentFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedPaymentIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedPaymentIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedPaymentIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<Payment> payments = paymentBean.findForOperation(paymentFile.getId(), batch);
            for (Payment payment : payments) {
                if (paymentFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать payment запись
                try {
                    userTransaction.begin();
                    bind(payment, updatePuAccount);
                    userTransaction.commit();
                } catch (Exception e) {
                    log.error("The payment item ( id = " + payment.getId() + ") was bound with error: ", e);

                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        log.error("Couldn't rollback transaction for binding payment item.", e1);
                    }

                    throw new BindException(e, false, paymentFile);
                }
            }
        }
    }

    /**
     * Связать benefit файл.
     * @param benefitFile Файл запросов льгот
     * @throws BindException Ошибка связывания
     */
    private void bindBenefitFile(RequestFile benefitFile) throws BindException {
        //каждой записи benefit проставить статус соответствующей записи payment
        benefitBean.updateBindingStatus(benefitFile.getId());
    }
}
