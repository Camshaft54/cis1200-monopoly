package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cis1200.monopoly.state.MonopolyState;

public class ServerMessage {
    // types: OK, NOT_TURN, INVALID, TOO_MANY_CLIENTS, PROVIDE_NAME, ROLL_FIRST, ALREADY_ROLLED, or a misc. property response, or a misc. space prompt
    @JsonProperty
    public String type;
    @JsonProperty
    public int clientId;
    @JsonProperty
    public MonopolyState monopolyState;

    @JsonCreator
    public ServerMessage() {
        type = null;
        clientId = -1;
        monopolyState = null;
    }

    public ServerMessage(@JsonProperty("type") String type, @JsonProperty("clientId") int clientId, @JsonProperty("monopolyState") MonopolyState monopolyState) {
        this.type = type;
        this.clientId = clientId;
        this.monopolyState = monopolyState;
    }
}
