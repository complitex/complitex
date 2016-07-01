package org.complitex.osznconnection.file.service.process;

import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.AbstractRequest;

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
