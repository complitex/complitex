package org.complitex.common.service.exception;

import javax.ejb.ApplicationException;
import java.text.MessageFormat;

/**
 * @author inheaven on 017 17.02.16 18:32
 */
@ApplicationException(rollback=true)
public class ServiceRuntimeException extends RuntimeException{
    public ServiceRuntimeException(String pattern, Object... arguments) {
        super(MessageFormat.format(pattern, arguments));
    }
}
