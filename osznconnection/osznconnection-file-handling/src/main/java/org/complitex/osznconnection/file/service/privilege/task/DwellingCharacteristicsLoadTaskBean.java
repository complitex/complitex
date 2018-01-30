package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.Log;
import org.complitex.common.entity.PersonalName;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.ConfigBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
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
public class DwellingCharacteristicsLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {

    @EJB
    private RequestFileBean requestFileBean;
    @EJB
    private LoadRequestFileBean loadRequestFileBean;
    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;
    @EJB
    private ConfigBean configBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            final String defaultCity = configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true);
            final Date dwellingCharacteristicsDate = requestFile.getBeginDate();

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<DwellingCharacteristics>() {

                @Override
                public Enum[] getFieldNames() {
                    return DwellingCharacteristicsDBF.values();
                }

                @Override
                public DwellingCharacteristics newObject() {
                    return new DwellingCharacteristics(defaultCity, dwellingCharacteristicsDate);
                }

                @Override
                public void save(List<DwellingCharacteristics> batch) {
                    dwellingCharacteristicsBean.insert(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_PRIVILEGE_GROUP));
                }

                @Override
                public void postProcess(int rowNumber, DwellingCharacteristics request) {
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

    private void parseFio(DwellingCharacteristics dwellingCharacteristics) {
        String fio = dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.FIO);
        PersonalName personalName = FacilityNameParser.parse(fio);
        dwellingCharacteristics.setFirstName(personalName.getFirstName());
        dwellingCharacteristics.setMiddleName(personalName.getMiddleName());
        dwellingCharacteristics.setLastName(personalName.getLastName());
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
