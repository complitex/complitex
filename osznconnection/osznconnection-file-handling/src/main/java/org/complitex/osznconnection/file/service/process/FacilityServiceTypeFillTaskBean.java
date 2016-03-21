package org.complitex.osznconnection.file.service.process;

import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * inheaven on 18.03.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeFillTaskBean extends AbstractTaskBean<RequestFile>{
    private final Logger log = LoggerFactory.getLogger(FacilityServiceTypeFillTaskBean.class);

    @Resource
    private UserTransaction userTransaction;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(requestFile);

        try {
            List<Long> ids = facilityServiceTypeBean.findIdsForOperation(requestFile.getId());

            for (Long id : ids) {
                List<FacilityServiceType> facilityServiceTypes = facilityServiceTypeBean
                        .findForOperation(requestFile.getId(), Collections.singletonList(id));

                for (FacilityServiceType facilityServiceType : facilityServiceTypes){
                    if (requestFile.isCanceled()){
                        throw new FillException(new CanceledByUserException(), true, requestFile);
                    }

                    userTransaction.begin();
                    fill(facilityServiceType);
                    userTransaction.commit();
                }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки файла субсидии", e);

            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("", e1);
            }

            throw new RuntimeException(e);
        }

        //проверить все ли записи в файле субсидии обработались
        if (!facilityServiceTypeBean.isFacilityServiceTypeFileFilled(requestFile.getId())) {
            throw new FillException(true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLED);
        requestFileBean.save(requestFile);


        return true;
    }

    private void fill(FacilityServiceType facilityServiceType) throws DBException {
        if (facilityServiceType.getAccountNumber() == null){
            return;
        }

        Cursor<PaymentAndBenefitData> cursor = serviceProviderAdapter.getPaymentAndBenefit(facilityServiceType.getUserOrganizationId(),
                facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

        if (cursor.getResultCode() == -1){
            facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);

            return;
        }

        if (cursor.getData().size() == 1){
            PaymentAndBenefitData data = cursor.getData().get(0);


        }else {
            facilityServiceType.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
        }




    }

    @Override
    public void onError(RequestFile requestFile) {
        requestFile.setStatus(RequestFileStatus.FILL_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class getControllerClass() {
        return FacilityServiceTypeFillTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
