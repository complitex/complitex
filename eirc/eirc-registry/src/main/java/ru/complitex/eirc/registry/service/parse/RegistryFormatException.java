package ru.complitex.eirc.registry.service.parse;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Pavel Sknar
 */
public class RegistryFormatException extends AbstractException {

    private Long position;

    public RegistryFormatException(String s) {
        super(s);
    }

    public RegistryFormatException(String s, Long position) {
        super(s);
        this.position = position;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }
}
