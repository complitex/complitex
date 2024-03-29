package ru.complitex.osznconnection.file.service;

import com.google.common.base.Strings;
import ru.complitex.address.util.AddressUtil;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.*;
import ru.complitex.osznconnection.file.entity.privilege.Debt;
import ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import ru.complitex.osznconnection.file.entity.subsidy.*;
import ru.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import ru.complitex.osznconnection.file.service.privilege.DebtBean;
import ru.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import ru.complitex.osznconnection.file.service.subsidy.*;
import ru.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import ru.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.complitex.address.util.AddressUtil.replaceApartmentSymbol;
import static ru.complitex.address.util.AddressUtil.replaceBuildingNumberSymbol;
import static ru.complitex.osznconnection.file.entity.RequestStatus.ACCOUNT_NUMBER_RESOLVED;

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

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @EJB
    private DebtBean debtBean;

    public String getLocalAccountNumber(AbstractAccountRequest request, String puAccountNumber) throws MoreOneAccountException {
        return getLocalAccountNumber(request, puAccountNumber, false);
    }

    public String getLocalAccountNumber(AbstractAccountRequest request, String puAccountNumber, boolean useAddressNames)
            throws MoreOneAccountException {
        return getLocalAccountNumber(request, puAccountNumber, useAddressNames,
                organizationStrategy.getBillingId(request.getUserOrganizationId()));
    }

    public String getLocalAccountNumber(AbstractAccountRequest request, String puAccountNumber, boolean useAddressNames,
                                        Long billingId) throws MoreOneAccountException {
        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(
                new PersonAccount(request, puAccountNumber, billingId, useAddressNames)).setNullable(true));

        Set<String> accountNumberSet = personAccounts.stream().map(PersonAccount::getAccountNumber).collect(Collectors.toSet());

        if (accountNumberSet.isEmpty()){
            return null;
        }else if (accountNumberSet.size() == 1){
            return accountNumberSet.iterator().next();
        }else{
            throw new MoreOneAccountException(accountNumberSet.toString());
        }
    }

    public void save(AbstractAccountRequest request, String puAccountNumber) throws MoreOneAccountException {
        Long billingId = organizationStrategy.getBillingId(request.getUserOrganizationId());

        List<PersonAccount> personAccounts = personAccountBean.getPersonAccounts(FilterWrapper.of(
                new PersonAccount(request, puAccountNumber, billingId, true, false)).setNullable(true));

        Set<String> accountNumberSet = personAccounts.stream().map(PersonAccount::getAccountNumber).collect(Collectors.toSet());

        if (accountNumberSet.isEmpty()){
            personAccountBean.save(new PersonAccount(request, puAccountNumber, billingId, true));
        }else if (accountNumberSet.size() == 1){
            PersonAccount personAccount = personAccounts.get(0);

            if (!personAccount.getAccountNumber().equals(request.getAccountNumber())){
                personAccount.setAccountNumber(request.getAccountNumber());

                personAccountBean.save(personAccount);
            }
        }else{
            throw new MoreOneAccountException(accountNumberSet.toString());
        }
    }

    public void localResolveAccountNumber(AbstractAccountRequest request, String puAccountNumber, Long billingId, boolean useAddressNames){
        try {
            String localAccountNumber = getLocalAccountNumber(request, puAccountNumber, useAddressNames, billingId);

            if (localAccountNumber != null) {
                request.setAccountNumber(localAccountNumber);
                request.setStatus(ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            request.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    public void resolveAccountNumber(AbstractAccountRequest request, String puAccountNumber,
                                     String serviceProviderCode, Long billingId,
                                     boolean updatePuAccount) {
        try {
            //resolve local account
            String accountNumber = getLocalAccountNumber(request, puAccountNumber, false, billingId);

            if (accountNumber != null) {
                request.setAccountNumber(accountNumber);
                request.setStatus(ACCOUNT_NUMBER_RESOLVED);

                return;
            }

            //resolve remote account
            AccountDetail accountDetail = serviceProviderAdapter.acquireAccountDetail(request,
                    request.getLastName(), puAccountNumber,
                    request.getOutgoingDistrict(), serviceProviderCode, request.getOutgoingStreetType(), request.getOutgoingStreet(),
                    request.getOutgoingBuildingNumber(), request.getOutgoingBuildingCorp(),
                    request.getOutgoingApartment(), request.getDate(), updatePuAccount);

            if (request.getStatus() == ACCOUNT_NUMBER_RESOLVED) {
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
                                          String accountNumber){
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
                        request.setStatus(ACCOUNT_NUMBER_RESOLVED);

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
        payment.setStatus(ACCOUNT_NUMBER_RESOLVED);

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
        actualPayment.setStatus(ACCOUNT_NUMBER_RESOLVED);
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


    //todo move to service
    public void updateAccountNumber(Subsidy subsidy, String accountNumber) {
        subsidy.setAccountNumber(accountNumber);
        subsidy.setStatus(ACCOUNT_NUMBER_RESOLVED);
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
        try {
            dwellingCharacteristics.setAccountNumber(accountNumber);
            serviceProviderAdapter.checkFacilityPerson(dwellingCharacteristics, accountNumber, dwellingCharacteristics.getDate(),
                    dwellingCharacteristics.getInn(), dwellingCharacteristics.getPassport());

            dwellingCharacteristicsBean.updateAccountNumber(dwellingCharacteristics);
            save(dwellingCharacteristics, dwellingCharacteristics.getInn());

            Long dwellingCharacteristicsFileId = dwellingCharacteristics.getRequestFileId();
            RequestFile dwellingCharacteristicsFile = requestFileBean.getRequestFile(dwellingCharacteristicsFileId);

            if (dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(dwellingCharacteristicsFileId)) {
                dwellingCharacteristicsFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(dwellingCharacteristicsFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void updateAccountNumber(FacilityServiceType facilityServiceType, String accountNumber) {
        try {
            facilityServiceType.setAccountNumber(accountNumber);
            serviceProviderAdapter.checkFacilityPerson(facilityServiceType, accountNumber, facilityServiceType.getDate(),
                    facilityServiceType.getInn(), facilityServiceType.getPassport());

            facilityServiceTypeBean.updateAccountNumber(facilityServiceType);
            save(facilityServiceType, facilityServiceType.getInn());

            Long facilityServiceTypeFileId = facilityServiceType.getRequestFileId();
            RequestFile facilityServiceTypeFile = requestFileBean.getRequestFile(facilityServiceTypeFileId);

            if (facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceTypeFileId)) {
                facilityServiceTypeFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(facilityServiceTypeFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccountNumber(PrivilegeProlongation privilegeProlongation, String accountNumber) {
        try {
            privilegeProlongation.setAccountNumber(accountNumber);
            serviceProviderAdapter.checkFacilityPerson(privilegeProlongation, accountNumber, privilegeProlongation.getDate(),
                    privilegeProlongation.getInn(), privilegeProlongation.getPassport());

            privilegeProlongationBean.updatePrivilegeProlongationAccountNumber(privilegeProlongation);
            save(privilegeProlongation, !Strings.isNullOrEmpty(privilegeProlongation.getPuAccountNumber())
                    ? privilegeProlongation.getPuAccountNumber() : privilegeProlongation.getInn());

            Long requestFileId = privilegeProlongation.getRequestFileId();
            RequestFile requestFile = requestFileBean.getRequestFile(requestFileId);

            if (privilegeProlongationBean.isPrivilegeProlongationBound(requestFileId)) {
                requestFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(requestFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccountNumber(Debt debt, String accountNumber) {
        try {
            debt.setAccountNumber(accountNumber);
            serviceProviderAdapter.checkFacilityPerson(debt, accountNumber, debt.getDate(),
                    debt.getInn(), debt.getPassport());

            debtBean.updateDebtAccountNumber(debt);
            save(debt, !Strings.isNullOrEmpty(debt.getPuAccountNumber())
                    ? debt.getPuAccountNumber() : debt.getInn());

            Long requestFileId = debt.getRequestFileId();
            RequestFile requestFile = requestFileBean.getRequestFile(requestFileId);

            if (debtBean.isDebtBound(requestFileId)) {
                requestFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(requestFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
