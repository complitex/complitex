package org.complitex.correction.service.exception;


public class MoreOneCorrectionException extends CorrectionException {

    private String entity;

    public MoreOneCorrectionException(String entity) {
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }
}
