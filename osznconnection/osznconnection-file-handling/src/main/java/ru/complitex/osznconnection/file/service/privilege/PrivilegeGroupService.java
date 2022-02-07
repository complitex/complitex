package ru.complitex.osznconnection.file.service.privilege;

import org.apache.commons.collections4.map.HashedMap;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.example.PrivilegeExample;
import ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import ru.complitex.osznconnection.file.service.RequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<DwellingCharacteristics> dwellingCharacteristicsList = dwellingCharacteristicsBean.getDwellingCharacteristicsListByGroup(groupId);
        List<FacilityServiceType> facilityServiceTypeList = facilityServiceTypeBean.getFacilityServiceTypeListByGroup(groupId);

        Map<String, PrivilegeGroup> privilegeGroupMap = new HashedMap<>(dwellingCharacteristicsList.size());

        for (DwellingCharacteristics dwellingCharacteristics : dwellingCharacteristicsList){
            privilegeGroupMap.put(getPrivilegeKey(dwellingCharacteristics), new PrivilegeGroup(dwellingCharacteristics));
        }

        for (FacilityServiceType facilityServiceType : facilityServiceTypeList){
            String key = getPrivilegeKey(facilityServiceType);

            PrivilegeGroup privilegeGroup = privilegeGroupMap.get(key);

            if (privilegeGroup != null){
                privilegeGroup.setFacilityServiceType(facilityServiceType); //todo duplicate status
            }else{
                privilegeGroupMap.put(key, new PrivilegeGroup(facilityServiceType));
            }
        }

        return new ArrayList<>(privilegeGroupMap.values());
    }

    private String getPrivilegeKey(AbstractAccountRequest request){
        return request.getInn() + request.getPassport() + request.getFio() + request.getCity() + request.getStreetType() +
                request.getStreet() + request.getBuildingNumber() + request.getBuildingCorp();
    }

    public PrivilegeGroup getPrivilegeGroup(Long requestFileId, String inn, String passport, String fio, String streetCode,
                                            String buildingNumber, String buildingCorp, String appartment){
        PrivilegeGroup privilegeGroup = new PrivilegeGroup();

        Long groupId = requestFileBean.getRequestFile(requestFileId).getGroupId();

        if (groupId == null){
            return privilegeGroup;
        }

        PrivilegeExample example = new PrivilegeExample();
        example.setGroupId(groupId);
        example.setInn(inn);
        example.setPassport(passport);
        example.setFio(fio);
        example.setStreetCode(streetCode);
        example.setBuilding(buildingNumber);
        example.setCorp(buildingCorp);
        example.setApartment(appartment);



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
