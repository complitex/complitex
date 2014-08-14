/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.common.strategy;

import javax.ejb.ApplicationException;

/**
 *
 * @author Artem
 */
@ApplicationException(rollback = true)
public class DeleteException extends Exception {

    public DeleteException(Throwable cause) {
        super(cause);
    }

    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteException(String message) {
        super(message);
    }

    public DeleteException() {
    }
}
