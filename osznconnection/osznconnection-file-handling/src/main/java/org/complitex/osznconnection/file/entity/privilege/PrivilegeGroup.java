package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * inheaven on 18.04.2016.
 */
public class PrivilegeGroup extends AbstractRequest {
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

    @Override
    public RequestFileType getRequestFileType() {
        return null;
    }
}
