package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientResponse {
    @JsonProperty
    String type;
    @JsonProperty
    String subject;
    @JsonProperty
    int player;

    // types: rollDice, buyProperty, buyHouse, sellHouse, mortgage, unmortgage, END_TURN, NAME
    @JsonCreator
    public ClientResponse() {
        type = null;
        subject = null;
        player = -1;
    }

    public ClientResponse(String type, String subject, int player) {
        this.type = type;
        this.subject = subject;
        this.player = player;
    }
}
