package org.cis1200.monopoly.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cis1200.monopoly.game.Board;
import org.cis1200.monopoly.game.Player;
import org.cis1200.monopoly.game.PropertySpace;
import org.cis1200.monopoly.game.Space;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
