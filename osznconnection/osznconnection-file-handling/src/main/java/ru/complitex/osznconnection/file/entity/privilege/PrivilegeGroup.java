package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.osznconnection.file.entity.AbstractRequest;

/**
 * inheaven on 18.04.2016.
 */
public class PrivilegeGroup extends AbstractRequest {
    private DwellingCharacteristics dwellingCharacteristics;
    private FacilityServiceType facilityServiceType;

    public PrivilegeGroup() {
        super(null);
    }

    public PrivilegeGroup(DwellingCharacteristics dwellingCharacteristics) {
        super(null);

        this.dwellingCharacteristics = dwellingCharacteristics;
    }

    public PrivilegeGroup(FacilityServiceType facilityServiceType) {
        super(null);

        this.facilityServiceType = facilityServiceType;
    }

    public DwellingCharacteristics getDwellingCharacteristics() {
        return dwellingCharacteristics;
    }

    public void setDwellingCharacteristics(DwellingCharacteristics dwellingCharacteristics) {
        this.dwellingCharacteristics = dwellingCharacteristics;
    }

    public FacilityServiceType getFacilityServiceType() {
        return facilityServiceType;
    }

    public void setFacilityServiceType(FacilityServiceType facilityServiceType) {
        this.facilityServiceType = facilityServiceType;
    }

    @Override
    public Long getGroupId() {
        if (dwellingCharacteristics != null){
            return dwellingCharacteristics.getGroupId();
        }

        if (facilityServiceType != null){
            return facilityServiceType.getGroupId();
        }

        return null;
    }
}
