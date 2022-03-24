package ru.complitex.eirc.account.server;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.ws.api.AbstractWebSocketConnection;
import org.apache.wicket.protocol.ws.api.AbstractWebSocketProcessor;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.IWebSocketProcessor;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import ru.complitex.eirc.account.application.WebApplication;

import java.nio.ByteBuffer;

/**
 * @author Ivanov Anatoliy
 */
public class WebSocket extends DefaultWebSocket {
    private IWebSocketProcessor webSocketProcessor;

    public WebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
    }

    @Override
    public void onConnect() {
        super.onConnect();

        WebApplication webApplication = (WebApplication) WebApplication.get(WebApplication.APPLICATION);

        try {
            ThreadContext.setApplication(webApplication);

            webSocketProcessor = new AbstractWebSocketProcessor(getUpgradeRequest(), webApplication) {
                {
                    onConnect(new AbstractWebSocketConnection(this) {
                        @Override
                        public boolean isOpen() {
                            return WebSocket.this.isConnected();
                        }

                        @Override
                        public void close(int code, String reason) {
                            WebSocket.this.close(code, reason);
                        }

                        @Override
                        public IWebSocketConnection sendMessage(String message) {
                            WebSocket.this.send(message);

                            return this;
                        }

                        @Override
                        public IWebSocketConnection sendMessage(byte[] message, int offset, int length) {
                            WebSocket.this.send(ByteBuffer.wrap(message, offset, length).array());

                            return this;
                        }
                    });
                }

                @Override
                public void onOpen(Object containerConnection) {}
            };
        } finally {
            ThreadContext.detach();
        }
    }

    @Override
    public void onMessage(byte[] data) {
        super.onMessage(data);

        webSocketProcessor.onMessage(data, 0, data.length);
    }

    @Override
    public void onMessage(String text) {
        super.onMessage(text);

        webSocketProcessor.onMessage(text);
    }

    @Override
    public void onClose(DataFrame frame) {
        super.onClose(frame);

        ClosingFrame closingFrame = (ClosingFrame) frame;

        webSocketProcessor.onClose(closingFrame.getCode(), closingFrame.getReason());
    }
}
