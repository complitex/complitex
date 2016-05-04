package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.entity.PersonalName;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyService;
import org.complitex.osznconnection.file.service.util.SubsidyNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyLoadTaskBean implements ITaskBean {

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyService subsidyService;

    @Override
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        final RequestFile requestFile = (RequestFile) executorObject;

        requestFile.setStatus(RequestFileStatus.LOADING);

        boolean noSkip = loadRequestFileBean.load(requestFile, new LoadRequestFileBean.AbstractLoadRequestFile() {

            @Override
            public Enum[] getFieldNames() {
                return SubsidyDBF.values();
            }

            @Override
            public AbstractRequest newObject() {
                return new Subsidy();
            }

            @Override
            public void save(List<AbstractRequest> batch) {
                //check sum
                for (AbstractRequest request : batch) {
                    if (!subsidyService.validateSum(request)){
                        request.setStatus(RequestStatus.SUBSIDY_NM_PAY_ERROR);
                        requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
                    }
                }

                subsidyBean.insert(batch);
            }

            @Override
            public void postProcess(int rowNumber, AbstractRequest request) {
                final Subsidy subsidy = (Subsidy) request;
                parseFio(subsidy);
            }
        });

        if (!noSkip) {
            requestFile.setStatus(RequestFileStatus.SKIPPED);

            return false; //skip - file already loaded
        }

        if (!requestFile.getStatus().equals(RequestFileStatus.LOAD_ERROR)) {
            requestFile.setStatus(RequestFileStatus.LOADED);
        }

        requestFileBean.save(requestFile);

        return true;
    }

    private void parseFio(Subsidy subsidy) {
        final String rash = subsidy.getStringField(SubsidyDBF.RASH);
        final String fio = subsidy.getStringField(SubsidyDBF.FIO);
        PersonalName personName = SubsidyNameParser.parse(rash, fio);
        subsidy.setFirstName(personName.getFirstName());
        subsidy.setMiddleName(personName.getMiddleName());
        subsidy.setLastName(personName.getLastName());
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFile requestFile = (RequestFile) executorObject;
        requestFileBean.delete(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return SubsidyLoadTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}