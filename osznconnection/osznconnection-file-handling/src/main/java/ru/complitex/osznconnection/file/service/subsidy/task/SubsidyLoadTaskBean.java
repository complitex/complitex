package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.entity.PersonalName;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.subsidy.Subsidy;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyService;
import ru.complitex.osznconnection.file.service.util.SubsidyNameParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyService subsidyService;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<Subsidy>() {

                @Override
                public Enum[] getFieldNames() {
                    return SubsidyDBF.values();
                }

                @Override
                public Subsidy newObject() {
                    return new Subsidy();
                }

                @Override
                public void save(List<Subsidy> batch) {
                    //check sum
                    for (AbstractRequest request : batch) {
                        if (!subsidyService.validateSum(request)){
                            request.setStatus(RequestStatus.SUBSIDY_NM_PAY_ERROR);
                            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
                        }
                    }

                    subsidyBean.insert(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_SUBSIDY));
                }

                @Override
                public void postProcess(int rowNumber, Subsidy request) {
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
        } catch (Exception e) {
            requestFileBean.delete(requestFile);

            throw e;
        }
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
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
