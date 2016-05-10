package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
public class PrivilegeGroupLoadTaskBean extends AbstractTaskBean<PrivilegeFileGroup>{
    @EJB
    private DwellingCharacteristicsLoadTaskBean dwellingCharacteristicsLoadTaskBean;

    @EJB
    private FacilityServiceTypeLoadTaskBean facilityServiceTypeLoadTaskBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        group.setStatus(RequestFileStatus.LOADING);

        RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();

        if (dwellingCharacteristicsRequestFile != null) {
            dwellingCharacteristicsLoadTaskBean.execute(dwellingCharacteristicsRequestFile, commandParameters);
        }

        RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

        if (facilityServiceTypeRequestFile != null) {
            facilityServiceTypeLoadTaskBean.execute(facilityServiceTypeRequestFile, commandParameters);
        }

        return true;
    }
}
