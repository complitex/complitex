package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 *
 * @author Artem
 */
public class FacilityTarif extends AbstractRequest<FacilityTarifDBF> {
    public FacilityTarif() {
    }

    public FacilityTarif(Long requestFileId) {
        setRequestFileId(requestFileId);
    }

    @Override
    public RequestFileType getRequestFileType() {
        return RequestFileType.FACILITY_TARIF;
    }
}
