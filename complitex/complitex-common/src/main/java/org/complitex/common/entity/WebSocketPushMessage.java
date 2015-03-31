package org.complitex.common.entity;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

/**
 * @author inheaven on 031 31.03.15 17:02
 */
public class WebSocketPushMessage<T> implements IWebSocketPushMessage {
    private T payload;

    public WebSocketPushMessage(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }
}
