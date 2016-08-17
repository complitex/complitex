package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityTarif extends AbstractRequest<FacilityTarifDBF> {
    public FacilityTarif() {
        super(RequestFileType.FACILITY_TARIF);
    }

    public FacilityTarif(Long requestFileId) {
        super(RequestFileType.FACILITY_TARIF);

        setRequestFileId(requestFileId);
    }
}
