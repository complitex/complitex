package org.complitex.osznconnection.file.service;

import org.complitex.address.util.AddressUtil;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Set;

import static org.complitex.address.util.AddressUtil.replaceApartmentSymbol;
import static org.complitex.address.util.AddressUtil.replaceBuildingNumberSymbol;

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

    @EJB
    private AddressService addressService;

    public String getLocalAccountNumber(AbstractAccountRequest request, String accountNumber) throws MoreOneAccountException {
        return getLocalAccountNumber(request, accountNumber, false);
    }

    public String getLocalAccountNumber(AbstractAccountRequest request, String accountNumber, boolean useAddressNames)
            throws MoreOneAccountException {
        Long billingId = organizationStrategy.getBillingId(request.getUserOrganizationId());

        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(new PersonAccount(request,
                accountNumber, billingId, useAddressNames)));

        if (personAccounts.size() == 1){
            return personAccounts.get(0).getAccountNumber();
        }else if (personAccounts.size() > 1){
            throw new MoreOneAccountException();
        }

        return null;
    }

    public void save(AbstractAccountRequest request, String puAccountNumber) throws MoreOneAccountException {
        Long billingId = organizationStrategy.getBillingId(request.getUserOrganizationId());

        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(new PersonAccount(request,
                puAccountNumber, billingId, false)));

        if (personAccounts.isEmpty()){
            personAccountBean.save(new PersonAccount(request, puAccountNumber, billingId, true));
        }else if (personAccounts.size() == 1){
            PersonAccount personAccount = personAccounts.get(0);

            if (!personAccount.getAccountNumber().equals(request.getAccountNumber())){
                personAccountBean.save(personAccount);
            }
        }else {
            throw new MoreOneAccountException();
        }
    }

    public void localResolveAccountNumber(AbstractAccountRequest request, String puAccountNumber, boolean useAddressNames){
        try {
            String localAccountNumber = getLocalAccountNumber(request, puAccountNumber, useAddressNames);

            if (localAccountNumber != null) {
                request.setAccountNumber(localAccountNumber);
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    public void resolveAccountNumber(AbstractAccountRequest request, String puAccountNumber,
                                     String serviceProviderCode,
                                     boolean updatePuAccount) throws DBException {
        try {
            //resolve local account
            String localAccountNumber = getLocalAccountNumber(request, puAccountNumber, false);

            if (localAccountNumber != null) {
                request.setAccountNumber(localAccountNumber);
                request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                return;
            }

            //resolve remote account
            AccountDetail accountDetail = serviceProviderAdapter.acquireAccountDetail(request,
                    request.getLastName(), puAccountNumber,
                    request.getOutgoingDistrict(), serviceProviderCode, request.getOutgoingStreetType(), request.getOutgoingStreet(),
                    request.getOutgoingBuildingNumber(), request.getOutgoingBuildingCorp(),
                    request.getOutgoingApartment(), request.getDate(), updatePuAccount);

            if (request.getStatus() == RequestStatus.ACCOUNT_NUMBER_RESOLVED) {
                //check servicing organization
                if (serviceProviderCode != null && accountDetail.getZheu() != null &&
                        !serviceProviderCode.toUpperCase().equals(accountDetail.getZheu().toUpperCase())){
                    request.setStatus(RequestStatus.SERVICING_ORGANIZATION_NOT_FOUND);

                    return;
                }

                save(request, puAccountNumber);
            }
        } catch (MoreOneAccountException e) {
            request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    public void forceResolveAccountNumber(AbstractAccountRequest request, String district, String serviceProviderCode,
                                          String accountNumber) throws DBException{
        try {
            //force resolve remote account
            List<AccountDetail> accountDetails = serviceProviderAdapter.acquireAccountDetailsByAccount(request, district,
                    serviceProviderCode, accountNumber, request.getDate());

            if (accountDetails != null) {
                if (accountDetails.size() == 1){
                    AccountDetail detail = accountDetails.get(0);

                    Set<String> streetNames = addressService.getStreetNames(request);

                    boolean bond = detail.getStreet() != null && detail.getBuildingNumber() != null && detail.getApartment() != null;

                    if (bond){
                        bond = detail.getStreet().equalsIgnoreCase(request.getOutgoingStreet()) ||
                                streetNames.contains(detail.getStreet().toUpperCase());
                    }

                    if (bond){
                        bond = detail.getBuildingNumber().equalsIgnoreCase(request.getOutgoingBuildingNumber()) ||
                                (request.getBuildingNumber() != null &&
                                        detail.getBuildingNumber().equalsIgnoreCase(replaceBuildingNumberSymbol(request.getBuildingNumber())));
                    }

                    if (bond){
                        bond = detail.getBuildingCorp() == null || detail.getBuildingCorp().equalsIgnoreCase(request.getOutgoingBuildingCorp()) ||
                                (request.getBuildingCorp() != null &&
                                        detail.getBuildingCorp().equalsIgnoreCase(AddressUtil.replaceBuildingCorpSymbol(request.getBuildingCorp())));
                    }

                    if (bond){
                        bond = detail.getApartment().equalsIgnoreCase(request.getOutgoingApartment()) ||
                                (request.getApartment() != null &&
                                        detail.getApartment().equalsIgnoreCase(replaceApartmentSymbol(request.getApartment())));
                    }

                    if (bond){
                        request.setAccountNumber(detail.getAccCode());
                        request.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);

                        save(request, accountNumber);
                    }else {
                        request.setStatus(RequestStatus.ACCOUNT_NUMBER_MISMATCH);
                    }
                }else if (accountDetails.size() > 1){
                    request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS);
                }
            }
        } catch (UnknownAccountNumberTypeException e) {
            log.error("error forceResolveAccountNumber", e);
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
        RequestFile actualPaymentFile = requestFileBean.getRequestFile(actualPaymentFileId);

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
        RequestFile subsidyFile = requestFileBean.getRequestFile(subsidyFileId);

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
        RequestFile dwellingCharacteristicsFile = requestFileBean.getRequestFile(dwellingCharacteristicsFileId);

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
        RequestFile facilityServiceTypeFile = requestFileBean.getRequestFile(facilityServiceTypeFileId);

        if (facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceTypeFileId)) {
            facilityServiceTypeFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(facilityServiceTypeFile);
        }

        try {
            save(facilityServiceType, facilityServiceType.getStringField(FacilityServiceTypeDBF.IDPIL));
        } catch (MoreOneAccountException e) {
            throw new RuntimeException(e);
        }
    }
}
