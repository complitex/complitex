package org.complitex.osznconnection.file.service.process;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service.RequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityStreetLoadTaskBean implements ITaskBean {
    public static final String LOCALE_TASK_PARAMETER_KEY = "locale";

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @Override
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        RequestFile requestFile = (RequestFile) executorObject;
        requestFile.setStatus(RequestFileStatus.LOADING);

        //update date range
        requestFileBean.updateDateRange(requestFile);

        loadRequestFileBean.load(requestFile, new LoadRequestFileBean.AbstractLoadRequestFile() {

            @Override
            public Enum[] getFieldNames() {
                return FacilityStreetDBF.values();
            }

            @Override
            public AbstractRequest newObject() {
                return new FacilityStreet();
            }

            @Override
            public void save(List<AbstractRequest> requests) throws ExecuteException {
                facilityReferenceBookBean.insert(requests);
            }
        });
        requestFile.setStatus(RequestFileStatus.LOADED);

        requestFileBean.save(requestFile);

        return true;
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFile requestFile = (RequestFile) executorObject;
        requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return FacilityStreetLoadTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
