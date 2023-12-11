package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cis1200.monopoly.state.MonopolyState;

public class ServerResponse {
    // types: OK, NOT_TURN, INVALID, or a misc. property response, or a misc. space prompt
    @JsonProperty
    String type;
    @JsonProperty
    int clientId;

    @JsonProperty
    MonopolyState monopolyState;

    public ServerResponse(String type, int clientId, MonopolyState monopolyState) {
        this.type = type;
        this.clientId = clientId;
        this.monopolyState = monopolyState;
    }
}
