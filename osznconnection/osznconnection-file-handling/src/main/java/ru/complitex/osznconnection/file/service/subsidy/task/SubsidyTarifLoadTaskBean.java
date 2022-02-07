package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyTarif;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyTarifDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyTarifBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.11.10 13:03
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyTarifLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {

    @EJB
    private RequestFileBean requestFileBean;
    @EJB
    private LoadRequestFileBean loadRequestFileBean;
    @EJB
    private SubsidyTarifBean subsidyTarifBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            //delete previous subsidy tarif files.
            requestFileBean.deleteSubsidyTarifFiles(requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

            requestFile.setStatus(RequestFileStatus.LOADING);

            loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<SubsidyTarif>() {

                @Override
                public Enum[] getFieldNames() {
                    return SubsidyTarifDBF.values();
                }

                @Override
                public SubsidyTarif newObject() {
                    return new SubsidyTarif();
                }

                @Override
                public void save(List<SubsidyTarif> batch) {
                    subsidyTarifBean.save(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_SUBSIDY_TARIF));
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
