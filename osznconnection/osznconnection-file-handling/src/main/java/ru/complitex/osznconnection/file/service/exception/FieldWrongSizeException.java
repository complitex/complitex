package ru.complitex.osznconnection.file.service.exception;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.10.2010 15:58:50
 */
public class FieldWrongSizeException extends AbstractException {
    private final static String MESSAGE_PATTERN = "Недопустимый размер поля {0}";

    public FieldWrongSizeException(String fieldName) {
        super(MESSAGE_PATTERN, fieldName);
    }
}
