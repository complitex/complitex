/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;

/**
 *
 * @author Artem
 */
public class OpenErrorFileException extends AbstractException {
    
    public OpenErrorFileException(Throwable cause, String fileName) {
        super(cause, "Невозможно открыть файл ошибок: {0}", fileName);
    }
    
}
