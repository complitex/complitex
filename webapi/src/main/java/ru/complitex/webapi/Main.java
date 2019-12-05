package ru.complitex.webapi;

import io.helidon.microprofile.server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:45 PM
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }

        Server.create().start();
    }
}
