package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.*;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.BenefitBean;
import org.complitex.osznconnection.file.service.subsidy.PaymentBean;
import org.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;

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
public class GroupLoadTaskBean extends AbstractTaskBean {

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
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        RequestFileGroup group = (RequestFileGroup) executorObject;

        group.setStatus(RequestFileStatus.LOADING);

        requestFileGroupBean.save(group);

        RequestFile benefitFile = group.getBenefitFile();
        benefitFile.setGroupId(group.getId());
        benefitFile.setStatus(RequestFileStatus.LOADING);

        RequestFile paymentFile = group.getPaymentFile();
        paymentFile.setGroupId(group.getId());
        paymentFile.setStatus(RequestFileStatus.LOADING);

        //load payment
        boolean noSkip = loadRequestFileBean.load(paymentFile, new LoadRequestFileBean.AbstractLoadRequestFile() {

            @Override
            public Enum[] getFieldNames() {
                return PaymentDBF.values();
            }

            @Override
            public AbstractRequest newObject() {
                return new Payment();
            }

            @Override
            public void save(List<AbstractRequest> batch) {
                paymentBean.insert(batch);

                batch.forEach(r -> onRequest(r));
            }

            @Override
            public void postProcess(int rowNumber, AbstractRequest request) {
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
            boolean notLoaded = loadRequestFileBean.load(group.getBenefitFile(), new LoadRequestFileBean.AbstractLoadRequestFile() {

                @Override
                public Enum[] getFieldNames() {
                    return BenefitDBF.values();
                }

                @Override
                public AbstractRequest newObject() {
                    return new Benefit();
                }

                @Override
                public void save(List<AbstractRequest> batch) {
                    benefitBean.insert(batch);

                    batch.forEach(r -> onRequest(r));
                }

                @Override
                public void postProcess(int rowNumber, AbstractRequest request) {
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
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFileGroup group = (RequestFileGroup) executorObject;
        group.setStatus(RequestFileStatus.LOAD_ERROR);

        requestFileGroupBean.delete(group);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return GroupLoadTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
