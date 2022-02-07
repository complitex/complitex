package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.osznconnection.file.entity.AbstractFieldMapEntity;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2019 18:45
 */
public class OschadbankRequestFile extends AbstractFieldMapEntity<OschadbankRequestFileField> {
    private Long id;
    private Long requestFileId;

    public OschadbankRequestFile() {
    }

    public OschadbankRequestFile(Long requestFileId) {
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
