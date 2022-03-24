package ru.complitex.eirc.registry.service.handle.exchange;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Pavel Sknar
 */
public class DataNotFoundException extends AbstractException {
    public DataNotFoundException(boolean initial, Throwable cause, String pattern, Object... arguments) {
        super(initial, cause, pattern, arguments);
    }

    public DataNotFoundException(Throwable cause, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
    }

    public DataNotFoundException(String pattern, Object... arguments) {
        super(pattern, arguments);
    }
}
