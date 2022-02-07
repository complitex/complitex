package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.executor.ITaskBean;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2DBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityForm2Bean;
import ru.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class FacilityForm2SaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFile> {

    @EJB
    private FacilityForm2Bean facilityForm2Bean2;

    @Override
    protected List<FacilityForm2> getAbstractRequests(RequestFile requestFile) {
        return facilityForm2Bean2.getFacilityForm2List(FilterWrapper.of(new FacilityForm2(requestFile.getId())));
    }

    @Override
    protected String getPuAccountFieldName() {
        return FacilityForm2DBF.IDCODE.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return FacilityForm2SaveTaskBean.class;
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_FACILITY_LOCAL_DIR;
    }
}
