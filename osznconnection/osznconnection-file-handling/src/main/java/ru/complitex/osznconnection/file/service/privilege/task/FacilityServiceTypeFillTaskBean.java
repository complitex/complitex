package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.entity.Cursor;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.util.DateUtil;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.organization.strategy.ServiceStrategy;
import ru.complitex.osznconnection.file.entity.*;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import ru.complitex.osznconnection.file.entity.privilege.FacilityTarif;
import ru.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.BindException;
import ru.complitex.osznconnection.file.service.exception.FillException;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.warning.RequestWarningBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.file.strategy.PrivilegeStrategy;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.complitex.common.util.StringUtil.equalNotEmpty;
import static ru.complitex.common.util.StringUtil.isNotEmpty;
import static ru.complitex.osznconnection.file.entity.RequestFileStatus.FILL_ERROR;
import static ru.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;
import static ru.complitex.osznconnection.file.entity.RequestStatus.*;
import static ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.*;
import static ru.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_CODE;
import static ru.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_SERV;

/**
 * inheaven on 18.03.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeFillTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(FacilityServiceTypeFillTaskBean.class);

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
    private CorrectionBean correctionBean;

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

            List<Long> ids = facilityServiceTypeBean.findIdsForOperation(requestFile.getId());

            for (Long id : ids) {
                List<FacilityServiceType> facilityServiceTypes = facilityServiceTypeBean
                        .findForOperation(requestFile.getId(), Collections.singletonList(id));

                for (FacilityServiceType facilityServiceType : facilityServiceTypes){
                    if (requestFile.isCanceled()){
                        throw new FillException(new CanceledByUserException(), true, requestFile);
                    }

                    fill(facilityServiceType);
                    onRequest(facilityServiceType, ProcessType.FILL_PRIVILEGE_PROLONGATION);

                }
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
    public void fill(FacilityServiceType facilityServiceType){
        if (facilityServiceType.getAccountNumber() == null){
            return;
        }

        //service code
        String serviceCode = null;

        List<Correction> serviceCorrections = correctionBean.getCorrections(ServiceStrategy.SERVICE_ENTITY,
                facilityServiceType.getStringField(FacilityServiceTypeDBF.LGCODE),
                facilityServiceType.getOrganizationId(), facilityServiceType.getUserOrganizationId());

        if (!serviceCorrections.isEmpty()){
            Long externalId = serviceCorrections.get(0).getExternalId();

            serviceCode = externalId != null ? externalId.toString() : null;
        }

        if (serviceCode == null){
            facilityServiceType.setStatus(SERVICE_NOT_FOUND);
            facilityServiceTypeBean.update(facilityServiceType);

            return;
        }

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
                    facilityServiceType.putUpdateField(TARIF, null);
                    facilityServiceType.putUpdateField(RIZN, null);

                    facilityServiceType.setStatus(TARIF_NOT_FOUND);

                    RequestWarning warning = new RequestWarning(facilityServiceType.getId(), FACILITY_SERVICE_TYPE,
                            RequestWarningStatus.TARIF_NOT_FOUND);
                    warning.addParameter(new RequestWarningParameter(0, tarif));
                    requestWarningBean.save(warning);

                    log.info("TARIF_NOT_FOUND serviceCode={}, tarif={}, date={}", serviceCode, tarif, facilityServiceType.getDate());
                }
            }
        }

        //benefit data
        if (facilityServiceType.getStatus().is(PROCESSED, TARIF_NOT_FOUND)) {
            Cursor<BenefitData> cursor = serviceProviderAdapter.getBenefitData(facilityServiceType.getUserOrganizationId(),
                    facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

            if (cursor.getResultCode() == -1){
                facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
                facilityServiceTypeBean.update(facilityServiceType);

                return;
            }

            for (BenefitData bd : cursor.getData()){
                if ((equalNotEmpty(bd.getInn(), facilityServiceType.getInn())) ||
                        (isNotEmpty(facilityServiceType.getPassport()) &&
                                ((isNotEmpty(bd.getPassportSerial()) || isNotEmpty(bd.getPassportNumber())) &&
                                        facilityServiceType.getPassport().matches(bd.getPassportSerial() +
                                                "\\s*" + bd.getPassportNumber())))) {
                    if ("Ф".equals(bd.getBudget())) {
                        facilityServiceType.putUpdateField(KAT, bd.getCode());
                    }

                    if (bd.getDateIn() != null) {
                        facilityServiceType.putUpdateField(YEARIN, DateUtil.getYear(bd.getDateIn()));
                        facilityServiceType.putUpdateField(MONTHIN, DateUtil.getMonth(bd.getDateIn()) + 1);
                    }

                    if (bd.getDateOut() != null) {
                        facilityServiceType.putUpdateField(YEAROUT, DateUtil.getYear(bd.getDateOut()));
                        facilityServiceType.putUpdateField(MONTHOUT, DateUtil.getMonth(bd.getDateOut()) + 1);
                    }

                    facilityServiceType.putUpdateField(RAH, facilityServiceType.getAccountNumber());

                    break;
                }
            }
        }

        facilityServiceTypeBean.update(facilityServiceType);
    }
}
