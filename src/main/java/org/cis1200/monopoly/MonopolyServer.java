package org.cis1200.monopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.game.Space;
import org.cis1200.monopoly.state.MonopolyState;
import org.cis1200.monopoly.game.Player;
import org.cis1200.monopoly.game.PropertySpace;

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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int clientsConnected = 0; // TODO implement multi client server

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
            System.out.println("Client connected. Passing state to new client...");
            String initialResponse = handleRequest(null);
            out.println(initialResponse);
            System.out.println("Sent state to client. Awaiting name...");

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

    public String handleRequest(String requestStr) throws IOException {
        if (requestStr == null) {
            int newClientId = (player1 == null) ? 1 : 2;
            return objectMapper.writeValueAsString(new ServerMessage("OK", newClientId, getState()));
        }
        ClientMessage request = objectMapper.readValue(requestStr, ClientMessage.class);
        ServerMessage response;
        if (request.type.equals("NAME")) {
            if (player1 != null && player2 != null) {
                response = new ServerMessage("INVALID", request.player, getState());
            } else if (request.player == 1) {
                player1 = new Player(request.subject, 1, 1500);
                currentPlayer = player1;
                response = new ServerMessage("OK", request.player, getState());
            } else if (request.player == 2) {
                player2 = new Player(request.subject, 2, 1500);
                response = new ServerMessage("OK", request.player, getState());
            } else {
                response = new ServerMessage("INVALID", request.player, getState());
            }
        } else if (request.player != currentPlayer.getId()) {
            response = new ServerMessage("NOT_TURN", request.player, getState());
        } else {
            switch (request.type) {
                case "ROLL_DICE" -> response = new ServerMessage(board.movePlayer(currentPlayer).toString(), request.player, getState());
                case "BUY_PROPERTY" -> {
                    Space space = board.getPlayerSpace(currentPlayer);
                    if (space instanceof PropertySpace) {
                        response = new ServerMessage(((PropertySpace) space).buyProperty(currentPlayer).toString(), request.player, getState());
                    } else {
                        response = new ServerMessage("NOT_PROPERTY", request.player, getState());
                    }
                }
                case "BUY_HOUSE" -> response = new ServerMessage(board.getProperty(request.subject).buyHouse(currentPlayer).toString(), request.player, getState());
                case "SELL_HOUSE" -> response = new ServerMessage(board.getProperty(request.subject).sellHouse(currentPlayer).toString(), request.player, getState());
                case "MORTGAGE" -> response = new ServerMessage(board.getProperty(request.subject).mortgageProperty(currentPlayer).toString(), request.player, getState());
                case "UNMORTGAGE" -> response = new ServerMessage(board.getProperty(request.subject).unmortgageProperty(currentPlayer).toString(), request.player, getState());
                case "END_TURN" -> {
                    currentPlayer = (currentPlayer.getId() == 1) ? player2 : player1;
                    response = new ServerMessage("OK", request.player, getState());
                }
                default -> response = new ServerMessage("INVALID", request.player, getState());
            }
        }
        return objectMapper.writeValueAsString(response);
    }

    private MonopolyState getState() throws IOException {
        return FileHandler.toState(currentPlayer, board, player1, player2);
    }
}
