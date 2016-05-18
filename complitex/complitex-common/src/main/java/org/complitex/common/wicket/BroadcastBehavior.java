package org.complitex.common.wicket;

import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author inheaven on 18.06.2015 13:32.
 */
public abstract class BroadcastBehavior extends WebSocketBehavior {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Class<?>[] producers;
    private String key;

    public BroadcastBehavior(Class<?>... producers) {
        this.producers = producers;
    }

    public BroadcastBehavior(Class producer, String key) {
        this.producers = new Class<?>[]{producer};
        this.key = key;
    }

    @Override
    protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
        if (message instanceof BroadcastMessage) {
            BroadcastMessage p = (BroadcastMessage) message;

            for (Class<?> producer : producers) {
                if (producer.isAssignableFrom(p.getProducer()) && (key == null || key.equals(p.getKey()))) {
                    try {
                        onBroadcast(handler, p.getKey(), p.getPayload());
                    } catch (Exception e) {
                        log.error("onBroadcast error", e);
                    }
                }
            }
        }
    }

    protected abstract void onBroadcast(WebSocketRequestHandler handler, String key, Object payload);
}
