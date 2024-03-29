package ru.complitex.osznconnection.file.service.subsidy.task;

import com.google.common.collect.Lists;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.Payment;
import ru.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.AddressService;
import ru.complitex.osznconnection.file.service.PersonAccountService;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.BindException;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.BenefitBean;
import ru.complitex.osznconnection.file.service.subsidy.PaymentBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:56
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GroupBindTaskBean extends AbstractRequestTaskBean<RequestFileGroup> {
    private final Logger log = LoggerFactory.getLogger(GroupBindTaskBean.class);

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

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @Resource
    private UserTransaction userTransaction;

    @Override
    public boolean execute(RequestFileGroup group, Map commandParameters) throws ExecuteException {
        try {
            group.setStatus(requestFileGroupBean.getRequestFileStatus(group)); //обновляем статус из базы данных

            if (group.isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new BindException(new AlreadyProcessingException(group.getFullName()), true, group);
            }

            group.setStatus(RequestFileStatus.BINDING);
            requestFileGroupBean.save(group);

            //очищаем колонки которые заполняются во время связывания и обработки для записей в таблицах payment и benefit
            paymentBean.clearBeforeBinding(group.getPaymentFile().getId());
            benefitBean.clearBeforeBinding(group.getBenefitFile().getId());

            //связывание файла payment
            RequestFile paymentFile = group.getPaymentFile();
            try {
                bindPaymentFile(paymentFile);
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
        } catch (Exception e) {
            group.setStatus(RequestFileStatus.BIND_ERROR);
            requestFileGroupBean.save(group);

            throw e;
        }
    }


    /*
     * Связать payment запись
     * Алгоритм связывания для payment записи:
     * Попытаться разрешить л/c локально.
     * Если не успешно, то попытаться разрешить адрес по схеме "ОСЗН адрес -> локальная адресная база -> адрес центра начислений".
     * Если адрес разрешен, то пытаемся разрешить номер л/c в ЦН.
     */
    public void bind(String serviceProviderCode, Long billingId, Payment payment){
        String accountNumber = payment.getStringField(PaymentDBF.OWN_NUM_SR);

        //resolve local account number
        personAccountService.localResolveAccountNumber(payment, accountNumber, billingId, true);

        if (!ACCOUNT_NUMBER_RESOLVED.equals(payment.getStatus()) && !MORE_ONE_ACCOUNTS_LOCALLY.equals(payment.getStatus())){
            //resolve address
            addressService.resolveAddress(payment, billingId);

            //resolve account number
            if (payment.getStatus().isAddressResolved()){
                personAccountService.resolveAccountNumber(payment, accountNumber, serviceProviderCode, billingId, false);

                if (MORE_ONE_ACCOUNTS.equals(payment.getStatus())){
                    personAccountService.forceResolveAccountNumber(payment, addressService.resolveOutgoingDistrict(
                            payment.getOrganizationId(), payment.getUserOrganizationId()), serviceProviderCode, accountNumber);
                }
            }else if (MORE_ONE_LOCAL_STREET.equals(payment.getStatus())){
                personAccountService.forceResolveAccountNumber(payment, addressService.resolveOutgoingDistrict(
                        payment.getOrganizationId(), payment.getUserOrganizationId()), serviceProviderCode, accountNumber);
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
     * @param requestFile Файл запроса начислений
     * @throws BindException Ошибка связывания
     */
    private void bindPaymentFile(RequestFile requestFile)
            throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла payment и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedPaymentIds = paymentBean.findIdsForBinding(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedPaymentIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedPaymentIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedPaymentIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<Payment> payments = paymentBean.findForOperation(requestFile.getId(), batch);
            for (Payment payment : payments) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать payment запись
                bind(serviceProviderCode, billingId, payment);
                onRequest(payment, ProcessType.BIND_GROUP);
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
