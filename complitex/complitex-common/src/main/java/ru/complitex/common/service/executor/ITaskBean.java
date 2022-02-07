package ru.complitex.common.service.executor;

import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.ExecuteException;

import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 18:51
 */
public interface ITaskBean<T extends IExecutorObject> {
    boolean execute(T object, Map<?, ?> commandParameters) throws ExecuteException;

    String getModuleName();

    Class<?> getControllerClass();

    Log.EVENT getEvent();
}
