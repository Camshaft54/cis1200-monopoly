package org.cis1200.monopoly;

import org.cis1200.monopoly.game.Board;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Monopoly implements Runnable {
    public void run() {
        displayInstructions();
    }

    private void startGame() {
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

    public void displayInstructions() {
        JFrame frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        JTextArea jTextArea = new JTextArea("""
                Hello! Welcome to Zen Monopoly.
                Zen Monopoly is a streamlined version of monopoly designed to make you relax and reflect. After clicking
                continue at the bottom, two windows will popup, one for each player. Everyone starts with $1500. When
                it's you're turn, you can roll the dice and move around the board. When you land on a property, you have
                the option to buy it. If you eventually buy all the properties with the same color (note that Railroads 
                and Utilities are separate), then you will be able to buy or sell houses on the properties in this
                group. If you're short on cash from paying rent after landing on your opponent's property, you can
                mortgage your properties. When you've made back some money you can then unmortgage it again.If you lost
                your progress due to a sudden rage quit (which hopefully won't happen), the game will automatically
                recover your progress (autosave occurs every 10 seconds). Have fun!""");
        frame.add(jTextArea);
        JButton button = new JButton("Continue");
        button.addActionListener((e) -> {
            startGame();
            frame.setVisible(false);
            frame.dispose();
        });
        frame.add(button);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
