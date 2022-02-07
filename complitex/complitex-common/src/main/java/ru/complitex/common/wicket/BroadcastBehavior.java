package ru.complitex.common.wicket;

import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author inheaven on 18.06.2015 13:32.
 */
public abstract class BroadcastBehavior<T> extends WebSocketBehavior {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Class<?> producer;
    private String key;
    private Class<T> payloadClass;

    public BroadcastBehavior(Class producer) {
        this.producer = producer;
    }

    public BroadcastBehavior(Class producer, String key) {
        this.producer = producer;
        this.key = key;
    }

    public BroadcastBehavior(Class producer, Class<T> payload) {
        this.producer = producer;
        this.payloadClass = payload;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
        if (message instanceof BroadcastMessage) {
            BroadcastMessage p = (BroadcastMessage) message;

            if (producer.isAssignableFrom(p.getProducer()) &&
                    (key == null || key.equals(p.getKey())) &&
                    (payloadClass == null || (p.getPayload() != null &&
                            payloadClass.isAssignableFrom(p.getPayload().getClass()) &&  filter((T) p.getPayload())))) {
                try {
                    onBroadcast(handler, p.getKey(), (T) p.getPayload());
                } catch (Exception e) {
                    log.error("onBroadcast error", e);
                }
            }
        }
    }

    protected abstract void onBroadcast(WebSocketRequestHandler handler, String key, T payload);

    protected boolean filter(T payload){
        return true;
    }
}
