package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
public class PrivilegeGroupSaveTaskBean extends AbstractTaskBean<PrivilegeFileGroup>{
    @EJB
    private DwellingCharacteristicsSaveTaskBean dwellingCharacteristicsSaveTaskBean;

    @EJB
    private FacilityServiceTypeSaveTaskBean facilityServiceTypeSaveTaskBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        dwellingCharacteristicsSaveTaskBean.execute(group.getDwellingCharacteristicsRequestFile(), commandParameters);
        facilityServiceTypeSaveTaskBean.execute(group.getFacilityServiceTypeRequestFile(), commandParameters);

        return true;
    }
}
