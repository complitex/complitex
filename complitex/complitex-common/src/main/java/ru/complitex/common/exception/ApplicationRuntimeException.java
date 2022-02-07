package ru.complitex.common.exception;

/**
 * @author inheaven on 001 01.09.15 17:29
 */
@javax.ejb.ApplicationException(rollback = true)
public class ApplicationRuntimeException extends RuntimeException{
    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
