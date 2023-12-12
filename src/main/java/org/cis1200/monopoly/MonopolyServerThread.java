package org.cis1200.monopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.game.Player;
import org.cis1200.monopoly.game.PropertySpace;
import org.cis1200.monopoly.game.Space;
import org.cis1200.monopoly.state.MonopolyState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MonopolyServerThread extends Thread {
    private final Socket socket;
    private final MonopolyMultiServer server;
    private final ObjectMapper objectMapper;

    public MonopolyServerThread(Socket socket, MonopolyMultiServer server) {
        super();
        this.socket = socket;
        this.server = server;
        objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
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
        Board board = server.board;
        Player player1 = server.player1;
        Player player2 = server.player2;
        Player currentPlayer = server.currentPlayer;

        if (requestStr == null) {
            int newClientId;
            if (!server.player1Connected) {
                newClientId = 1;
                server.player1Connected = true;
            } else if (!server.player2Connected) {
                newClientId = 2;
                server.player2Connected = true;
            } else {
                return objectMapper.writeValueAsString(new ServerMessage("TOO_MANY_CLIENTS", -1, state()));
            }
            return objectMapper.writeValueAsString(new ServerMessage("PROVIDE_NAME", newClientId, state()));
        }
        ClientMessage request = objectMapper.readValue(requestStr, ClientMessage.class);
        ServerMessage response;
        if (request.type.equals("NAME")) {
            if (player1 != null && player2 != null) {
                response = new ServerMessage("OK", request.player, state());
            } else if (request.player == 1) {
                server.player1 = new Player(request.subject, 1, 1500);
                server.currentPlayer = server.player1;
                response = new ServerMessage("OK", request.player, state());
                System.out.println(server.currentPlayer);
            } else if (request.player == 2) {
                server.player2 = new Player(request.subject, 2, 1500);
                response = new ServerMessage("OK", request.player, state());
            } else {
                response = new ServerMessage("INVALID", request.player, state());
            }
        } else if (request.type.equals("WAITING")) {
            response = new ServerMessage("OK", request.player, state());
        } else if (request.player != currentPlayer.getId()) {
            response = new ServerMessage("NOT_TURN", request.player, state());
        } else {
            switch (request.type) {
                case "ROLL_DICE" -> {
                    if (!server.rolledDice) {
                        response = new ServerMessage(board.movePlayer(currentPlayer).toString(), request.player, state());
                        server.rolledDice = true;
                    } else {
                        response = new ServerMessage("ALREADY_ROLLED", request.player, state());
                    }
                }
                case "BUY_PROPERTY" -> {
                    Space space = board.getPlayerSpace(currentPlayer);
                    if (space instanceof PropertySpace) {
                        response = new ServerMessage(((PropertySpace) space).buyProperty(currentPlayer).toString(), request.player, state());
                    } else {
                        response = new ServerMessage("NOT_PROPERTY", request.player, state());
                    }
                }
                case "BUY_HOUSE" -> response = new ServerMessage(board.getProperty(request.subject).buyHouse(currentPlayer).toString(), request.player, state());
                case "SELL_HOUSE" -> response = new ServerMessage(board.getProperty(request.subject).sellHouse(currentPlayer).toString(), request.player, state());
                case "MORTGAGE" -> response = new ServerMessage(board.getProperty(request.subject).mortgageProperty(currentPlayer).toString(), request.player, state());
                case "UNMORTGAGE" -> response = new ServerMessage(board.getProperty(request.subject).unmortgageProperty(currentPlayer).toString(), request.player, state());
                case "END_TURN" -> {
                    if (!server.rolledDice) {
                        response = new ServerMessage("ROLL_FIRST", request.player, state());
                    } else {
                        server.rolledDice = false;
                        server.currentPlayer = (currentPlayer.getId() == 1) ? player2 : player1;
                        response = new ServerMessage("OK", request.player, state());
                    }
                }
                default -> response = new ServerMessage("INVALID", request.player, state());
            }
        }
        return objectMapper.writeValueAsString(response);
    }

    private MonopolyState state() {
        return FileHandler.toState(server.currentPlayer, server.board, server.player1, server.player2);
    }
}
