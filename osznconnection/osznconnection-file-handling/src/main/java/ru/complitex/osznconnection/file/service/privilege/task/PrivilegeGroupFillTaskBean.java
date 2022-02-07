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
import ru.complitex.osznconnection.file.entity.privilege.*;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.BindException;
import ru.complitex.osznconnection.file.service.exception.FillException;
import ru.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeGroupService;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.warning.RequestWarningBean;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
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
 * inheaven on 05.04.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeGroupFillTaskBean extends AbstractRequestTaskBean<PrivilegeFileGroup> {
    private Logger log = LoggerFactory.getLogger(PrivilegeGroupFillTaskBean.class);

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private RequestWarningBean requestWarningBean;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    @EJB
    private ServiceStrategy serviceStrategy;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private DwellingCharacteristicsFillTaskBean dwellingCharacteristicsFillTaskBean;

    @EJB
    private FacilityServiceTypeFillTaskBean facilityServiceTypeFillTaskBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        try {
            RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();

            if (dwellingCharacteristicsRequestFile != null){
                filling(dwellingCharacteristicsRequestFile);
            }

            RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

            if (facilityServiceTypeRequestFile != null){
                filling(facilityServiceTypeRequestFile);
            }

            try {
                List<PrivilegeGroup> privilegeGroups = privilegeGroupService.getPrivilegeGroups(group.getId());
                for (PrivilegeGroup p : privilegeGroups){
                    if (group.isCanceled()){
                        throw new CanceledByUserException();
                    }

                    if (p.getFacilityServiceType() != null && p.getDwellingCharacteristics() != null){
                        fill(p);
                    }else if (p.getDwellingCharacteristics() != null){
                        dwellingCharacteristicsFillTaskBean.fill(p.getDwellingCharacteristics());
                    }else if (p.getFacilityServiceType() != null){
                        facilityServiceTypeFillTaskBean.fill(p.getFacilityServiceType());
                    }

                    onRequest(p, ProcessType.LOAD_PRIVILEGE_GROUP);
                }
            } catch (Exception e) {
                throw new FillException(e, false, dwellingCharacteristicsRequestFile != null
                        ? dwellingCharacteristicsRequestFile : facilityServiceTypeRequestFile);
            }

            //filled
            if (dwellingCharacteristicsRequestFile != null) {
                if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileFilled(dwellingCharacteristicsRequestFile.getId())) {
                    throw new FillException(true, dwellingCharacteristicsRequestFile);
                }

                dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.FILLED);
                requestFileBean.save(dwellingCharacteristicsRequestFile);
            }

            if (facilityServiceTypeRequestFile != null){
                if (!facilityServiceTypeBean.isFacilityServiceTypeFileFilled(facilityServiceTypeRequestFile.getId())) {
                    throw new FillException(true, facilityServiceTypeRequestFile);
                }

                facilityServiceTypeRequestFile.setStatus(RequestFileStatus.FILLED);
                requestFileBean.save(facilityServiceTypeRequestFile);
            }

            return false;
        } catch (Exception e) {
            if (group.getDwellingCharacteristicsRequestFile() != null) {
                group.getDwellingCharacteristicsRequestFile().setStatus(FILL_ERROR);
                requestFileBean.save(group.getDwellingCharacteristicsRequestFile());
            }

            if (group.getFacilityServiceTypeRequestFile() != null) {
                group.getFacilityServiceTypeRequestFile().setStatus(FILL_ERROR);
                requestFileBean.save(group.getFacilityServiceTypeRequestFile());
            }

            throw e;
        }
    }

    private void filling(RequestFile requestFile) throws BindException {
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(requestFile);

        requestWarningBean.delete(requestFile.getId(), requestFile.getType());
    }

    @SuppressWarnings("Duplicates")
    private void fill(PrivilegeGroup group){
        DwellingCharacteristics dwellingCharacteristics = group.getDwellingCharacteristics();
        FacilityServiceType facilityServiceType = group.getFacilityServiceType();

        if (dwellingCharacteristics.getAccountNumber() == null || facilityServiceType.getAccountNumber() == null){
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

            dwellingCharacteristics.setStatus(SERVICE_NOT_FOUND);
            dwellingCharacteristicsBean.update(dwellingCharacteristics);

            return;
        }

        //payment and benefit data
        Cursor<PaymentAndBenefitData> paymentAndBenefitData = serviceProviderAdapter.getPaymentAndBenefit(facilityServiceType.getUserOrganizationId(),
                facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

        if (!paymentAndBenefitData.isEmpty()) {
            PaymentAndBenefitData d = paymentAndBenefitData.getData().get(0);

            //dwellingCharacteristics
            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLZAG, d.getReducedArea());
            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLOPAL, d.getHeatingArea());

            dwellingCharacteristics.setStatus(PROCESSED);

            //facilityServiceType
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
        }else {
            facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
        }

        //benefit data
        if (facilityServiceType.getStatus().is(PROCESSED, TARIF_NOT_FOUND)) {
            Cursor<BenefitData> cursor = serviceProviderAdapter.getBenefitData(dwellingCharacteristics.getUserOrganizationId(),
                    dwellingCharacteristics.getAccountNumber(), dwellingCharacteristics.getDate());

            if (cursor.getResultCode() == 0){
                facilityServiceType.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
            }else if (!cursor.getData().isEmpty()){
                BenefitData benefitData = cursor.getData().stream()
                        .filter(bd -> (equalNotEmpty(bd.getInn(), facilityServiceType.getInn())) ||
                                (isNotEmpty(facilityServiceType.getPassport()) &&
                                        ((isNotEmpty(bd.getPassportSerial()) || isNotEmpty(bd.getPassportNumber())) &&
                                                facilityServiceType.getPassport().matches(bd.getPassportSerial() +
                                                        "\\s*" + bd.getPassportNumber()))))
                        .findAny()
                        .orElse(null);

                if (benefitData != null){
                    if ("Ф".equals(benefitData.getBudget())) {
                        facilityServiceType.putUpdateField(KAT, benefitData.getCode());
                    }

                    if (benefitData.getDateIn() != null) {
                        facilityServiceType.putUpdateField(YEARIN, DateUtil.getYear(benefitData.getDateIn()));
                        facilityServiceType.putUpdateField(MONTHIN, DateUtil.getMonth(benefitData.getDateIn()) + 1);
                    }

                    if (benefitData.getDateOut() != null) {
                        facilityServiceType.putUpdateField(YEAROUT, DateUtil.getYear(benefitData.getDateOut()));
                        facilityServiceType.putUpdateField(MONTHOUT, DateUtil.getMonth(benefitData.getDateOut()) + 1);
                    }

                    facilityServiceType.putUpdateField(RAH, facilityServiceType.getAccountNumber());
                }else{
                    facilityServiceType.setStatus(BENEFIT_OWNER_NOT_ASSOCIATED);

                    requestWarningBean.delete(facilityServiceType);

                    requestWarningBean.save(facilityServiceType.getRequestFileType(), facilityServiceType.getId(),
                            RequestWarningStatus.NO_MATCH);
                }
            }else {
                facilityServiceType.setStatus(BENEFIT_OWNER_NOT_ASSOCIATED);

                requestWarningBean.delete(facilityServiceType);

                requestWarningBean.save(facilityServiceType.getRequestFileType(), facilityServiceType.getId(),
                        RequestWarningStatus.NO_DATA);
            }
        }

        dwellingCharacteristicsBean.update(dwellingCharacteristics);
        facilityServiceTypeBean.update(facilityServiceType);
    }
}
