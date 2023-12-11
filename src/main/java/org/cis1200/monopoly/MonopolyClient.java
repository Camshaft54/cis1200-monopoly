package org.cis1200.monopoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.state.MonopolyState;
import org.cis1200.monopoly.state.PlayerState;
import org.cis1200.monopoly.state.PropertyState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class MonopolyClient implements Runnable {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<PropertyState> propertyStates;
    private PlayerState me;
    private PlayerState opponent;
    private int myPlayerId;
    private int currentPlayerId;
    private boolean isMyTurn;

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
                fromServer = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleResponse(String responseStr) throws JsonProcessingException {
        ServerResponse response = objectMapper.readValue(responseStr, ServerResponse.class);
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
        currentPlayerId = state.getCurrentPlayerId();
        ClientResponse request;
        switch (response.type) {
            // TODO Do swing stuff depending on server response type
        }
        // TODO make separate method for responding to server
        return null;
    }
}
