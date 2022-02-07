package ru.complitex.common.entity;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

/**
 * @author inheaven on 031 31.03.15 17:02
 */
public class WebSocketPushMessage implements IWebSocketPushMessage {
    private String service;
    private Object payload;

    public WebSocketPushMessage(String service, Object payload) {
        this.service = service;
        this.payload = payload;
    }

    public String getService() {
        return service;
    }

    public Object getPayload() {
        return payload;
    }
}
