package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityForm2 extends AbstractAccountRequest<FacilityForm2DBF> {
    public FacilityForm2() {
        super(RequestFileType.FACILITY_FORM2);
    }

    public FacilityForm2(Long requestFileId){
        super(RequestFileType.FACILITY_FORM2);

        setRequestFileId(requestFileId);
    }
}
