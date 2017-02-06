package org.complitex.common.service.executor;

import org.complitex.common.Module;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.BroadcastService;

import javax.ejb.EJB;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Anatoly Ivanov
 *         Date: 023 23.09.14 17:49
 */
public abstract class AbstractTaskBean<T extends IExecutorObject> implements ITaskBean<T> {
    @EJB
    private BroadcastService broadcastService;

    private static AtomicLong lastOnRequest = new AtomicLong(System.currentTimeMillis());

    protected <R> void onRequest(R request){
        if (System.currentTimeMillis() - lastOnRequest.get() > 100) {
            broadcastService.broadcast(getClass(), "onRequest", request);

            lastOnRequest.set(System.currentTimeMillis());
        }
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
