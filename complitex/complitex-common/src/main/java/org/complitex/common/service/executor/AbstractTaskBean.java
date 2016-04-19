package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;

/**
 * @author Anatoly Ivanov
 *         Date: 023 23.09.14 17:49
 */
public abstract class AbstractTaskBean<T extends IExecutorObject> implements ITaskBean<T> {
    @Override
    public String getModuleName() {
        return "AbstractTaskBean";
    }

    @Override
    public Class getControllerClass() {
        return getClass();
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.EDIT;
    }
}
