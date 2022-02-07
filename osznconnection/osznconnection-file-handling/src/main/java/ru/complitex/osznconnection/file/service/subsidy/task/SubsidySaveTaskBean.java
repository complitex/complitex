package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.service.executor.ITaskBean;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.subsidy.Subsidy;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import ru.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyService;

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
