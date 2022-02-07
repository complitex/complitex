package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.entity.RequestFileType;

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
