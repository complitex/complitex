package ru.complitex.osznconnection.file.service.exception;

import ru.complitex.common.exception.AbstractException;
import ru.complitex.osznconnection.file.entity.RequestFile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.09.2010 16:53:30
 */
public class AlreadyLoadedException extends AbstractException {
    private final static String MESSAGE_PATTERN = "Файл с названием: {0}, номером реестра: {1}, датой: {2} " +
            "и идентификатором организации: {3} уже загружен";

    public AlreadyLoadedException(RequestFile rf) {
        super(MESSAGE_PATTERN, rf.getName(), null, rf.getBeginDate(), rf.getOrganizationId());
    }
}
