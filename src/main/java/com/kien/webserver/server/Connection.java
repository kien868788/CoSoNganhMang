package com.kien.webserver.server;

import com.kien.RequestHandler;
import com.kien.webserver.response.HttpRequestHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * One connection is one thread
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connection extends Thread{
    int portNumber = 8000;
    int executorNumber;
    WebSocket webSocket;
    Boolean error = false;
    String root;
    RequestHandler requestHandler;


    public Connection(ServerProperties properties, int executorNumber) {
        this.portNumber = properties.getPort();
        this.executorNumber = executorNumber;
        this.root = properties.getRoot();
        this.webSocket = WebSocket.getInstance(portNumber, properties.getMaxConnections());
    }

    public void run() {
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        // Accept a connection once it's free
        while (true) {
            try {
                if (error) {
                    return;
                }
                System.out.println("#" + executorNumber + ": Waiting for connection..");
                Socket clientSocket = webSocket.getServerSocket().accept();
                System.out.println("Accepted on #" + executorNumber);
                input = new BufferedInputStream(clientSocket.getInputStream());
                output = new BufferedOutputStream(clientSocket.getOutputStream());
//                 Read request
                while (true) {
                    try {
                        RequestHandler requestHandler = new HttpRequestHandler();

                        // TODO
                        clientSocket.setSoTimeout(15000);

                        byte[] response = requestHandler.response(input, root);

                        output.write(response);
                        output.flush();
                    } catch (SocketTimeoutException e) {
                        System.err.println("Timeout on port " + portNumber);
                        System.err.println(e.getMessage());
                        break;
                    }
                }

                input.close();
                output.close();
            }
            catch (IOException e) {
                System.err.println("#" + executorNumber + ": Error while reading data on port " + portNumber);
                System.err.println(e.getMessage());
                try {
                    input.close();
                    output.close();
                } catch (Exception ex) {
                    // Nothing to do here
                }
            }
        }
    }
}
