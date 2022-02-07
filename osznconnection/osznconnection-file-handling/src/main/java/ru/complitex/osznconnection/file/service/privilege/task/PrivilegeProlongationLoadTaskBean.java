package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.entity.PersonalName;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeProlongationDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.util.FacilityNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 30.06.16.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeProlongationLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @EJB
    private ConfigBean configBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<PrivilegeProlongation>() {

                @Override
                public Enum[] getFieldNames() {
                    return PrivilegeProlongationDBF.values();
                }

                @Override
                public PrivilegeProlongation newObject() {
                    PrivilegeProlongation privilegeProlongation =  new PrivilegeProlongation();

                    privilegeProlongation.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true));
                    privilegeProlongation.setDate(requestFile.getBeginDate());

                    return privilegeProlongation;
                }

                @Override
                public void save(List<PrivilegeProlongation> batch) {
                    privilegeProlongationBean.insertPrivilegeProlongation(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_PRIVILEGE_PROLONGATION));
                }

                @Override
                public void postProcess(int rowNumber, PrivilegeProlongation request) {
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

    private void parseFio(PrivilegeProlongation privilegeProlongation) {
        String fio = privilegeProlongation.getStringField(PrivilegeProlongationDBF.FIOPIL);

        PersonalName personalName = FacilityNameParser.parse(fio);
        privilegeProlongation.setFirstName(personalName.getFirstName());
        privilegeProlongation.setMiddleName(personalName.getMiddleName());
        privilegeProlongation.setLastName(personalName.getLastName());
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }
}

