package com.kien.webserver.server;

import com.kien.Main;
import lombok.Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Data
public class ServerProperties {
    String root;
    int totalThreads;
    int port;
    int maxConnections;

    public ServerProperties() {
        Properties prop = new Properties();
        InputStream inputproperties = null;

        try {
            // read property from application.properties file
            inputproperties = Main.class.getClassLoader().getResourceAsStream("application.properties");
            prop.load(inputproperties);

            this.root = prop.getProperty("root", "www");

            // Create directory to store file when init
            Path path = Paths.get(this.root);
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }

            String numThreadsStr = prop.getProperty("total-threads", "10");
            this.totalThreads = Integer.parseInt(numThreadsStr);
            String portStr = prop.getProperty("port", "8000");
            this.port = Integer.parseInt(portStr);
            String backlogStr = prop.getProperty("max-connections", "65536");
            this.maxConnections = Integer.parseInt(backlogStr);
        }
        catch (FileNotFoundException e) {
            System.err.println("Properties file not found. " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("Error while parsing properties file " + e.getMessage());
        }
    }
}
