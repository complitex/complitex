package ru.complitex.osznconnection.file.service;

import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.service.BroadcastService;
import ru.complitex.common.service.executor.AbstractTaskBean;
import ru.complitex.osznconnection.file.entity.AbstractRequest;
import ru.complitex.osznconnection.file.service.process.ProcessType;

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
