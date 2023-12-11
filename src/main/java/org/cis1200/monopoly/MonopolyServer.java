package org.cis1200.monopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.state.MonopolyState;

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

    public String handleRequest(String requestStr) throws IOException {
        MonopolyState state = FileHandler.toState(currentPlayer, board, player1, player2);
        if (requestStr == null) {
            int newClientId = (player1 == null) ? 1 : 2;
            return objectMapper.writeValueAsString(new ServerResponse("OK", newClientId, state));
        }
        ClientResponse request = objectMapper.readValue(requestStr, ClientResponse.class);
        ServerResponse response;
        if (request.type.equals("NAME")) {
            if (player1 != null && player2 != null) {
                response = new ServerResponse("INVALID", request.player, state);
            } else if (request.player == 1) {
                player1 = new Player(request.subject, 1, 1500);
                currentPlayer = player1;
                response = new ServerResponse("OK", request.player, state);
            } else if (request.player == 2) {
                player2 = new Player(request.subject, 2, 1500);
                response = new ServerResponse("OK", request.player, state);
            } else {
                response = new ServerResponse("INVALID", request.player, state);
            }
        } else if (request.player != currentPlayer.getId()) {
            response = new ServerResponse("NOT_TURN", request.player, state);
        } else {
            switch (request.type) {
                case "ROLL_DICE" -> response = new ServerResponse(board.movePlayer(currentPlayer).toString(), request.player, state);
                case "BUY_PROPERTY" -> {
                    Space space = board.getPlayerSpace(currentPlayer);
                    if (space instanceof PropertySpace) {
                        response = new ServerResponse(((PropertySpace) space).buyProperty(currentPlayer).toString(), request.player, state);
                    } else {
                        response = new ServerResponse("NOT_PROPERTY", request.player, state);
                    }
                }
                case "BUY_HOUSE" -> {
                    response = new ServerResponse(board.getProperty(request.subject).buyHouse(currentPlayer).toString(), request.player, state);
                }
                case "SELL_HOUSE" -> {
                    response = new ServerResponse(board.getProperty(request.subject).sellHouse(currentPlayer).toString(), request.player, state);
                }
                case "MORTGAGE" -> {
                    response = new ServerResponse(board.getProperty(request.subject).mortgageProperty(currentPlayer).toString(), request.player, state);
                }
                case "UNMORTGAGE" -> {
                    response = new ServerResponse(board.getProperty(request.subject).unmortgageProperty(currentPlayer).toString(), request.player, state);
                }
                case "END_TURN" -> {
                    currentPlayer = (currentPlayer.getId() == 1) ? player2 : player1;
                    response = new ServerResponse("OK", request.player, state);
                }
                default -> {
                    response = new ServerResponse("INVALID", request.player, state);
                }
            }
        }
        return objectMapper.writeValueAsString(response);
    }
}
