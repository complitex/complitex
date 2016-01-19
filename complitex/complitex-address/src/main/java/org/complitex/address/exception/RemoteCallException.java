package org.complitex.address.exception;

/**
 * @author inheaven on 003 03.07.15 19:30
 */
public class RemoteCallException extends Exception{
    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteCallException(Throwable cause) {
        super(cause);
    }
}
