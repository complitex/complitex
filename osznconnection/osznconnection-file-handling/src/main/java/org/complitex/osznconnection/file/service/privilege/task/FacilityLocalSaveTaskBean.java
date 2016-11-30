package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocal;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocalDBF;
import org.complitex.osznconnection.file.service.privilege.FacilityLocalBean;
import org.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 018 18.11.16.
 */
@Stateless
public class FacilityLocalSaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFile> {

    @EJB
    private FacilityLocalBean facilityLocalBean;

    @Override
    protected List<FacilityLocal> getAbstractRequests(RequestFile requestFile) {
        return facilityLocalBean.getFacilityLocal(requestFile.getId());
    }

    @Override
    protected String getPuAccountFieldName() {
        return FacilityLocalDBF.IDCODE.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return FacilityLocalSaveTaskBean.class;
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_FACILITY_LOCAL_DIR;
    }
}