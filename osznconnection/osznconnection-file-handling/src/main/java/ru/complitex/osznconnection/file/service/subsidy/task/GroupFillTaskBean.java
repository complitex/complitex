package ru.complitex.osznconnection.file.service.subsidy.task;

import com.google.common.collect.Lists;
import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.subsidy.Benefit;
import ru.complitex.osznconnection.file.entity.subsidy.Payment;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.FillException;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.BenefitBean;
import ru.complitex.osznconnection.file.service.subsidy.PaymentBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:56
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GroupFillTaskBean extends AbstractRequestTaskBean<RequestFileGroup> {
    private final Logger log = LoggerFactory.getLogger(GroupFillTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private ServiceProviderAdapter adapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @Override
    public boolean execute(RequestFileGroup group, Map commandParameters) throws ExecuteException {
        try {
            group.setStatus(requestFileGroupBean.getRequestFileStatus(group)); //обновляем статус из базы данных

            if (group.isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new FillException(new AlreadyProcessingException(group.getFullName()), true, group);
            }

            group.setStatus(RequestFileStatus.FILLING);
            requestFileGroupBean.save(group);

            //очищаем колонки которые заполняются во время обработки для записей в таблицах payment и benefit
            paymentBean.clearBeforeProcessing(group.getPaymentFile().getId(),
                    organizationStrategy.getServices(group.getPaymentFile().getUserOrganizationId()));
            benefitBean.clearBeforeProcessing(group.getBenefitFile().getId());

            //обработка файла payment
            processPayment(group.getPaymentFile());

            //обработка файла benefit
            processBenefit(group.getBenefitFile());

            //проверить все ли записи в payment файле обработались
            if (!paymentBean.isPaymentFileProcessed(group.getPaymentFile().getId())) {
                throw new FillException(true, group.getPaymentFile());
            }

            if (!benefitBean.isBenefitFileProcessed(group.getBenefitFile().getId())) {
                throw new FillException(true, group.getBenefitFile());
            }

            group.setStatus(RequestFileStatus.FILLED);
            requestFileGroupBean.save(group);

            return true;
        } catch (Exception e) {
            group.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileGroupBean.save(group);

            throw e;
        }
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.EDIT;
    }

    /**
     * Обрабатывает payment.
     * Алгоритм:
     * Если статус payment записи указывает на то что запись даже не связана, то пропускаем запись.(Такая ситуация
     * возможна т.к. существует требование обрабатывать файла, которые связаны с ошибками)
     * Иначе вызываем DefaultCalculationCenterAdapter.processPaymentAndBenefit() для заполнения
     * некоторых полей в payment и benefit записях. Наконец, обновляем payment и соответствующие ему benefit
     * записи(BenefitBean.populateBenefit()).
     *
     * @param payment Запись запроса начислений
     */
    private void process(Payment payment){
//        if (RequestStatus.unboundStatuses().contains(payment.getStatus())) {
//            return;
//        }

        List<Benefit> benefits = benefitBean.findByOZN(payment);
        Map<Long, Set<Long>> billingServices = organizationStrategy.getBillingServices(payment.getUserOrganizationId());

        for (Long billingId : billingServices.keySet()) {
            adapter.processPaymentAndBenefit(billingId, billingServices.get(billingId), payment, benefits);

            /* если payment обработан некорректно текущим модулем начислений, то прерываем обработку данной записи
             * оставшимися модулями.
             */
            if (payment.getStatus() != RequestStatus.PROCESSED) {
                break;
            }
        }

        paymentBean.update(payment, organizationStrategy.getServices(payment.getUserOrganizationId()));

        for (Benefit benefit : benefits) {
            benefitBean.populateBenefit(benefit);
        }
    }

    /**
     * Обработать payment файл.
     * @param paymentFile Файл запроса начислений
     * @throws FillException Ошибка обработки
     */
    private void processPayment(RequestFile paymentFile) throws FillException {
        //извлечь из базы все id подлежащие обработке для файла payment и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedPaymentIds = paymentBean.findIdsForProcessing(paymentFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.FILL_BATCH_SIZE, true);

        while (notResolvedPaymentIds.size() > 0) {
            batch.clear();
            for (int i = 0; i < Math.min(batchSize, notResolvedPaymentIds.size()); i++) {
                batch.add(notResolvedPaymentIds.remove(i));
            }

            //достать из базы очередную порцию записей
            List<Payment> payments = paymentBean.findForOperation(paymentFile.getId(), batch);
            for (Payment payment : payments) {
                if (paymentFile.isCanceled()) {
                    throw new FillException(new CanceledByUserException(), true, paymentFile);
                }

                //обработать payment запись
                process(payment);
                onRequest(payment, ProcessType.FILL_GROUP);
            }
        }
    }

    /**
     * Обработать benefit файл.
     * Алгоритм:
     * Извлечь все не null account numbers в benefit файле(BenefitBean.getAllAccountNumbers()).
     * Для каждого account number достаем из базы benefit записи с таким account number(BenefitBean.findByAccountNumber().
     * Методом BenefitBean.findDat1() достает дату из поля DAT1 в записи payment, у которой account number такой же
     * (т.е. payment соответствующий данной группе benefit записей) и
     * кроме того поле FROG больше 0(только benefit записи соответствующие таким payment записям нужно обрабатывать).
     * Дата нужна как параметр для вызова
     * ru.complitex.osznconnection.file.calculation.adapter.DefaultCalculationCenterAdapter.processBenefit().
     * И если дата не null, то вызываем processBenefit() для заполнения полей в benefit записях. У тех групп benefit
     * записей, у которых дата не нашлась, т.е. соответствующий payment имеет в поле FROG значение 0, проставляем
     * статус RequestStatus.PROCESSED. Наконец, обновляем все benefit записи.
     *
     * @param benefitFile Файл запроса льгот
     * @throws FillException Ошибка обработки
     */
    private void processBenefit(RequestFile benefitFile) throws FillException {
        List<String> allAccountNumbers = benefitBean.getAllAccountNumbers(benefitFile.getId());

        for (String accountNumber : allAccountNumbers) {
            List<Benefit> benefits = benefitBean.findByAccountNumber(accountNumber, benefitFile.getId());

            if (benefits != null && !benefits.isEmpty()) {
                Date dat1 = paymentBean.findDat1(accountNumber, benefitFile.getId());

                if (dat1 != null) {
                    adapter.processBenefit(dat1, benefits);
                } else {
                    for (Benefit benefit : benefits) {
                        benefit.setStatus(RequestStatus.PROCESSED);
                    }
                }

                for (Benefit benefit : benefits) {
                    try {
                        benefitBean.update(benefit);

                        onRequest(benefit, ProcessType.FILL_GROUP);
                    } catch (Exception e) {
                        log.error("The benefit item (id = " + benefit.getId() + ") was processed with error: ", e);
                        throw new FillException(e, false, benefitFile);
                    }
                }
            }
        }
    }
}
