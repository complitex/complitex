package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.osznconnection.file.entity.AbstractAddressRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityStreet extends AbstractAddressRequest<FacilityStreetDBF> {
    public FacilityStreet() {
        super(RequestFileType.FACILITY_STREET_REFERENCE);
    }


    public FacilityStreet(Long requestFileId) {
        super(RequestFileType.FACILITY_STREET_REFERENCE);

        setRequestFileId(requestFileId);
    }
}
