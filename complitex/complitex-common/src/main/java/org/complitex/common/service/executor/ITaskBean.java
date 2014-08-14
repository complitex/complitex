package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 18:51
 */
@Local
public interface ITaskBean<T extends IExecutorObject> {
    public boolean execute(T object, Map commandParameters) throws ExecuteException;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onError(T object);

    public String getModuleName();

    public Class getControllerClass();

    public Log.EVENT getEvent();
}
