package org.complitex.osznconnection.file.service.subsidy.task;

import com.google.common.collect.Lists;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.common.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.web.pages.util.GlobalOptions;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.complitex.osznconnection.file.entity.RequestStatus.*;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyBindTaskBean extends AbstractTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(SubsidyBindTaskBean.class);

    @EJB
    protected ConfigBean configBean;

    @EJB(name = "OsznAddressService")
    private AddressService addressService;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    public void bind(String serviceProviderCode, Subsidy subsidy, boolean updatePuAccount) {
        String puAccountNumber = subsidy.getStringField(SubsidyDBF.RASH);

        //resolve local account number
        personAccountService.localResolveAccountNumber(subsidy, puAccountNumber, true);

        if (subsidy.getStatus().isNotIn(ACCOUNT_NUMBER_RESOLVED, MORE_ONE_ACCOUNTS_LOCALLY)){
            //resolve address
            String city = subsidy.getCity();

            if (city == null || city.isEmpty()){
                subsidy.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY));
            }

            addressService.resolveAddress(subsidy);

            subsidy.setCity(city);

            //resolve account number
            if (subsidy.getStatus().isAddressResolved()){
                personAccountService.resolveAccountNumber(subsidy, puAccountNumber, serviceProviderCode, false);

                if (MORE_ONE_ACCOUNTS.equals(subsidy.getStatus())){
                    personAccountService.forceResolveAccountNumber(subsidy, addressService.resolveOutgoingDistrict(
                            subsidy.getOrganizationId(), subsidy.getUserOrganizationId()), serviceProviderCode,
                            puAccountNumber);
                }
            }
        }

        // обновляем subsidy запись
        subsidyBean.update(subsidy);
    }

    private void bindSubsidyFile(RequestFile requestFile, Boolean updatePuAccount)
            throws BindException, CanceledByUserException {
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        //извлечь из базы все id подлежащие связыванию для файла subsidy и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedSubsidyIds = subsidyBean.findIdsForBinding(requestFile.getId());

        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedSubsidyIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedSubsidyIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedSubsidyIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<Subsidy> subsidies = subsidyBean.findForOperation(requestFile.getId(), batch);
            for (Subsidy subsidy : subsidies) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать subsidy запись
                bind(serviceProviderCode, subsidy, updatePuAccount);
                onRequest(subsidy);
            }
        }
    }

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            // ищем в параметрах комманды опцию "Переписывать номер л/с ПУ номером л/с МН"
            final Boolean updatePuAccount = commandParameters.containsKey(GlobalOptions.UPDATE_PU_ACCOUNT)
                    ? (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT) : false;

            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.BINDING);
            requestFileBean.save(requestFile);

            Set<Long> serviceIds = new HashSet<>(); //todo get services

            subsidyBean.clearBeforeBinding(requestFile.getId(), serviceIds);

            //связывание файла subsidy
            try {
                bindSubsidyFile(requestFile, updatePuAccount);
            } catch (CanceledByUserException e) {
                throw new BindException(e, true, requestFile);
            }

            //проверить все ли записи в subsidy файле связались
            if (!subsidyBean.isSubsidyFileBound(requestFile.getId())) {
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
