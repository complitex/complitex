package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.common.entity.LogChangeList;
import ru.complitex.osznconnection.file.entity.AbstractRequestFileGroup;
import ru.complitex.osznconnection.file.entity.RequestFile;

/**
 * inheaven on 04.04.2016.
 */
public class PrivilegeFileGroup extends AbstractRequestFileGroup {
    public RequestFile getDwellingCharacteristicsRequestFile(){
        return getFirstRequestFile();
    }

    public void setDwellingCharacteristicsRequestFile(RequestFile dwellingCharacteristicsRequestFile){
        setFirstRequestFile(dwellingCharacteristicsRequestFile);
    }

    public RequestFile getFacilityServiceTypeRequestFile(){
        return getSecondRequestFile();
    }

    public void setFacilityServiceTypeRequestFile(RequestFile facilityServiceTypeRequestFile){
        setSecondRequestFile(facilityServiceTypeRequestFile);
    }

    @Override
    public LogChangeList getLogChangeList() {
        return null;
    }
}
