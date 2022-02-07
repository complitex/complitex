package ru.complitex.osznconnection.file.service.process;

import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.entity.AbstractRequest;

import java.util.List;

/**
 * @author inheaven on 001 01.07.16.
 */
public abstract class AbstractLoadRequestFile<T extends AbstractRequest> {

    public abstract Enum[] getFieldNames();

    public abstract T newObject();

    public abstract void save(List<T> batch) throws ExecuteException;

    public void postProcess(int rowNumber, T request) {
    }
}
