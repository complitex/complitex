package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.Log;
import org.complitex.common.entity.PersonalName;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.util.FacilityNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeLoadTaskBean extends AbstractTaskBean<RequestFile> {

    @EJB
    private RequestFileBean requestFileBean;
    @EJB
    private LoadRequestFileBean loadRequestFileBean;
    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;
    @EJB
    private ConfigBean configBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            final String defaultCity = configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true);
            final Date facilityServiceTypeDate = requestFile.getBeginDate();

            boolean noSkip = loadRequestFileBean.load(requestFile, new LoadRequestFileBean.AbstractLoadRequestFile() {

                @Override
                public Enum[] getFieldNames() {
                    return FacilityServiceTypeDBF.values();
                }

                @Override
                public AbstractRequest newObject() {
                    return new FacilityServiceType(defaultCity, facilityServiceTypeDate);
                }

                @Override
                public void save(List<AbstractRequest> batch) {
                    facilityServiceTypeBean.insert(batch);

                    batch.forEach(r -> onRequest(r));
                }

                @Override
                public void postProcess(int rowNumber, AbstractRequest request) {
                    final FacilityServiceType facilityServiceType = (FacilityServiceType) request;
                    parseFio(facilityServiceType);
                }
            });

            if (!noSkip) {
                requestFile.setStatus(RequestFileStatus.SKIPPED);

                return false; //skip - file already loaded
            }

            requestFile.setStatus(RequestFileStatus.LOADED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFileBean.delete(requestFile);

            throw e;
        }
    }

    private void parseFio(FacilityServiceType facilityServiceType) {
        String fio = facilityServiceType.getStringField(FacilityServiceTypeDBF.FIO);
        PersonalName personalName = FacilityNameParser.parse(fio);
        facilityServiceType.setFirstName(personalName.getFirstName());
        facilityServiceType.setMiddleName(personalName.getMiddleName());
        facilityServiceType.setLastName(personalName.getLastName());
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
