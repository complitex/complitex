package org.complitex.common.service;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.complitex.common.entity.WebSocketPushMessage;
import org.complitex.common.service.executor.AsyncBean;
import org.complitex.common.wicket.BroadcastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author inheaven on 031 31.03.15 16:55
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class BroadcastService {
    private Logger log = LoggerFactory.getLogger(BroadcastService.class);

    private Application application;

    private WebSocketPushBroadcaster broadcaster;

    @EJB
    private AsyncBean asyncBean;

    public void setApplication(Application application){
        this.application = application;

        WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
        broadcaster = new WebSocketPushBroadcaster(webSocketSettings.getConnectionRegistry());
    }

    public void broadcast(Object service, Object payload){
        broadcast(service.getClass().getName(), payload);
    }

    public void broadcast(String service, Object payload){
        try {
            broadcaster.broadcastAll(application, new WebSocketPushMessage(service, payload));
        } catch (Exception e) {
            log.error("broadcast error", e);
        }
    }

    @Asynchronous
    public <T> void broadcast(Class producer, String key, T payload){
        broadcastSync(producer, key, payload);
    }

    private <T> void broadcastSync(Class producer, String key, T payload){
        try {
            broadcaster.broadcastAll(application, new BroadcastMessage<>(producer, key, payload));
        } catch (Exception e) {
            log.error("broadcast error", e);
        }
    }
}
