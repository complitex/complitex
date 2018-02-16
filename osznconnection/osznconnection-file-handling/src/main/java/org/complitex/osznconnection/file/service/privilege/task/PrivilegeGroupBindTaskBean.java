package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import org.complitex.osznconnection.file.service.privilege.PrivilegeGroupService;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PrivilegeGroupBindTaskBean extends AbstractRequestTaskBean<PrivilegeFileGroup> {
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
        try {
            RequestFile dwellingCharacteristicsRequestFile = group.getDwellingCharacteristicsRequestFile();

            if (dwellingCharacteristicsRequestFile != null){
                binding(dwellingCharacteristicsRequestFile);
            }

            RequestFile facilityServiceTypeRequestFile = group.getFacilityServiceTypeRequestFile();

            if (facilityServiceTypeRequestFile != null){
                binding(facilityServiceTypeRequestFile);
            }

            List<PrivilegeGroup> privilegeGroups = privilegeGroupService.getPrivilegeGroups(group.getId());

            String serviceProviderCode = dwellingCharacteristicsRequestFile != null
                    ? organizationStrategy.getServiceProviderCode(dwellingCharacteristicsRequestFile.getEdrpou(),
                    dwellingCharacteristicsRequestFile.getOrganizationId(), dwellingCharacteristicsRequestFile.getUserOrganizationId())
                    : facilityServiceTypeRequestFile != null
                    ? organizationStrategy.getServiceProviderCode(facilityServiceTypeRequestFile.getEdrpou(),
                    facilityServiceTypeRequestFile.getOrganizationId(), facilityServiceTypeRequestFile.getUserOrganizationId())
                    : null;

            Long billingId = organizationStrategy.getBillingId(group.getUserOrganizationId());

            try {
                for (PrivilegeGroup p : privilegeGroups){
                    if (group.isCanceled()) {
                        throw new CanceledByUserException();
                    }

                    if (p.getDwellingCharacteristics() != null){
                        dwellingCharacteristicsBindTaskBean.bind(serviceProviderCode, billingId, p.getDwellingCharacteristics());

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
                        facilityServiceTypeBindTaskBean.bind(serviceProviderCode, billingId, p.getFacilityServiceType());

                    }

                    onRequest(p, ProcessType.LOAD_PRIVILEGE_GROUP);
                }
            } catch (Exception e) {
                throw new BindException(e, false, dwellingCharacteristicsRequestFile != null
                        ? dwellingCharacteristicsRequestFile : facilityServiceTypeRequestFile);
            }

            if (dwellingCharacteristicsRequestFile != null){
                if (!dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(dwellingCharacteristicsRequestFile.getId())) {
                    throw new BindException(true, dwellingCharacteristicsRequestFile);
                }

                dwellingCharacteristicsRequestFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(dwellingCharacteristicsRequestFile);
            }

            if (facilityServiceTypeRequestFile != null){
                if (!facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceTypeRequestFile.getId())) {
                    throw new BindException(true, facilityServiceTypeRequestFile);
                }

                facilityServiceTypeRequestFile.setStatus(RequestFileStatus.BOUND);
                requestFileBean.save(facilityServiceTypeRequestFile);
            }

            return false;
        } catch (Exception e) {
            if (group.getDwellingCharacteristicsRequestFile() != null) {
                group.getDwellingCharacteristicsRequestFile().setStatus(RequestFileStatus.BIND_ERROR);
                requestFileBean.save(group.getDwellingCharacteristicsRequestFile());
            }

            if (group.getFacilityServiceTypeRequestFile() != null) {
                group.getFacilityServiceTypeRequestFile().setStatus(RequestFileStatus.BIND_ERROR);
                requestFileBean.save(group.getFacilityServiceTypeRequestFile());
            }

            throw e;
        }
    }

    private void binding(RequestFile requestFile) throws BindException {
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
            throw new BindException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.BINDING);
        requestFileBean.save(requestFile);

        dwellingCharacteristicsBean.clearBeforeBinding(requestFile.getId(), null);

        requestWarningBean.delete(requestFile.getId(), requestFile.getType());
    }
}
