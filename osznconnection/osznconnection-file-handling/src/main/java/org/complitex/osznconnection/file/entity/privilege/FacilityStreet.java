package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAddressRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityStreet extends AbstractAddressRequest<FacilityStreetDBF> {
    public FacilityStreet() {
        super(RequestFileType.FACILITY_STREET);
    }


    public FacilityStreet(Long requestFileId) {
        super(RequestFileType.FACILITY_STREET);

        setRequestFileId(requestFileId);
    }
}
