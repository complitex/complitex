package org.complitex.common.wicket;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.io.Serializable;

/**
 * @author inheaven on 17.06.2015 22:04.
 */
public class BroadcastMessage<T> implements IWebSocketPushMessage, Serializable {
    private Class producer;
    private String key;
    private T payload;

    public BroadcastMessage(Class producer, String key, T payload) {
        this.producer = producer;
        this.key = key;
        this.payload = payload;
    }

    public Class getProducer() {
        return producer;
    }

    public String getKey() {
        return key;
    }

    public T getPayload() {
        return payload;
    }
}