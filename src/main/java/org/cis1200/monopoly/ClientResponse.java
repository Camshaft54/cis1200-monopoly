package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientResponse {
    @JsonProperty
    String type;
    @JsonProperty
    String subject;
    @JsonProperty
    int player;

    // types: rollDice, buyProperty, buyHouse, sellHouse, mortgage, unmortgage, END_TURN, NAME
}
