package ru.complitex.osznconnection.file.entity.subsidy;

import ru.complitex.osznconnection.file.entity.AbstractFieldMapEntity;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:34
 */
public class OschadbankResponseFile extends AbstractFieldMapEntity<OschadbankResponseFileField> {
    private Long id;
    private Long requestFileId;

    public OschadbankResponseFile(){
    }

    public OschadbankResponseFile(Long requestFileId){
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
