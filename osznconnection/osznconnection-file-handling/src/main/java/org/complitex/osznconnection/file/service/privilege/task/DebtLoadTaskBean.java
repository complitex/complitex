package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.PersonalName;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.util.FacilityNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:32
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DebtLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private DebtBean debtBean;

    @EJB
    private ConfigBean configBean;
    
    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<Debt>() {

                @Override
                public Enum[] getFieldNames() {
                    return DebtDBF.values();
                }

                @Override
                public Debt newObject() {
                    Debt debt =  new Debt();

                    debt.setCity(configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true));
                    debt.setDate(requestFile.getBeginDate());

                    return debt;
                }

                @Override
                public void save(List<Debt> batch) {
                    debtBean.save(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_DEBT));
                }

                @Override
                public void postProcess(int rowNumber, Debt request) {
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

    private void parseFio(Debt debt) {
        String fio = debt.getStringField(DebtDBF.FIOPIL);

        PersonalName personalName = FacilityNameParser.parse(fio);

        debt.setFirstName(personalName.getFirstName());
        debt.setMiddleName(personalName.getMiddleName());
        debt.setLastName(personalName.getLastName());
    }
}
