package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.exception.CanceledByUserException;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.privilege.PrivilegeGroupService;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;
import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeGroupBindTaskBean extends AbstractTaskBean<PrivilegeFileGroup>{
    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private DwellingCharacteristicsBindTaskBean dwellingCharacteristicsBindTaskBean;

    @EJB
    private FacilityServiceTypeBindTaskBean facilityServiceTypeBindTaskBean;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private RequestWarningBean requestWarningBean;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    @Override
    public boolean execute(PrivilegeFileGroup group, Map commandParameters) throws ExecuteException {
        RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();
        RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

        if (requestFileBean.getRequestFileStatus(dwellingCharacteristicsRequestFile.getId()).isProcessing()) {
            throw new BindException(new AlreadyProcessingException(dwellingCharacteristicsRequestFile.getFullName()), true, dwellingCharacteristicsRequestFile);
        }
        if (requestFileBean.getRequestFileStatus(facilityServiceTypeRequestFile.getId()).isProcessing()) {
            throw new BindException(new AlreadyProcessingException(facilityServiceTypeRequestFile.getFullName()), true, facilityServiceTypeRequestFile);
        }

        //binding
        dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.BINDING);
        requestFileBean.save(dwellingCharacteristicsRequestFile);

        facilityServiceTypeRequestFile.setStatus(RequestFileStatus.BINDING);
        requestFileBean.save(facilityServiceTypeRequestFile);


        //clear
        dwellingCharacteristicsBean.clearBeforeBinding(dwellingCharacteristicsRequestFile.getId(), null);
        facilityServiceTypeBean.clearBeforeBinding(facilityServiceTypeRequestFile.getId(), null);

        //clear warning
        requestWarningBean.delete(dwellingCharacteristicsRequestFile.getId(), DWELLING_CHARACTERISTICS);
        requestWarningBean.delete(facilityServiceTypeRequestFile.getId(), FACILITY_SERVICE_TYPE);

        String serviceProviderCode = organizationStrategy.getServiceProviderCode(dwellingCharacteristicsRequestFile.getEdrpou(),
                dwellingCharacteristicsRequestFile.getOrganizationId(), dwellingCharacteristicsRequestFile.getUserOrganizationId());

        List<PrivilegeGroup> privilegeGroups = privilegeGroupService.getPrivilegeGroups(group.getId());

        try {
            for (PrivilegeGroup p : privilegeGroups){
                if (group.isCanceled()) {
                    throw new CanceledByUserException();
                }

                if (p.getDwellingCharacteristics() != null){
                    dwellingCharacteristicsBindTaskBean.bind(serviceProviderCode, p.getDwellingCharacteristics());

                    if (p.getFacilityServiceType() != null){
                        DwellingCharacteristics d = p.getDwellingCharacteristics();
                        FacilityServiceType f = p.getFacilityServiceType();

                        f.setCityId(d.getCityId());
                        f.setStreetTypeId(d.getStreetTypeId());
                        f.setStreetId(d.getStreetId());
                        f.setBuildingId(d.getBuildingId());

                        f.setOutgoingCity(d.getOutgoingCity());
                        f.setOutgoingStreetType(d.getOutgoingStreetType());
                        f.setOutgoingStreet(d.getOutgoingStreet());
                        f.setOutgoingBuildingNumber(d.getOutgoingBuildingNumber());
                        f.setOutgoingBuildingCorp(d.getOutgoingBuildingCorp());
                        f.setOutgoingApartment(d.getOutgoingApartment());

                        f.setAccountNumber(d.getAccountNumber());
                        f.setStatus(d.getStatus());

                        facilityServiceTypeBean.update(f);
                    }
                }else if (p.getFacilityServiceType() != null){
                    facilityServiceTypeBindTaskBean.bind(serviceProviderCode, p.getFacilityServiceType());

                }
            }
        } catch (Exception e) {
            throw new BindException(e, false, dwellingCharacteristicsRequestFile);
        }

        //is bound
        if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(dwellingCharacteristicsRequestFile.getId())) {
            throw new BindException(true, dwellingCharacteristicsRequestFile);
        }

        if (!facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceTypeRequestFile.getId())) {
            throw new BindException(true, facilityServiceTypeRequestFile);
        }

        //bound
        dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.BOUND);
        requestFileBean.save(dwellingCharacteristicsRequestFile);

        facilityServiceTypeRequestFile.setStatus(RequestFileStatus.BOUND);
        requestFileBean.save(facilityServiceTypeRequestFile);

        return true;
    }

    @Override
    public void onError(PrivilegeFileGroup group) {
        group.getDwellingCharacteristicsRequestFile().setStatus(RequestFileStatus.BIND_ERROR);
        requestFileBean.save(group.getDwellingCharacteristicsRequestFile());

        group.getFacilityServiceTypeRequestFile().setStatus(RequestFileStatus.BIND_ERROR);
        requestFileBean.save(group.getFacilityServiceTypeRequestFile());
    }
}
