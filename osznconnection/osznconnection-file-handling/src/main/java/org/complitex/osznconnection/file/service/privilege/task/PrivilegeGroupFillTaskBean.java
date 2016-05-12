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
import org.complitex.osznconnection.file.entity.privilege.*;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.privilege.*;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileStatus.FILL_ERROR;
import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;
import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;
import static org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.*;
import static org.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_CODE;
import static org.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF.TAR_SERV;
import static org.complitex.osznconnection.file.strategy.PrivilegeStrategy.CODE;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeGroupFillTaskBean extends AbstractTaskBean<PrivilegeFileGroup>{
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
    private ServiceCorrectionBean serviceCorrectionBean;

    @EJB
    private DwellingCharacteristicsFillTaskBean dwellingCharacteristicsFillTaskBean;

    @EJB
    private FacilityServiceTypeFillTaskBean facilityServiceTypeFillTaskBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();
        RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

        if (requestFileBean.getRequestFileStatus(dwellingCharacteristicsRequestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(dwellingCharacteristicsRequestFile.getFullName()), true, dwellingCharacteristicsRequestFile);
        }
        if (requestFileBean.getRequestFileStatus(facilityServiceTypeRequestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(facilityServiceTypeRequestFile.getFullName()), true, facilityServiceTypeRequestFile);
        }

        //filling
        dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(dwellingCharacteristicsRequestFile);

        facilityServiceTypeRequestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(facilityServiceTypeRequestFile);

        //clear warning
        requestWarningBean.delete(dwellingCharacteristicsRequestFile.getId(), DWELLING_CHARACTERISTICS);
        requestWarningBean.delete(facilityServiceTypeRequestFile.getId(), FACILITY_SERVICE_TYPE);

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
            }
        } catch (Exception e) {
            throw new FillException(e, false, dwellingCharacteristicsRequestFile);
        }

        //filled
        if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileFilled(dwellingCharacteristicsRequestFile.getId())) {
            throw new FillException(true, dwellingCharacteristicsRequestFile);
        }
        if (!facilityServiceTypeBean.isFacilityServiceTypeFileFilled(facilityServiceTypeRequestFile.getId())) {
            throw new FillException(true, facilityServiceTypeRequestFile);
        }

        dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.FILLED);
        requestFileBean.save(dwellingCharacteristicsRequestFile);

        facilityServiceTypeRequestFile.setStatus(RequestFileStatus.FILLED);
        requestFileBean.save(facilityServiceTypeRequestFile);


        return false;
    }


    @SuppressWarnings("Duplicates")
    private void fill(PrivilegeGroup group) throws DBException {
        DwellingCharacteristics dwellingCharacteristics = group.getDwellingCharacteristics();
        FacilityServiceType facilityServiceType = group.getFacilityServiceType();

        if (dwellingCharacteristics.getAccountNumber() == null || facilityServiceType.getAccountNumber() == null){
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
        Cursor<BenefitData> cursor = serviceProviderAdapter.getBenefitData(dwellingCharacteristics.getUserOrganizationId(),
                dwellingCharacteristics.getAccountNumber(), dwellingCharacteristics.getDate());

        if (cursor.getResultCode() == -1){
            dwellingCharacteristics.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
            dwellingCharacteristicsBean.update(dwellingCharacteristics);

            facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
            facilityServiceTypeBean.update(facilityServiceType);

            return;
        }

        BenefitData benefitData = cursor.getData().stream()
                .filter(bd -> facilityServiceType.getInn() != null &&
                        facilityServiceType.getInn().equals(bd.getInn()) ||
                        (facilityServiceType.getPassport() != null && facilityServiceType.getPassport()
                                .matches(bd.getPassportSerial() + "\\s*" + bd.getPassportNumber())))
                .findAny()
                .orElse(null);

        if (benefitData == null){
            dwellingCharacteristics.setStatus(BENEFIT_OWNER_NOT_ASSOCIATED);
            dwellingCharacteristicsBean.update(dwellingCharacteristics);

            facilityServiceType.setStatus(BENEFIT_OWNER_NOT_ASSOCIATED);
            facilityServiceTypeBean.update(facilityServiceType);

            return;
        }

        //facilityServiceType
        if ("Ф".equals(benefitData.getBudget())) {
            facilityServiceType.putUpdateField(KAT, benefitData.getCode());
        }

        facilityServiceType.putUpdateField(YEARIN, DateUtil.getYear(benefitData.getDateIn()));
        facilityServiceType.putUpdateField(MONTHIN, DateUtil.getMonth(benefitData.getDateIn()) + 1);

        facilityServiceType.putUpdateField(YEAROUT, DateUtil.getYear(benefitData.getDateOut()));
        facilityServiceType.putUpdateField(MONTHOUT, DateUtil.getMonth(benefitData.getDateOut()) + 1);

        facilityServiceType.putUpdateField(RAH, facilityServiceType.getAccountNumber());

        Cursor<PaymentAndBenefitData> paymentAndBenefitData = serviceProviderAdapter.getPaymentAndBenefit(facilityServiceType.getUserOrganizationId(),
                facilityServiceType.getAccountNumber(), facilityServiceType.getDate());

        if (!paymentAndBenefitData.isEmpty()) {
            PaymentAndBenefitData d = paymentAndBenefitData.getData().get(0);

            //dwellingCharacteristics
            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLZAG, d.getReducedArea());
            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.PLOPAL, d.getHeatingArea());

            dwellingCharacteristics.setStatus(RequestStatus.PROCESSED);

//            String ownership = null;
//            Long ownershipId = ownershipCorrectionBean.findInternalOwnership(d.getOwnership(), dwellingCharacteristics.getUserOrganizationId());
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
//                log.warn("Форма собственности не найдена {}", d.getOwnership());
//            }

//            dwellingCharacteristics.putUpdateField(DwellingCharacteristicsDBF.VL, ownership);

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
                    facilityServiceType.setStatus(TARIF_NOT_FOUND);

                    RequestWarning warning = new RequestWarning(facilityServiceType.getId(), FACILITY_SERVICE_TYPE,
                            RequestWarningStatus.TARIF_NOT_FOUND);
                    warning.addParameter(new RequestWarningParameter(0, tarif));
                    requestWarningBean.save(warning);

                    log.info("TARIF_NOT_FOUND serviceCode={}, tarif={}, date={}", serviceCode, tarif, facilityServiceType.getDate());
                }
            }
        }

        dwellingCharacteristicsBean.update(dwellingCharacteristics);
        facilityServiceTypeBean.update(facilityServiceType);
    }

    @Override
    public void onError(PrivilegeFileGroup group) {
        group.getDwellingCharacteristicsRequestFile().setStatus(FILL_ERROR);
        requestFileBean.save(group.getDwellingCharacteristicsRequestFile());

        group.getFacilityServiceTypeRequestFile().setStatus(FILL_ERROR);
        requestFileBean.save(group.getFacilityServiceTypeRequestFile());
    }
}
