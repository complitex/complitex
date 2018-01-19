package org.complitex.sync.handler;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 21:43
 */
public class CorrectionNotFoundException extends RuntimeException{
    public CorrectionNotFoundException(String message) {
        super(message);
    }
}
