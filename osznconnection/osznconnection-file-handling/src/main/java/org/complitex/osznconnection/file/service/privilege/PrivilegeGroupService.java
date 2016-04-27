package org.complitex.osznconnection.file.service.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import org.complitex.osznconnection.file.service.RequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * inheaven on 18.04.2016.
 */
@Stateless
public class PrivilegeGroupService {
    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private RequestFileBean requestFileBean;

    public List<PrivilegeGroup> getPrivilegeGroups(Long groupId){
        List<PrivilegeGroup> privilegeGroups = new ArrayList<>();

        List<DwellingCharacteristics> dwellingCharacteristicsList = dwellingCharacteristicsBean.getDwellingCharacteristicsListByGroup(groupId);
        List<FacilityServiceType> facilityServiceTypeList = facilityServiceTypeBean.getFacilityServiceTypeListByGroup(groupId);

        dwellingCharacteristicsList.forEach(d -> privilegeGroups.add(new PrivilegeGroup(d)));

        facilityServiceTypeList.forEach(f -> {
            PrivilegeGroup g = privilegeGroups.parallelStream()
                    .filter(p -> p.getDwellingCharacteristics() != null)
                    .filter(p -> p.getDwellingCharacteristics().getInn().equals(f.getInn()))
                    .filter(p -> p.getDwellingCharacteristics().getPassport().equals(f.getPassport()))
                    .filter(p -> p.getDwellingCharacteristics().getFio().equals(f.getFio()))
                    .filter(p -> p.getDwellingCharacteristics().getCity().equals(f.getCity()))
                    .filter(p -> p.getDwellingCharacteristics().getStreetType().equals(f.getStreetType()))
                    .filter(p -> p.getDwellingCharacteristics().getStreet().equals(f.getStreet()))
                    .filter(p -> p.getDwellingCharacteristics().getBuildingNumber().equals(f.getBuildingNumber()))
                    .filter(p -> p.getDwellingCharacteristics().getBuildingCorp().equals(f.getBuildingCorp()))
                    .findAny()
                    .orElse(null);

            if (g != null){
                g.setFacilityServiceType(f);
            }else{
                privilegeGroups.add(new PrivilegeGroup(f));
            }
        });

        return privilegeGroups;
    }

    public PrivilegeGroup getPrivilegeGroup(AbstractAccountRequest request){
        PrivilegeExample example = new PrivilegeExample();

        example.setGroupId(requestFileBean.getRequestFile(request.getRequestFileId()).getGroupId());
        example.setInn(request.getInn());
        example.setPassport(request.getPassport());
        example.setFio(request.getFio());
        example.setStreetCode(request.getStreetCode());
        example.setBuilding(request.getBuildingNumber());
        example.setCorp(request.getBuildingCorp());
        example.setApartment(request.getApartment());

        PrivilegeGroup privilegeGroup = new PrivilegeGroup();

        List<DwellingCharacteristics> dwellingCharacteristicsList = dwellingCharacteristicsBean.find(example);

        if (dwellingCharacteristicsList.size() == 1){
            privilegeGroup.setDwellingCharacteristics(dwellingCharacteristicsList.get(0));
        }

        List<FacilityServiceType> facilityServiceTypeList = facilityServiceTypeBean.find(example);

        if (facilityServiceTypeList.size() == 1){
            privilegeGroup.setFacilityServiceType(facilityServiceTypeList.get(0));
        }

        return privilegeGroup;
    }
}
