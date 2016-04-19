package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 *
 * @author Artem
 */
public class FacilityForm2 extends AbstractAccountRequest<FacilityForm2DBF> {
    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.FACILITY_FORM2;
    }
}
