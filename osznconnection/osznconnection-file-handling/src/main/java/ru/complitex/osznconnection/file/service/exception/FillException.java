package ru.complitex.osznconnection.file.service.exception;

import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.11.10 18:45
 */
public class FillException extends ExecuteException{
    private final static String MESSAGE_PATTERN = "Ошибка обработки файла запроса {0}";

    public FillException(boolean warn, RequestFile requestFile) {
        super(warn, MESSAGE_PATTERN, requestFile.getName());
    }

    public FillException(Throwable cause, boolean warn, RequestFile requestFile) {
        super(cause, warn, MESSAGE_PATTERN, requestFile.getFullName());
    }

    public FillException(Throwable cause, boolean warn, RequestFileGroup group) {
        super(cause, warn, MESSAGE_PATTERN, group.getFullName());
    }
}
