package ru.complitex.eirc.mb_transformer.service;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Pavel Sknar
 */
public class MbConverterException extends AbstractException {
    public MbConverterException(boolean initial, Throwable cause, String pattern, Object... arguments) {
        super(initial, cause, pattern, arguments);
    }

    public MbConverterException(Throwable cause, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
    }

    public MbConverterException(String pattern, Object... arguments) {
        super(pattern, arguments);
    }
}
