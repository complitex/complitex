package org.complitex.osznconnection.file.service;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Разрешает номер л/c
 * @author Artem
 */
@Stateless
public class PersonAccountService extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(PersonAccountService.class);

    @EJB
    private PersonAccountBean personAccountBean;

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyService subsidyService;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    public String getAccountNumber(AbstractAccountRequest request, String puPersonAccount)
            throws MoreOneAccountException {
        Long billingId = organizationStrategy.getBillingId(request.getUserOrganizationId());

        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(new PersonAccount(request,
                puPersonAccount, billingId, false)));

        if (personAccounts.size() == 1){
            return personAccounts.get(0).getAccountNumber();
        }else if (personAccounts.size() > 1){
            throw new MoreOneAccountException();
        }

        return null;
    }

    public void save(AbstractAccountRequest request, String puPersonAccount)
            throws MoreOneAccountException {
        Long billingId = organizationStrategy.getBillingId(request.getUserOrganizationId());

        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(new PersonAccount(request,
                puPersonAccount, billingId, false)));
        if (personAccounts.isEmpty()){
            personAccountBean.save(new PersonAccount(request, puPersonAccount, billingId, true));
        }else if (personAccounts.size() == 1){
            PersonAccount personAccount = personAccounts.get(0);

            if (!personAccount.getAccountNumber().equals(request.getAccountNumber())){
                personAccountBean.save(personAccount);
            }
        }else {
            throw new MoreOneAccountException();
        }
    }

    public void resolveAccountNumber(AbstractAccountRequest request, String puPersonAccount,
                                     String servicingOrganizationCode,
                                     boolean updatePuAccount) throws DBException {
        try {
            //resolve local account
            String accountNumber = getAccountNumber(request, puPersonAccount);

            if (accountNumber != null) {
                request.setAccountNumber(accountNumber);
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                return;
            }

            //resolve remote account
            AccountDetail accountDetail = serviceProviderAdapter.acquireAccountDetail(request,
                    request.getLastName(), puPersonAccount,
                    request.getOutgoingDistrict(), request.getOutgoingStreetType(), request.getOutgoingStreet(),
                    request.getOutgoingBuildingNumber(), request.getOutgoingBuildingCorp(),
                    request.getOutgoingApartment(), request.getDate(), updatePuAccount);

            if (request.getStatus() == RequestStatus.ACCOUNT_NUMBER_RESOLVED) {
                //check servicing organization
                if (accountDetail.getZheu() != null && !accountDetail.getZheu().isEmpty() &&
                        servicingOrganizationCode != null && !servicingOrganizationCode.equals(accountDetail.getZheu())){
                    request.setStatus(RequestStatus.SERVICING_ORGANIZATION_NOT_FOUND);

                    return;
                }

                save(request, puPersonAccount);
            }
        } catch (MoreOneAccountException e) {
            request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    /**
     * Корректировать account number из UI в случае когда в ЦН больше одного человека соответствуют номеру л/c.
     */

    public void updateAccountNumber(Payment payment, String accountNumber) {
        payment.setAccountNumber(accountNumber);
        payment.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        benefitBean.updateAccountNumber(payment.getId(), accountNumber);
        paymentBean.updateAccountNumber(payment);

        long paymentFileId = payment.getRequestFileId();
        long benefitFileId = requestFileGroupBean.getBenefitFileId(paymentFileId);
        if (benefitBean.isBenefitFileBound(benefitFileId) && paymentBean.isPaymentFileBound(paymentFileId)) {
            requestFileGroupBean.updateStatus(benefitFileId, RequestFileStatus.BOUND);
        }

        try {
            save(payment, payment.getStringField(PaymentDBF.OWN_NUM_SR));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateAccountNumber(ActualPayment actualPayment, String accountNumber) {
        actualPayment.setAccountNumber(accountNumber);
        actualPayment.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        actualPaymentBean.updateAccountNumber(actualPayment);

        long actualPaymentFileId = actualPayment.getRequestFileId();
        RequestFile actualPaymentFile = requestFileBean.findById(actualPaymentFileId);

        if (actualPaymentBean.isActualPaymentFileBound(actualPaymentFileId)) {
            actualPaymentFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(actualPaymentFile);
        }

        try {
            save(actualPayment, actualPayment.getStringField(ActualPaymentDBF.OWN_NUM));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateAccountNumber(Subsidy subsidy, String accountNumber) {
        subsidy.setAccountNumber(accountNumber);
        subsidy.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        subsidyBean.updateAccountNumberForSimilarSubs(subsidy);

        long subsidyFileId = subsidy.getRequestFileId();
        RequestFile subsidyFile = requestFileBean.findById(subsidyFileId);

        if (subsidyBean.isSubsidyFileBound(subsidyFileId)) {
            subsidyFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(subsidyFile);
        }

        try {
            save(subsidy, subsidy.getStringField(SubsidyDBF.RASH));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccountNumber(DwellingCharacteristics dwellingCharacteristics, String accountNumber) {
        dwellingCharacteristics.setAccountNumber(accountNumber);
        dwellingCharacteristics.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        dwellingCharacteristicsBean.updateAccountNumber(dwellingCharacteristics);

        long dwellingCharacteristicsFileId = dwellingCharacteristics.getRequestFileId();
        RequestFile dwellingCharacteristicsFile = requestFileBean.findById(dwellingCharacteristicsFileId);

        if (dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(dwellingCharacteristicsFileId)) {
            dwellingCharacteristicsFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(dwellingCharacteristicsFile);
        }

        try {
            save(dwellingCharacteristics, dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.IDCODE));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateAccountNumber(FacilityServiceType facilityServiceType, String accountNumber) {
        facilityServiceType.setAccountNumber(accountNumber);
        facilityServiceType.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        facilityServiceTypeBean.updateAccountNumber(facilityServiceType);

        long facilityServiceTypeFileId = facilityServiceType.getRequestFileId();
        RequestFile facilityServiceTypeFile = requestFileBean.findById(facilityServiceTypeFileId);

        if (facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceTypeFileId)) {
            facilityServiceTypeFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(facilityServiceTypeFile);
        }

        try {
            save(facilityServiceType, facilityServiceType.getStringField(FacilityServiceTypeDBF.IDCODE));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }
}
