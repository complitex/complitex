package org.complitex.osznconnection.file.entity.privilege;

import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author inheaven on 014 14.11.16.
 */
public class FacilityLocal extends AbstractAccountRequest<FacilityLocalDBF>{
    public FacilityLocal() {
        super(RequestFileType.FACILITY_LOCAL);
    }

    public FacilityLocal(Long requestFileId){
        super(RequestFileType.FACILITY_LOCAL);

        setRequestFileId(requestFileId);
    }
}
