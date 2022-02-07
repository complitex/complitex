package ru.complitex.osznconnection.file.service.exception;

import ru.complitex.common.exception.AbstractException;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MoreOneAccountException extends AbstractException {
    public MoreOneAccountException() {
        super("Найдено более одного лицевого счета");
    }

    public MoreOneAccountException(String s) {
        super("Найдено более одного лицевого счета: " + s);
    }
}
