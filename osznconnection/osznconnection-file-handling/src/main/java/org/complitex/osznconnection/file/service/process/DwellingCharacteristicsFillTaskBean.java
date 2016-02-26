package org.complitex.osznconnection.file.service.process;

import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.OwnershipCorrectionBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.strategy.OwnershipStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * inheaven on 25.02.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DwellingCharacteristicsFillTaskBean implements ITaskBean<RequestFile>{
    private final Logger log = LoggerFactory.getLogger(SubsidyFillTaskBean.class);

    @Resource
    private UserTransaction userTransaction;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    @EJB
    private OwnershipStrategy ownershipStrategy;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(requestFile);

        //todo clear before filling

        try {
            List<Long> ids = dwellingCharacteristicsBean.findIdsForOperation(requestFile.getId());

            for (Long id : ids) {
                List<DwellingCharacteristics> dwellingCharacteristicsList = dwellingCharacteristicsBean
                        .findForOperation(requestFile.getId(), Collections.singletonList(id));

                for (DwellingCharacteristics dwellingCharacteristics : dwellingCharacteristicsList){
                    if (requestFile.isCanceled()){
                        throw new FillException(new CanceledByUserException(), true, requestFile);
                    }

                    userTransaction.begin();
                    fill(dwellingCharacteristics);
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
        if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileFilled(requestFile.getId())) {
            throw new FillException(true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLED);
        requestFileBean.save(requestFile);

        return true;
    }

    /**
     * Заполняются поля VL (код формы собственности через соответствие), PLZAG (общая площадь), PLOPAL (отапливаемая площадь).
     */
    private void fill(DwellingCharacteristics dwellingCharacteristics) throws DBException {
        if (dwellingCharacteristics.getAccountNumber() == null){
            return;
        }

        Cursor<PaymentAndBenefitData> cursor = serviceProviderAdapter.getPaymentAndBenefit(dwellingCharacteristics.getUserOrganizationId(),
                dwellingCharacteristics.getAccountNumber(), dwellingCharacteristics.getDate());

        if (cursor.getResultCode() == -1){
            dwellingCharacteristics.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);

            return;
        }

        if (cursor.getData().size() == 1){
            PaymentAndBenefitData data = cursor.getData().get(0);

            String ownership = null;
            Long ownershipId = ownershipCorrectionBean.findInternalOwnership(data.getOwnership(), dwellingCharacteristics.getBuildingId());

            if (ownershipId != null){
                DomainObject object = ownershipStrategy.getDomainObject(ownershipId);

                if (object != null){
                    ownership = object.getStringValue(OwnershipStrategy.NAME);
                }else{
                    log.warn("Форма собственности не найдена {} ко коррекции {}", ownershipId, data.getOwnership());
                }
            }else {
                log.warn("Форма собственности не найдена {}", data.getOwnership());
            }

            dwellingCharacteristics.setField(DwellingCharacteristicsDBF.VL, ownership);
            dwellingCharacteristics.setField(DwellingCharacteristicsDBF.PLZAG, data.getReducedArea());
            dwellingCharacteristics.setField(DwellingCharacteristicsDBF.PLOPAL, data.getHeatingArea());
        }else if(cursor.getData().size() > 1){
            dwellingCharacteristics.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
        }
    }

    @Override
    public void onError(RequestFile requestFile) {
        requestFile.setStatus(RequestFileStatus.FILL_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return  Module.NAME;
    }

    @Override
    public Class getControllerClass() {
        return DwellingCharacteristicsFillTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return  Log.EVENT.EDIT;
    }
}
