package ru.complitex.common.service.executor;

import ru.complitex.common.entity.IExecutorObject;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.12.10 14:49
 */
public interface IExecutorListener<T extends IExecutorObject>{
    void onComplete(List<T> processed);
    void onError(List<T> unprocessed);
}
