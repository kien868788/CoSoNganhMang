package com.kien.webserver.server;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;

@NoArgsConstructor
public class WebSocket {
    private static WebSocket INSTANCE;

    int port;
    int maxConnections;
    ServerSocket serverSocket = null;

    public WebSocket(int port, int maxConnections) {
        this.port = port;
        this.maxConnections = maxConnections;
        try {
            serverSocket = new ServerSocket(port, maxConnections);
        } catch (IOException e) { System.err.println("Error while opening socket on port " + port);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Singleton design pattern here
     */
    public static WebSocket getInstance(int port, int maxConnections) {
        if (INSTANCE == null) {
            INSTANCE = new WebSocket(port, maxConnections);
        }
        return INSTANCE;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
