package org.complitex.common.exception;

import javax.ejb.ApplicationException;

/**
 * @author Anatoly A. Ivanov
 *         28.02.2017 19:52
 */
@ApplicationException(rollback = true)
public class DBRuntimeException extends RuntimeException{
    public DBRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBRuntimeException(Throwable cause) {
        super(cause);
    }
}

