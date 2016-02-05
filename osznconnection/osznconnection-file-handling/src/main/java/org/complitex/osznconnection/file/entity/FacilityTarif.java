package org.complitex.osznconnection.file.entity;

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
