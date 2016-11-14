package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityForm2 extends AbstractAccountRequest<FacilityForm2DBF> {
    public FacilityForm2() {
        super(RequestFileType.FACILITY_FORM2);
    }
}
