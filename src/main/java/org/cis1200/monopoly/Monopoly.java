package org.cis1200.monopoly;

import java.io.IOException;

public class Monopoly implements Runnable {
    public void run() {
        // Prompt user to choose between server or client
        boolean clientMode = true;
        // Run client or server
        // noinspection ConstantValue
        if (clientMode) {
            runClient();
        } else {
            runServer();
        }
    }

    public void runClient() {
        // Prompt user to select board file
        String filepath = "files/classicBoard.json";
        try {
            Board b = FileHandler.loadBoard(filepath);
            // Start websocket
            MonopolyClient monopolyClient = new MonopolyClient();
            monopolyClient.run();
            Player currentPlayer = null;
            // Open window to represent server status (clients connected, etc.)
        } catch (IOException io) {
            System.out.println("Could not load board from file!");
        }
    }

    public void runServer() {
        // Prompt user to select board file
        String filepath = "files/classicBoard.json";
        try {
            Board b = FileHandler.loadBoard(filepath);
            // Start websocket
            MonopolyServer monopolyServer = new MonopolyServer(b);
            monopolyServer.run();
            // Open window to represent server status (clients connected, etc.)
        } catch (IOException io) {
            System.out.println("Could not load board from file!");
        }
    }
}
