package org.cis1200.monopoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.state.MonopolyState;
import org.cis1200.monopoly.state.PlayerState;
import org.cis1200.monopoly.state.PropertyState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MonopolyClient implements Runnable {
    private final ClientGUI clientGUI;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<PropertyState> propertyStates;
    private PlayerState me;
    private PlayerState opponent;
    private int myPlayerId;
    private boolean isMyTurn;

    public MonopolyClient(Board board) {
        clientGUI = new ClientGUI(this, board.getSpaceNames(), board.getSpaceColors());
    }

    @Override
    public void run() {
        String hostname = "localhost";
        int portNumber = 80;
        try (
                Socket socket = new Socket(hostname, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String fromServer = in.readLine();
            String toServer;
            while ((fromServer != null)) {
                toServer = handleResponse(fromServer);
                out.println(toServer);
                System.out.println(toServer);
                fromServer = in.readLine();
                System.out.println(fromServer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleResponse(String responseStr) throws JsonProcessingException {
        System.out.println(responseStr);
        ServerMessage response = objectMapper.readValue(responseStr, ServerMessage.class);
        MonopolyState state = response.monopolyState;
        propertyStates = state.getPropertyStates();
        myPlayerId = response.clientId;
        if (myPlayerId == 1) {
            me = state.getPlayer1();
            opponent = state.getPlayer2();
        } else {
            me = state.getPlayer2();
            opponent = state.getPlayer1();
        }
        isMyTurn = state.getCurrentPlayerId() == myPlayerId;
        ClientMessage clientMessage;
        // React to server message
        switch (response.type) {
            case "PROVIDE_NAME" -> {
                return objectMapper.writeValueAsString(
                        new ClientMessage("NAME", clientGUI.provideName(), myPlayerId)
                );
            }
            case "NOT_TURN" -> {
                clientGUI.warnUser("It's not your turn!");
            }
            case "INVALID" -> {
                clientGUI.warnUser("Invalid action!");
            }
            case "ROLL_FIRST" -> {
                clientGUI.warnUser("Nice try, but you've got to roll first!");
            }
            case "ALREADY_ROLLED" -> {
                clientGUI.warnUser("I saw you roll already liar");
            }
            case "INSUFFICIENT_FUNDS" -> {
                clientGUI.warnUser("I know you want that, but you're poor :(");
            }
            case "NOT_OWNER" -> {
                clientGUI.warnUser("You think you own this place... well ya don't!");
            }
            case "NO_OWNER" -> {
                clientGUI.warnUser("No one owns this property, especially you!");
            }
            case "NO_HOUSES" -> {
                clientGUI.warnUser("Ok. Ok. Ok. Lemme get this straight. You want NEGATIVE houses on this property?! WHY? Are you trying to lose money?!");
            }
            case "MAX_HOUSES" -> {
                clientGUI.warnUser("Please stop putting more houses on this property. " + opponent.getName() + " hates you enough already.");
            }
            case "ALREADY_OWNED" -> {
                clientGUI.warnUser("Will you stop clicking \'Buy Property\' all the time? You already own this one!");
            }
            case "UNBALANCED_GROUP" -> {
                clientGUI.warnUser("Woah buddy, those other properties in this color group also want some attention.");
            }
            case "NOT_GROUP_OWNER" -> {
                clientGUI.warnUser("You've gotta own the whole group to do this, hotshot");
            }
            case "ALREADY_MORTGAGED" -> {
                clientGUI.warnUser("Quit acting like a private equity firm and stop mortgaging properties multiple times");
            }
            case "NOT_MORTGAGED" -> {
                clientGUI.warnUser("Looks like somebody never learned how to manage money. This property isn't even mortgaged, yet you want to spend money on mortgaging it?");
            }
            case "BUY_PROPERTY" -> {
                clientGUI.warnUser("Wanna buy a property?");
            }
            case "PAID_RENT" -> {
                clientGUI.warnUser("I hope you enjoyed your stay because you just paid rent!");
            }
        }

        // Respond to server
        if (opponent == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return objectMapper.writeValueAsString(new ClientMessage("WAITING", "", myPlayerId));
        } else if (!isMyTurn) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            CompletableFuture<ClientMessage> messageFuture = new CompletableFuture<>();
            clientGUI.update(messageFuture);
            return objectMapper.writeValueAsString(new ClientMessage("WAITING", "", myPlayerId));
        } else {
            CompletableFuture<ClientMessage> messageFuture = new CompletableFuture<>();
            clientGUI.update(messageFuture);
            clientMessage = messageFuture.join();
            return objectMapper.writeValueAsString(clientMessage);
        }
    }

    public List<PropertyState> getPropertyStates() {
        return propertyStates;
    }

    public PlayerState getMe() {
        return me;
    }

    public PlayerState getOpponent() {
        return opponent;
    }

    public int getId() {
        return myPlayerId;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }
}
