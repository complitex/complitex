package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:25
 */
public class OschadbankRequest extends AbstractRequest<OschadbankRequestField> {
    public OschadbankRequest() {
        super(RequestFileType.OSCHADBANK_REQUEST);
    }

    public OschadbankRequest(Long requestFileId) {
        this();

        setRequestFileId(requestFileId);
    }
}
