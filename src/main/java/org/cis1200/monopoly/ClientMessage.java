package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientMessage {
    @JsonProperty
    String type;
    @JsonProperty
    String subject;
    @JsonProperty
    int player;

    // types: rollDice, buyProperty, buyHouse, sellHouse, mortgage, unmortgage, END_TURN, NAME
    @JsonCreator
    public ClientMessage() {
        type = null;
        subject = null;
        player = -1;
    }

    public ClientMessage(String type, String subject, int player) {
        this.type = type;
        this.subject = subject;
        this.player = player;
    }
}
