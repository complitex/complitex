package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.PersonalName;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongationDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import org.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.util.FacilityNameParser;

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
public class PrivilegeProlongationLoadTaskBean extends AbstractTaskBean<RequestFile> {
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

                    batch.forEach(r -> onRequest(r));
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

