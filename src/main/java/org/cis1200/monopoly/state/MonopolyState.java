package org.cis1200.monopoly.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MonopolyState {
    @JsonProperty
    PlayerState player1;
    @JsonProperty
    PlayerState player2;
    @JsonProperty
    List<PropertyState> propertyStates;
    @JsonProperty
    int currentPlayerId;

    @JsonCreator
    public MonopolyState() {
        player1 = null;
        player2 = null;
        propertyStates = null;
        currentPlayerId = -1;
    }

    public MonopolyState(PlayerState player1,
                         PlayerState player2,
                         List<PropertyState> propertyStates,
                         int currentPlayerId) {
        this.player1 = player1;
        this.player2 = player2;
        this.propertyStates = propertyStates;
        this.currentPlayerId = currentPlayerId;
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public List<PropertyState> getPropertyStates() {
        return propertyStates;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }
}
