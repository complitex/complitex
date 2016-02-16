package org.complitex.osznconnection.file.service.process;

import com.google.common.collect.Lists;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.entity.Log.EVENT;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.*;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.web.pages.util.GlobalOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyBindTaskBean extends AbstractTaskBean {

    private final Logger log = LoggerFactory.getLogger(SubsidyBindTaskBean.class);
    @Resource
    private UserTransaction userTransaction;

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
    private SubsidyService subsidyService;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    public void bind(Subsidy subsidy, boolean updatePuAccount) throws DBException {
        //resolve address
        String city = subsidy.getCity();

        if (city == null || city.isEmpty()){
            subsidy.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY));
        }

        addressService.resolveAddress(subsidy);

        subsidy.setCity(city);

        //resolve account number
        if (subsidy.getStatus().isAddressResolved()){
            personAccountService.resolveAccountNumber(subsidy, subsidy.getStringField(SubsidyDBF.RASH),
                    subsidyService.getServiceProviderCode(subsidy.getRequestFileId()),
                    updatePuAccount);
        }

        // обновляем subsidy запись
        subsidyBean.update(subsidy);
    }

    private void bindSubsidyFile(RequestFile subsidyFile, Boolean updatePuAccount)
            throws BindException, DBException, CanceledByUserException {
        //извлечь из базы все id подлежащие связыванию для файла subsidy и доставать записи порциями по BATCH_SIZE штук.
        List<Long> notResolvedSubsidyIds = subsidyBean.findIdsForBinding(subsidyFile.getId());

        List<Long> batch = Lists.newArrayList();

        int batchSize = configBean.getInteger(FileHandlingConfig.BIND_BATCH_SIZE, true);

        while (notResolvedSubsidyIds.size() > 0) {
            batch.clear();
            int toRemoveCount = Math.min(batchSize, notResolvedSubsidyIds.size());
            for (int i = 0; i < toRemoveCount; i++) {
                batch.add(notResolvedSubsidyIds.remove(0));
            }

            //достать из базы очередную порцию записей
            List<Subsidy> subsidies = subsidyBean.findForOperation(subsidyFile.getId(), batch);
            for (Subsidy subsidy : subsidies) {
                if (subsidyFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                //связать subsidy запись
                try {
                    userTransaction.begin();
                    bind(subsidy, updatePuAccount);
                    userTransaction.commit();
                } catch (Exception e) {
                    log.error("The subsidy item ( id = " + subsidy.getId() + ") was bound with error: ", e);

                    try {
                        userTransaction.rollback();
                    } catch (SystemException e1) {
                        log.error("Couldn't rollback transaction for binding subsidy item.", e1);
                    }

                    throw new BindException(e, false, subsidyFile);
                }
            }
        }
    }

    @Override
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        // ищем в параметрах комманды опцию "Переписывать номер л/с ПУ номером л/с МН"
        final Boolean updatePuAccount = commandParameters.containsKey(GlobalOptions.UPDATE_PU_ACCOUNT)
                ? (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT) : false;

        RequestFile requestFile = (RequestFile) executorObject;

        requestFile.setStatus(requestFileBean.getRequestFileStatus(requestFile)); //обновляем статус из базы данных

        if (requestFile.isProcessing()) { //проверяем что не обрабатывается в данный момент
            throw new BindException(new AlreadyProcessingException(requestFile), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.BINDING);
        requestFileBean.save(requestFile);

        Set<Long> serviceIds = new HashSet<>(); //todo get services

        subsidyBean.clearBeforeBinding(requestFile.getId(), serviceIds);

        //связывание файла subsidy
        try {
            bindSubsidyFile(requestFile, updatePuAccount);
        } catch (DBException e) {
            throw new RuntimeException(e);
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
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFile requestFile = (RequestFile) executorObject;

        requestFile.setStatus(RequestFileStatus.BIND_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return SubsidyBindTaskBean.class;
    }

    @Override
    public EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
