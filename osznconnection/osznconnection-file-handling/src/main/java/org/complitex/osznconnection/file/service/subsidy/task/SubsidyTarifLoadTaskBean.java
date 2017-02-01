package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.Log;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyTarif;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyTarifDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyTarifBean;

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
public class SubsidyTarifLoadTaskBean extends AbstractTaskBean<RequestFile> {

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

                    batch.forEach(r -> onRequest(r));
                }
            });

            requestFile.setStatus(RequestFileStatus.LOADED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
            requestFileBean.save(requestFile);

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
