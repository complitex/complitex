package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import org.complitex.osznconnection.file.service.subsidy.SubsidyService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidySaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFile> {

    @EJB
    private SubsidyService subsidyService;

    @Override
    protected List<Subsidy> getAbstractRequests(RequestFile requestFile) {
       return subsidyService.getSubsidyWithSplitList(requestFile.getId());
    }

    @Override
    protected String getPuAccountFieldName() {
        return SubsidyDBF.RASH.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return SubsidySaveTaskBean.class;
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_SUBSIDY_DIR;
    }
}
