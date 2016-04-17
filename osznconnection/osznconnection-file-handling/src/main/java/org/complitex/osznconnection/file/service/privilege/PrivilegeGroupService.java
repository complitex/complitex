package org.complitex.osznconnection.file.service.privilege;

import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;

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
}
