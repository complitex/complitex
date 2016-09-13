package org.complitex.osznconnection.file.service.subsidy.task;

import com.google.common.collect.Lists;
import org.complitex.common.entity.Log;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:56
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ActualPaymentFillTaskBean extends AbstractTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(ActualPaymentFillTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter adapter;


    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            //проверяем что не обрабатывается в данный момент
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
                throw new FillException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            //обработка файла actualPayment
            try {
                processActualPayment(requestFile);
            } catch (CanceledByUserException e) {
                throw new FillException(e, true, requestFile);
            }

            //проверить все ли записи в actualPayment файле обработались
            if (!actualPaymentBean.isActualPaymentFileProcessed(requestFile.getId())) {
                throw new FillException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileBean.save(requestFile);

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

    private void process(ActualPayment actualPayment, Date date){
        if (RequestStatus.unboundStatuses().contains(actualPayment.getStatus())) {
            return;
        }

//        for (BillingContext billingContext : billingContexts) { todo process payments
//            long startTime = 0;
//            if (log.isDebugEnabled()) {
//                startTime = System.nanoTime();
//            }
//            adapter.processActualPayment(billingContext, actualPayment, date);
//            log.debug("Processing actualPayment (id = {}, calculation center id: {}) took {} sec.",
//                    new Object[]{
//                        actualPayment.getId(),
//                        billingContext.getBillingId(),
//                        (System.nanoTime() - startTime) / 1000000000F
//                    });
//
//            /*
//             * если actualPayment обработан некорректно текущим модулем начислений, то прерываем обработку данной записи
//             * оставшимися модулями.
//             */
//            if (actualPayment.getStatus() != RequestStatus.PROCESSED) {
//                break;
//            }
//        }

        long startTime = 0;
        if (log.isDebugEnabled()) {
            startTime = System.nanoTime();
        }

        Set<Long> serviceIds = new HashSet<>(); //todo service ids

        actualPaymentBean.update(actualPayment, serviceIds);
        log.debug("Updating of actualPayment (id = {}) took {} sec.", actualPayment.getId(), (System.nanoTime() - startTime) / 1000000000F);
    }

    private void processActualPayment(RequestFile actualPaymentFile)
            throws FillException, CanceledByUserException {
        //извлечь из базы все id подлежащие обработке для файла actualPayment и доставать записи порциями по BATCH_SIZE штук.
        long startTime = 0;
        if (log.isDebugEnabled()) {
            startTime = System.nanoTime();
        }
        List<Long> notResolvedPaymentIds = actualPaymentBean.findIdsForProcessing(actualPaymentFile.getId());
        log.debug("Finding of actualPayment ids for processing took {} sec.", (System.nanoTime() - startTime) / 1000000000F);
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.FILL_BATCH_SIZE, true);

        while (notResolvedPaymentIds.size() > 0) {
            batch.clear();
            for (int i = 0; i < Math.min(batchSize, notResolvedPaymentIds.size()); i++) {
                batch.add(notResolvedPaymentIds.remove(i));
            }

            //достать из базы очередную порцию записей
            List<ActualPayment> actualPayments = actualPaymentBean.findForOperation(actualPaymentFile.getId(), batch);
            for (ActualPayment actualPayment : actualPayments) {
                if (actualPaymentFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //обработать actualPayment запись
                process(actualPayment, actualPaymentBean.getFirstDay(actualPayment, actualPaymentFile));
                onRequest(actualPayment);
            }
        }
    }
}
