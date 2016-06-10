package org.complitex.common.service;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.complitex.common.entity.WebSocketPushMessage;
import org.complitex.common.service.executor.AsyncService;
import org.complitex.common.wicket.BroadcastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.concurrent.Callable;

/**
 * @author inheaven on 031 31.03.15 16:55
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class BroadcastService {
    private Logger log = LoggerFactory.getLogger(BroadcastService.class);

    private Application application;

    private WebSocketPushBroadcaster broadcaster;

    @EJB
    private AsyncService asyncService;

    public void setApplication(Application application){
        this.application = application;

        WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
        broadcaster = new WebSocketPushBroadcaster(webSocketSettings.getConnectionRegistry());

        Executor executor = new Executor() {
            @Override
            public void run(Runnable command) {
                asyncService.async(command);
            }

            @Override
            public <T> T call(Callable<T> callable) throws Exception {
                return callable.call();
            }
        };

        webSocketSettings.setWebSocketPushMessageExecutor(executor);
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
        try {
            broadcaster.broadcastAll(application, new BroadcastMessage<>(producer, key, payload));
        } catch (Exception e) {
            log.error("broadcast error", e);
        }
    }
}
