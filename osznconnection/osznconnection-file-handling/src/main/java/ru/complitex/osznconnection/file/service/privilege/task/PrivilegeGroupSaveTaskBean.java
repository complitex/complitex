package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
public class PrivilegeGroupSaveTaskBean extends AbstractRequestTaskBean<PrivilegeFileGroup> {
    @EJB
    private DwellingCharacteristicsSaveTaskBean dwellingCharacteristicsSaveTaskBean;

    @EJB
    private FacilityServiceTypeSaveTaskBean facilityServiceTypeSaveTaskBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        if (group.getDwellingCharacteristicsRequestFile() != null) {
            dwellingCharacteristicsSaveTaskBean.execute(group.getDwellingCharacteristicsRequestFile(), commandParameters);
        }

        if (group.getFacilityServiceTypeRequestFile() != null) {
            facilityServiceTypeSaveTaskBean.execute(group.getFacilityServiceTypeRequestFile(), commandParameters);
        }

        return true;
    }
}
