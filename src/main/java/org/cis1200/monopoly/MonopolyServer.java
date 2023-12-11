package org.cis1200.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.WebSocket;

public class MonopolyServer implements Runnable, WebSocket.Listener {
    private final Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    public MonopolyServer(Board board) {
        this.board = board;
        this.player1 = null;
        this.player2 = null;
        this.currentPlayer = null;
    }

    @Override
    public void run() {
        try (
                ServerSocket server = new ServerSocket(80);
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String initialResponse = handleRequest(null);
            out.println(initialResponse);

            String inputLine = in.readLine();
            String outputLine;
            while (inputLine != null) {
                outputLine = handleRequest(inputLine);
                out.println(outputLine);
                inputLine = in.readLine();
            }
        } catch (IOException e) {
            // TODO implement cleanly
            throw new RuntimeException(e);
        }

    }

    public String handleRequest(String request) {
        // TODO implement
        return null;
    }
}
