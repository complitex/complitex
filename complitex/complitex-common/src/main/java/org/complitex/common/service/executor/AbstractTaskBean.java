package org.complitex.common.service.executor;

import org.complitex.common.Module;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.BroadcastService;

import javax.ejb.EJB;

/**
 * @author Anatoly Ivanov
 *         Date: 023 23.09.14 17:49
 */
public abstract class AbstractTaskBean<T extends IExecutorObject> implements ITaskBean<T> {
    @EJB
    private BroadcastService broadcastService;

    protected <R> void onRequest(R request){
        broadcastService.broadcast(getClass(), "onRequest", request);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
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
