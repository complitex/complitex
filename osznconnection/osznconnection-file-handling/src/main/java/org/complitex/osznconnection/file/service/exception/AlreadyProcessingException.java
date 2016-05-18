package org.complitex.osznconnection.file.service.exception;

import org.complitex.common.exception.AbstractException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.01.11 15:37
 */
public class AlreadyProcessingException extends AbstractException {
    private final static String MESSAGE_PATTERN = "Файл {0} уже обрабатывается";

    public AlreadyProcessingException(String fileName) {
        super(MESSAGE_PATTERN, fileName);
    }
}

