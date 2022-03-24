package ru.complitex.eirc.registry.service;

import ru.complitex.common.entity.ILocalizedType;
import ru.complitex.common.exception.AbstractException;

/**
 * @author Pavel Sknar
 */
public class TransitionNotAllowed extends AbstractException {
    private ILocalizedType type;

    public TransitionNotAllowed(String s) {
        super(s);
    }

    public TransitionNotAllowed(String s, ILocalizedType type) {
        super(s);
        this.type = type;
    }

    public ILocalizedType getType() {
        return type;
    }

    public void setType(ILocalizedType type) {
        this.type = type;
    }
}
