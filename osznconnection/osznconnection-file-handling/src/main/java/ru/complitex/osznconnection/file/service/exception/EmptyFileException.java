package ru.complitex.osznconnection.file.service.exception;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.05.11 13:43
 */
public class EmptyFileException extends AbstractException {
    public EmptyFileException() {
        super("Пустой файл");
    }
}
