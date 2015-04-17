package org.complitex.common.service;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.IWebSocketSettings;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.complitex.common.entity.WebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;

/**
 * @author inheaven on 031 31.03.15 16:55
 */
@Singleton
public class BroadcasterService {
    private Logger log = LoggerFactory.getLogger(BroadcasterService.class);

    private Application application;

    private WebSocketPushBroadcaster broadcaster;

    public void setApplication(Application application){
        this.application = application;

        IWebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
        broadcaster = new WebSocketPushBroadcaster(webSocketSettings.getConnectionRegistry());
    }

    public void broadcast(Object service, Object payload){
        broadcast(service.getClass().getName(), payload);
    }

    @Asynchronous
    public void broadcast(String service, Object payload){
        try {
            broadcaster.broadcastAll(application, new WebSocketPushMessage(service, payload));
        } catch (Exception e) {
            log.error("broadcast error", e);
        }
    }
}
