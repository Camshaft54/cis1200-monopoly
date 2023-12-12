package org.cis1200.monopoly;

import org.cis1200.monopoly.game.Board;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Monopoly implements Runnable {
    public void run() {
        String filepath = "files/classicBoard.json";
        try {
            Board b = FileHandler.loadBoard(filepath);
            Thread server = new Thread(() -> runServer(b));
            server.setDaemon(false);
            server.start();
            new Thread(() -> runClient(b)).start();
            new Thread(() -> runClient(b)).start();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load board from file!");
            JOptionPane.showMessageDialog(new JFrame(), "Could not locate files/classicBoard.json. Client will be terminated.");
        } catch (IOException io) {
            System.out.println("Could not load board from file!");
            JOptionPane.showMessageDialog(new JFrame(), "Could not parse board file. Client will be terminated.");
        }
    }

    public void runClient(Board b) {
        MonopolyClient monopolyClient = new MonopolyClient(b);
        monopolyClient.run();
    }

    public void runServer(Board b) {
        MonopolyMultiServer monopolyServer = new MonopolyMultiServer(b);
        monopolyServer.run();
    }
}
