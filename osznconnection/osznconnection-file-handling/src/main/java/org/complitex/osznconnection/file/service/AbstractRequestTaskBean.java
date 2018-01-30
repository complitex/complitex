package org.complitex.osznconnection.file.service;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.service.process.ProcessType;

import javax.ejb.EJB;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Anatoly A. Ivanov
 * 30.01.2018 19:45
 */
public abstract class AbstractRequestTaskBean<T extends IExecutorObject> extends AbstractTaskBean<T> {
    @EJB
    private BroadcastService broadcastService;

    private static AtomicLong lastOnRequest = new AtomicLong(System.currentTimeMillis());

    protected void onRequest(AbstractRequest request, ProcessType processType){
        if (System.currentTimeMillis() - lastOnRequest.get() > 1000) {
            request.setProcessType(processType);

            broadcastService.broadcast(getClass(), "onRequest", request);

            lastOnRequest.set(System.currentTimeMillis());
        }
    }
}
