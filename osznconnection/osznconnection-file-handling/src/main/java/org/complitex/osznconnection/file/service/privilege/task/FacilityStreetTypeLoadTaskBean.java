package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.Log;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetType;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetTypeDBF;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessType;

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
public class FacilityStreetTypeLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {

    @EJB
    private RequestFileBean requestFileBean;
    @EJB
    private LoadRequestFileBean loadRequestFileBean;
    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            //update date range
            requestFileBean.updateDateRange(requestFile);

            loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<FacilityStreetType>() {

                @Override
                public Enum[] getFieldNames() {
                    return FacilityStreetTypeDBF.values();
                }

                @Override
                public FacilityStreetType newObject() {
                    return new FacilityStreetType();
                }

                @Override
                public void save(List<FacilityStreetType> requests) {
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
