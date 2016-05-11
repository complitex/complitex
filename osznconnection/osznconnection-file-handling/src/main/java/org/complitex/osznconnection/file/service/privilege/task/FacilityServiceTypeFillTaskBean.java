package org.complitex.osznconnection.file.service.privilege.task;

import com.google.common.base.Strings;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.util.DateUtil;
import org.complitex.correction.entity.ServiceCorrection;
import org.complitex.correction.service.ServiceCorrectionBean;
import org.complitex.organization.strategy.ServiceStrategy;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import org.complitex.osznconnection.file.entity.privilege.FacilityTarif;
import org.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.strategy.PrivilegeStrategy;
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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileStatus.FILL_ERROR;
import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;
import static org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.*;
import static org.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_CODE;
import static org.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_SERV;
import static org.complitex.osznconnection.file.strategy.PrivilegeStrategy.CODE;

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

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private ServiceStrategy serviceStrategy;

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    @EJB
    private PrivilegeStrategy privilegeStrategy;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

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

            //clear warning
            requestWarningBean.delete(requestFile.getId(), FACILITY_SERVICE_TYPE);

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
                        onRequest(facilityServiceType);

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
        } catch (Exception e) {
            requestFile.setStatus(FILL_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }

    /**
     * LGCODE - код услуги ПУ
     * KAT - категория льготы данного льготника через соответствия.
     * YEARIN - год начала действия льготы.
     * MONTHIN - месяц начала действия льготы.
     * YEAROUT - год окончания действия льготы.
     * MONTHOUT - месяц окончания действия льготы.
     * RAH - номер л/с ПУ (номер л/с с которым связана запись).
     * RIZN - код услуги через соответствия данного отдела льгот.
     * TARIF - код тарифа из справочника тарифов данного отдела льгот для данного кода услуги.
     */
    @SuppressWarnings("Duplicates")
    public void fill(FacilityServiceType facilityServiceType) throws DBException {
        if (facilityServiceType.getAccountNumber() == null){
            return;
        }

        //service code
        String serviceCode = null;

        List<ServiceCorrection> serviceCorrections = serviceCorrectionBean.getServiceCorrections(FilterWrapper.of(
                new ServiceCorrection(facilityServiceType.getStringField(FacilityServiceTypeDBF.LGCODE),
                        facilityServiceType.getOrganizationId(), facilityServiceType.getUserOrganizationId())));

        if (!serviceCorrections.isEmpty()){
            ServiceCorrection serviceCorrection = serviceCorrections.get(0);

            if (!Strings.isNullOrEmpty(serviceCorrection.getExternalId())){
                serviceCode = serviceCorrection.getExternalId();
            }else {
                DomainObject service = serviceStrategy.getDomainObject(serviceCorrection.getObjectId());

                if (service != null && !Strings.isNullOrEmpty(service.getStringValue(ServiceStrategy.CODE))){
                    serviceCode = service.getStringValue(CODE);
                }
            }
        }

        if (serviceCode == null){
            facilityServiceType.setStatus(SERVICE_NOT_FOUND);
            facilityServiceTypeBean.update(facilityServiceType);

            return;
        }

        //benefit data
        Cursor<BenefitData> cursor = serviceProviderAdapter.getBenefitData(facilityServiceType.getUserOrganizationId(),
                facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

        if (cursor.getResultCode() == -1){
            facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
            facilityServiceTypeBean.update(facilityServiceType);

            return;
        }

        for (BenefitData bd : cursor.getData()){
            if (facilityServiceType.getInn() != null && facilityServiceType.getInn().equals(bd.getInn())
                    || (facilityServiceType.getPassport() != null &&
                    facilityServiceType.getPassport().matches(bd.getPassportSerial() + "\\s*" + bd.getPassportNumber()))) {
                BenefitData data = cursor.getData().get(0);

                if ("Ф".equals(data.getBudget())) {
                    facilityServiceType.putUpdateField(KAT, data.getCode());
                }

                facilityServiceType.putUpdateField(YEARIN, DateUtil.getYear(data.getDateIn()));
                facilityServiceType.putUpdateField(MONTHIN, DateUtil.getMonth(data.getDateIn()) + 1);

                facilityServiceType.putUpdateField(YEAROUT, DateUtil.getYear(data.getDateOut()));
                facilityServiceType.putUpdateField(MONTHOUT, DateUtil.getMonth(data.getDateOut()) + 1);

                facilityServiceType.putUpdateField(RAH, facilityServiceType.getAccountNumber());

                Cursor<PaymentAndBenefitData> cursorTarif = serviceProviderAdapter.getPaymentAndBenefit(facilityServiceType.getUserOrganizationId(),
                        facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

                if (!cursorTarif.isEmpty()) {
                    PaymentAndBenefitData d = cursorTarif.getData().get(0);

                    BigDecimal tarif = null;

                    switch (serviceCode) {
                        case "500":
                            tarif = d.getApartmentFeeTarif();
                            break;
                        case "5061":
                            tarif = d.getGarbageDisposalTarif();
                            break;
                    }

                    if (tarif != null) {
                        FacilityTarif facilityTarif = new FacilityTarif();
                        facilityTarif.putField(FacilityTarifDBF.TAR_COST, tarif);
                        facilityTarif.putField(FacilityTarifDBF.TAR_CDPLG, serviceCode);

                        facilityTarif.setOrganizationId(facilityServiceType.getOrganizationId());
                        facilityTarif.setUserOrganizationId(facilityServiceType.getUserOrganizationId());
                        facilityTarif.setDate(facilityServiceType.getDate());

                        List<FacilityTarif> list = facilityReferenceBookBean.getFacilityTarifs(FilterWrapper.of(facilityTarif));

                        if (!list.isEmpty()) {
                            FacilityTarif ft = list.get(0);

                            facilityServiceType.putUpdateField(TARIF, ft.getField(TAR_CODE));
                            facilityServiceType.putUpdateField(RIZN, ft.getField(TAR_SERV));

                            facilityServiceType.setStatus(PROCESSED);
                        } else {
                            facilityServiceType.setStatus(TARIF_NOT_FOUND);

                            RequestWarning warning = new RequestWarning(facilityServiceType.getId(), FACILITY_SERVICE_TYPE,
                                    RequestWarningStatus.TARIF_NOT_FOUND);
                            warning.addParameter(new RequestWarningParameter(0, tarif));
                            requestWarningBean.save(warning);

                            log.info("TARIF_NOT_FOUND serviceCode={}, tarif={}, date={}", serviceCode, tarif, facilityServiceType.getDate());
                        }
                    }
                }

                facilityServiceTypeBean.update(facilityServiceType);

                return;
            }
        }

        facilityServiceType.setStatus(BENEFIT_OWNER_NOT_ASSOCIATED);
        facilityServiceTypeBean.update(facilityServiceType);
    }
}
