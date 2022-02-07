package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.entity.PersonalName;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.util.FacilityNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {

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

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<FacilityServiceType>() {

                @Override
                public Enum[] getFieldNames() {
                    return FacilityServiceTypeDBF.values();
                }

                @Override
                public FacilityServiceType newObject() {
                    return new FacilityServiceType(defaultCity, facilityServiceTypeDate);
                }

                @Override
                public void save(List<FacilityServiceType> batch) {
                    facilityServiceTypeBean.insert(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_PRIVILEGE_GROUP));
                }

                @Override
                public void postProcess(int rowNumber, FacilityServiceType request) {
                    parseFio(request);
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
