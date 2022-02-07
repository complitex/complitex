package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileGroupType;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
public class PrivilegeGroupLoadTaskBean extends AbstractRequestTaskBean<PrivilegeFileGroup> {
    @EJB
    private DwellingCharacteristicsLoadTaskBean dwellingCharacteristicsLoadTaskBean;

    @EJB
    private FacilityServiceTypeLoadTaskBean facilityServiceTypeLoadTaskBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        group.setStatus(RequestFileStatus.LOADING);

        //request file group id
        RequestFileGroup requestFileGroup = new RequestFileGroup();
        requestFileGroup.setGroupType(RequestFileGroupType.PRIVILEGE_GROUP);

        requestFileGroupBean.save(requestFileGroup);

        RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();

        boolean skip = false;

        if (dwellingCharacteristicsRequestFile != null) {
            dwellingCharacteristicsRequestFile.setGroupId(requestFileGroup.getId());

            skip = dwellingCharacteristicsLoadTaskBean.execute(dwellingCharacteristicsRequestFile, commandParameters);
        }

        RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

        if (facilityServiceTypeRequestFile != null) {
            facilityServiceTypeRequestFile.setGroupId(requestFileGroup.getId());

            skip |= facilityServiceTypeLoadTaskBean.execute(facilityServiceTypeRequestFile, commandParameters);
        }

        return skip;
    }
}
