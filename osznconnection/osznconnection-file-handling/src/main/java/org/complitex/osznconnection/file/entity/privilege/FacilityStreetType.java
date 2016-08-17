package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityStreetType extends AbstractRequest<FacilityStreetTypeDBF> {
    public FacilityStreetType() {
        super(RequestFileType.FACILITY_STREET_TYPE);
    }

    public FacilityStreetType(Long requestFileId) {
        super(RequestFileType.FACILITY_STREET_TYPE);

        setRequestFileId(requestFileId);
    }
}
