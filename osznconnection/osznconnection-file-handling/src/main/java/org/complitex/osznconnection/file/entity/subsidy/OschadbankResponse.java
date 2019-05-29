package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:33
 */
public class OschadbankResponse extends AbstractRequest<OschadbankResponseField> {
    public OschadbankResponse() {
        super(RequestFileType.OSCHADBANK_RESPONSE);
    }

    public OschadbankResponse(Long requestFileId){
        this();

        setRequestFileId(requestFileId);
    }
}
