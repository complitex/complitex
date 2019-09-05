package org.complitex.osznconnection.file.service.privilege.task;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.MoreOneAccountException;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;

@Stateless(name = "DwellingCharacteristicsBindTaskBean")
@TransactionManagement(TransactionManagementType.BEAN)
public class DwellingCharacteristicsBindTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(DwellingCharacteristicsBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestWarningBean requestWarningBean;


    private boolean resolveAddress(DwellingCharacteristics dwellingCharacteristics, Long billingId) {
        addressService.resolveAddress(dwellingCharacteristics, billingId);

        return dwellingCharacteristics.getStatus().isAddressResolved();
    }

    private void resolveLocalAccount(DwellingCharacteristics dwellingCharacteristics) {
        try {
            String accountNumber = personAccountService.getLocalAccountNumber(dwellingCharacteristics,
                    dwellingCharacteristics.getInn());

            if (!Strings.isEmpty(accountNumber)) {
                dwellingCharacteristics.setAccountNumber(accountNumber);
                dwellingCharacteristics.setStatus(RequestStatus.ACCOUNT_NUMBER_RESOLVED);
            }
        } catch (MoreOneAccountException e) {
            dwellingCharacteristics.setStatus(RequestStatus.MORE_ONE_ACCOUNTS_LOCALLY);
        }
    }

    private boolean resolveRemoteAccountNumber(String serviceProviderCode, DwellingCharacteristics dwellingCharacteristics) throws MoreOneAccountException {
        serviceProviderAdapter.acquireFacilityPersonAccount(dwellingCharacteristics,
                dwellingCharacteristics.getOutgoingDistrict(), serviceProviderCode, dwellingCharacteristics.getOutgoingStreetType(),
                dwellingCharacteristics.getOutgoingStreet(),
                dwellingCharacteristics.getOutgoingBuildingNumber(), dwellingCharacteristics.getOutgoingBuildingCorp(),
                dwellingCharacteristics.getOutgoingApartment(), dwellingCharacteristics.getDate(),
                dwellingCharacteristics.getInn(),
                dwellingCharacteristics.getPassport());

        if (dwellingCharacteristics.getStatus() == RequestStatus.ACCOUNT_NUMBER_RESOLVED) {
            personAccountService.save(dwellingCharacteristics, dwellingCharacteristics.getInn());
        }

        return dwellingCharacteristics.getStatus() == ACCOUNT_NUMBER_RESOLVED;
    }

    @SuppressWarnings("Duplicates") //todo extract abstract privilege bind
    public void bind(String serviceProviderCode, Long billingId, DwellingCharacteristics dwellingCharacteristics)
            throws MoreOneAccountException {
        dwellingCharacteristics.setAccountNumber(null);

        //resolve local account number
        personAccountService.localResolveAccountNumber(dwellingCharacteristics, dwellingCharacteristics.getInn(),
                billingId, true);

//        boolean checkLodgerPerson = true;

        //noinspection Duplicates
        if (dwellingCharacteristics.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            //resolve address
            resolveAddress(dwellingCharacteristics, billingId);

            if (dwellingCharacteristics.getStatus().isAddressResolved()){
                //resolve local account.
                resolveLocalAccount(dwellingCharacteristics);

                if (dwellingCharacteristics.getStatus().isNot(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)) {
                    resolveRemoteAccountNumber(serviceProviderCode, dwellingCharacteristics);
//                    checkLodgerPerson = false;

                    if (dwellingCharacteristics.getAccountNumber() == null &&
                            BENEFIT_OWNER_NOT_ASSOCIATED.equals(dwellingCharacteristics.getStatus())){
                        dwellingCharacteristics.setStatus(ACCOUNT_NUMBER_NOT_FOUND);
                    }
                }
            }
        }

//        if (checkLodgerPerson && dwellingCharacteristics.getAccountNumber() != null){
//            serviceProviderAdapter.checkLodgerPerson(dwellingCharacteristics, dwellingCharacteristics.getAccountNumber(),
//                    dwellingCharacteristics.getDate(), dwellingCharacteristics.getInn(), dwellingCharacteristics.getPassport());
//        }

        // обновляем dwelling characteristics запись
        dwellingCharacteristicsBean.update(dwellingCharacteristics);
    }

    private void bindDwellingCharacteristicsFile(RequestFile requestFile)
            throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла dwelling characteristics и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedDwellingCharacteristicsIds = dwellingCharacteristicsBean.findIdsForBinding(requestFile.getId());
        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedDwellingCharacteristicsIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedDwellingCharacteristicsIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedDwellingCharacteristicsIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<DwellingCharacteristics> dwellingCharacteristics =
                    dwellingCharacteristicsBean.findForOperation(requestFile.getId(), batch);
            for (DwellingCharacteristics dwellingCharacteristic : dwellingCharacteristics) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать dwelling characteristics запись
                try {
                    bind(serviceProviderCode, billingId, dwellingCharacteristic);
                    onRequest(dwellingCharacteristic, ProcessType.BIND_PRIVILEGE_GROUP);
                } catch (MoreOneAccountException e) {
                    throw new BindException(e, true, requestFile);
                }
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            // ищем в параметрах комманды опцию "Переписывать номер л/с ПУ номером л/с МН"
//        final Boolean updatePuAccount = commandParameters.containsKey(GlobalOptions.UPDATE_PU_ACCOUNT)
//                ? (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT) : false;

            //проверяем что не обрабатывается в данный момент
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
                throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BINDING);
            requestFileBean.save(requestFile);

            dwellingCharacteristicsBean.clearBeforeBinding(requestFile.getId(), null);

            //clear warning
            requestWarningBean.delete(requestFile.getId(), DWELLING_CHARACTERISTICS);

            //связывание файла dwelling characteristics
            try {
                bindDwellingCharacteristicsFile(requestFile);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в dwelling characteristics файле связались
            if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(requestFile.getId())) {
                throw new BindException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BOUND);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.BIND_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }
}
