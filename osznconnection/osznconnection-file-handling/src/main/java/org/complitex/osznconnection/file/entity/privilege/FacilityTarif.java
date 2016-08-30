package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

public class FacilityTarif extends AbstractRequest<FacilityTarifDBF> {
    public FacilityTarif() {
        super(RequestFileType.FACILITY_TARIF_REFERENCE);
    }

    public FacilityTarif(Long requestFileId) {
        super(RequestFileType.FACILITY_TARIF_REFERENCE);

        setRequestFileId(requestFileId);
    }
}
