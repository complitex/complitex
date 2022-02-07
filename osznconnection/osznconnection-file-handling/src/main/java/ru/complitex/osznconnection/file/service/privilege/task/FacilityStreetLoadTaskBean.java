package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreetDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;

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
public class FacilityStreetLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
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
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            //update date range
            requestFileBean.updateDateRange(requestFile);

            loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<FacilityStreet>() {

                @Override
                public Enum[] getFieldNames() {
                    return FacilityStreetDBF.values();
                }

                @Override
                public FacilityStreet newObject() {
                    return new FacilityStreet();
                }

                @Override
                public void save(List<FacilityStreet> requests) throws ExecuteException {
                    facilityReferenceBookBean.insert(requests);

                    requests.forEach(r -> onRequest(r, ProcessType.LOAD_FACILITY_STREET_REFERENCE));
                }
            });
            requestFile.setStatus(RequestFileStatus.LOADED);

            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
            requestFileBean.delete(requestFile);

            throw e;
        }
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
