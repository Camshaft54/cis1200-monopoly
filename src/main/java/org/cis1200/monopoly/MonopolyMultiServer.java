package org.cis1200.monopoly;

import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.game.Player;

import java.io.IOException;
import java.net.ServerSocket;

public class MonopolyMultiServer implements Runnable {
    public final Board board;
    public Player player1;
    public Player player2;
    public Player currentPlayer;
    public boolean rolledDice;
    public int clientsConnected = 0;
    public boolean player1Connected = false;
    public boolean player2Connected = false;

    public MonopolyMultiServer(Board board) {
        this.board = board;
        this.player1 = null;
        this.player2 = null;
        this.currentPlayer = null;
    }

    @Override
    public void run() {
        try (
                ServerSocket server = new ServerSocket(80)
        ) {
            while (clientsConnected < 3) {
                new MonopolyServerThread(server.accept(), this).start();
                clientsConnected++;
            }
        } catch (IOException e) {
            // TODO implement cleanly
            throw new RuntimeException(e);
        }
    }
}
