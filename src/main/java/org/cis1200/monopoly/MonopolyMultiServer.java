package org.cis1200.monopoly;

import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.game.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonopolyMultiServer implements Runnable {
    public Board board;
    public Player player1;
    public Player player2;
    public Player currentPlayer;
    public boolean rolledDice;
    public boolean player1Connected = false;
    public boolean player2Connected = false;

    public MonopolyMultiServer(Board board) {
        try {
            FileHandler.RecoveredState state = FileHandler.recoverState(board);
            this.board = state.board;
            this.player1 = state.player1;
            this.player2 = state.player2;
            this.currentPlayer = state.currentPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not locate a saved game. Creating new one.");
            this.board = board;
            this.player1 = null;
            this.player2 = null;
            this.currentPlayer = null;
        }
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                FileHandler.saveState(currentPlayer, board, player1, player2);
            } catch (IOException e) {
                System.out.println("Could not save game state to files/save.json!");
            }
        }, 10, 10, TimeUnit.SECONDS);

        try (
                ServerSocket server = new ServerSocket(80)
        ) {
            List<MonopolyServerThread> connections = new LinkedList<>();
            while (connections.size() < 3) {
                MonopolyServerThread thread = new MonopolyServerThread(server.accept(), this);
                thread.start();
                connections.add(thread);
                connections.removeIf(th -> !th.isAlive());

            }
        } catch (IOException e) {
            // TODO implement cleanly
            throw new RuntimeException(e);
        }
    }
}
