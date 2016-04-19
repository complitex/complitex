package org.complitex.osznconnection.file.entity.privilege;

/**
 * inheaven on 18.04.2016.
 */
public class PrivilegeGroup {
    private DwellingCharacteristics dwellingCharacteristics;
    private FacilityServiceType facilityServiceType;

    public PrivilegeGroup() {
    }

    public PrivilegeGroup(DwellingCharacteristics dwellingCharacteristics) {
        this.dwellingCharacteristics = dwellingCharacteristics;
    }

    public PrivilegeGroup(FacilityServiceType facilityServiceType) {
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
}
