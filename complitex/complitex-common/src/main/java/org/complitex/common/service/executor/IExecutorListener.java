package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.12.10 14:49
 */
public interface IExecutorListener{
    public void onComplete(List<IExecutorObject> processed);
}
