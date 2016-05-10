package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;

import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 18:51
 */
public interface ITaskBean<T extends IExecutorObject> {
    boolean execute(T object, Map commandParameters) throws ExecuteException;

    String getModuleName();

    Class getControllerClass();

    Log.EVENT getEvent();
}
