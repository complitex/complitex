package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.PaymentAndBenefitData;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.privilege.OwnershipCorrectionBean;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;

/**
 * inheaven on 25.02.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DwellingCharacteristicsFillTaskBean extends AbstractTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(DwellingCharacteristicsFillTaskBean.class);

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

    @EJB
    private RequestWarningBean requestWarningBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()){
                throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            //todo clear before filling

            //clear warning
            requestWarningBean.delete(requestFile.getId(), DWELLING_CHARACTERISTICS);

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
                        onRequest(dwellingCharacteristics);
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
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }

    /**
     * Заполняются поля VL (код формы собственности через соответствие), PLZAG (общая площадь), PLOPAL (отапливаемая площадь).
     */
    @SuppressWarnings("Duplicates")
    public void fill(DwellingCharacteristics dwellingCharacteristics) throws DBException {
        if (dwellingCharacteristics.getAccountNumber() == null){
            return;
        }

        Cursor<PaymentAndBenefitData> cursor = serviceProviderAdapter.getPaymentAndBenefit(dwellingCharacteristics.getUserOrganizationId(),
                dwellingCharacteristics.getAccountNumber(), dwellingCharacteristics.getDate());

        if (cursor.getResultCode() == -1){
            dwellingCharacteristics.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
            dwellingCharacteristicsBean.update(dwellingCharacteristics);

            return;
        }

        if (cursor.getData().size() == 1){
            PaymentAndBenefitData data = cursor.getData().get(0);

            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLZAG, data.getReducedArea());
            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLOPAL, data.getHeatingArea());

            dwellingCharacteristics.setStatus(RequestStatus.PROCESSED);

//            String ownership = null;
//            Long ownershipId = ownershipCorrectionBean.findInternalOwnership(data.getOwnership(), dwellingCharacteristics.getUserOrganizationId());
//
//            if (ownershipId != null){
//                ownership = ownershipCorrectionBean.findOwnershipCode(ownershipId, dwellingCharacteristics.getOrganizationId(),
//                        dwellingCharacteristics.getUserOrganizationId());
//
//                dwellingCharacteristics.setStatus(RequestStatus.PROCESSED);
//            }
//
//            if (ownership == null){
//                dwellingCharacteristics.setStatus(RequestStatus.OWNERSHIP_NOT_FOUND);
//                dwellingCharacteristicsBean.update(dwellingCharacteristics);
//
//                log.warn("Форма собственности не найдена {}", data.getOwnership());
//
//                return;
//            }

//            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.VL, ownership);
        }else if(cursor.getData().size() > 1){
            dwellingCharacteristics.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
        }

        dwellingCharacteristicsBean.update(dwellingCharacteristics);
    }

    @Override
    public String getModuleName() {
        return  Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return  Log.EVENT.EDIT;
    }
}
