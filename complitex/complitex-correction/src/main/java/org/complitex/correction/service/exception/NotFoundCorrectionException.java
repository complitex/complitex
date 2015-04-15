package org.complitex.correction.service.exception;

public class NotFoundCorrectionException extends CorrectionException {

    private String entity;

    public NotFoundCorrectionException(String entity) {
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }
}
