package ru.complitex.pspoffice.importing.legacy.service.exception;


import ru.complitex.common.exception.AbstractException;

/**
 *
 * @author Artem
 */
public class OpenErrorDescriptionFileException extends AbstractException {
    
    public OpenErrorDescriptionFileException(Throwable cause, String fileName) {
        super(cause, "Невозможно открыть файл описаний ошибок: {0}", fileName);
    }
    
}
