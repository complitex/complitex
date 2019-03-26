package org.complitex.osznconnection.file.entity.subsidy;

import org.complitex.osznconnection.file.entity.AbstractFieldMapEntity;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:25
 */
public class OschadbankRequest extends AbstractFieldMapEntity<OschadbankRequestField> {
    private Long id;
    private Long requestFileId;

    public OschadbankRequest() {
    }

    public OschadbankRequest(Long requestFileId) {
        this.requestFileId = requestFileId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestFileId() {
        return requestFileId;
    }

    public void setRequestFileId(Long requestFileId) {
        this.requestFileId = requestFileId;
    }
}
