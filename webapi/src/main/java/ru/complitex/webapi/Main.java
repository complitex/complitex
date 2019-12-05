package ru.complitex.webapi;

import io.helidon.microprofile.server.Server;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:45 PM
 */
public class Main {
    public static void main(String[] args) {
        Server.create().start();
    }
}
