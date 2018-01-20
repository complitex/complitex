package org.complitex.sync.handler;

import javax.ejb.ApplicationException;

/**
 * @author Anatoly A. Ivanov
 * 19.01.2018 21:43
 */
@ApplicationException(rollback = true)
public class CorrectionNotFoundException extends RuntimeException{
    public CorrectionNotFoundException(String message) {
        super(message);
    }
}
