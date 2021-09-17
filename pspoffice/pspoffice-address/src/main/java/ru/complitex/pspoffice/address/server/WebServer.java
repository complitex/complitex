package ru.complitex.pspoffice.address.server;

import ch.qos.logback.classic.Level;
import org.apache.wicket.protocol.http.WicketServlet;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.*;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.pspoffice.address.application.WebApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.util.Properties;

import static java.util.concurrent.locks.LockSupport.park;

/**
 * @author Ivanov Anatoliy
 */
public class WebServer {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("pspoffice-address.properties"));
        } catch (IOException e) {
            System.out.println("pspoffice-address.properties not found");
        }

        String host = properties.getProperty("host", "localhost");
        int port = Integer.parseInt(properties.getProperty("port", "7777"));

        if (ObjectInputFilter.Config.getSerialFilter() == null) {
            ObjectInputFilter.Config.setSerialFilter(ObjectInputFilter.Config.createFilter("*"));
        }

        Logger logger = LoggerFactory.getLogger("ru.complitex");

        if (logger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) logger).setLevel(Level.toLevel(properties.getProperty("log.level", "info")));
        }

        NetworkListener networkListener = new NetworkListener("network-listener", host, port);
        networkListener.registerAddOn(new WebSocketAddOn());

        HttpServer httpServer = new HttpServer();
        httpServer.addListener(networkListener);

        WebappContext webappContext = new WebappContext("webapp-context");
        webappContext.addListener(Listener.class);

        webappContext.setSessionTimeout(Integer.MAX_VALUE);

        ServletRegistration servletRegistration = webappContext.addServlet(WebApplication.APPLICATION, new WicketServlet());
        servletRegistration.setInitParameter("applicationClassName", WebApplication.class.getName());
        servletRegistration.setInitParameter("filterMappingUrlPattern", "/*");

        WebSocketEngine.getEngine().register("", "/wicket/websocket", new WebSocketApplication() {
            @Override
            public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
                return new ru.complitex.pspoffice.address.server.WebSocket(handler, requestPacket, listeners);
            }
        });

        webappContext.deploy(httpServer);

        httpServer.start();

        park();
    }
}
