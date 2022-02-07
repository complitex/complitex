package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.*;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.BenefitBean;
import ru.complitex.osznconnection.file.service.subsidy.PaymentBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:57
 */
@Stateless(name = "GroupLoadTaskBean")
@TransactionManagement(TransactionManagementType.BEAN)
public class GroupLoadTaskBean extends AbstractRequestTaskBean<RequestFileGroup> {

    @EJB
    protected PaymentBean paymentBean;
    @EJB
    protected BenefitBean benefitBean;
    @EJB
    protected RequestFileBean requestFileBean;
    @EJB
    private RequestFileGroupBean requestFileGroupBean;
    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @Override
    public boolean execute(RequestFileGroup group, Map commandParameters) throws ExecuteException {
        try {
            group.setStatus(RequestFileStatus.LOADING);

            requestFileGroupBean.save(group);

            RequestFile benefitFile = group.getBenefitFile();
            benefitFile.setGroupId(group.getId());
            benefitFile.setStatus(RequestFileStatus.LOADING);

            RequestFile paymentFile = group.getPaymentFile();
            paymentFile.setGroupId(group.getId());
            paymentFile.setStatus(RequestFileStatus.LOADING);

            //load payment
            boolean noSkip = loadRequestFileBean.load(paymentFile, new AbstractLoadRequestFile<Payment>() {

                @Override
                public Enum[] getFieldNames() {
                    return PaymentDBF.values();
                }

                @Override
                public Payment newObject() {
                    return new Payment();
                }

                @Override
                public void save(List<Payment> batch) {
                    paymentBean.insert(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_GROUP));
                }

                @Override
                public void postProcess(int rowNumber, Payment request) {
                    //установка номера реестра
    //                if (rowNumber == 0) {
    //                    Payment payment = (Payment) request;
    //
    //                    Long registry = (Long) payment.getField(PaymentDBF.REE_NUM);
    //
    //                    if (registry != null) {
    //                        //paymentFile.setRegistry(registry.intValue());
    //                    }
    //                }
                }
            });

            // load benefit
            if (noSkip) {
                boolean notLoaded = loadRequestFileBean.load(group.getBenefitFile(), new AbstractLoadRequestFile<Benefit>() {

                    @Override
                    public Enum[] getFieldNames() {
                        return BenefitDBF.values();
                    }

                    @Override
                    public Benefit newObject() {
                        return new Benefit();
                    }

                    @Override
                    public void save(List<Benefit> batch) {
                        benefitBean.insert(batch);

                        batch.forEach(r -> onRequest(r, ProcessType.LOAD_GROUP));
                    }

                    @Override
                    public void postProcess(int rowNumber, Benefit request) {
                        //установка номера реестра
    //                    if (rowNumber == 0) {
    //                        Benefit benefit = (Benefit) request;
    //
    //                        Long registry = (Long) benefit.getField(BenefitDBF.REE_NUM);
    //
    //                        if (registry != null) {
    //                            benefitFile.setRegistry(registry.intValue());
    //                        }
    //                    }
                    }
                });

                if (!notLoaded) {
                    throw new ExecuteException("Файл начислений {0} уже загружен.", group.getBenefitFile().getFullName());
                }
            } else {
                requestFileGroupBean.clear(group); //no cascading remove group
                group.setStatus(RequestFileStatus.SKIPPED);

                return false; //skip - file already loaded
            }

            group.setStatus(RequestFileStatus.LOADED);
            requestFileGroupBean.save(group);

            return true;
        } catch (Exception e) {
            group.setStatus(RequestFileStatus.LOAD_ERROR);
            requestFileGroupBean.delete(group);

            throw e;
        }
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
