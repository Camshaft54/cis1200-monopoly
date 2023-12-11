package org.cis1200.monopoly.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyState {
    @JsonProperty
    String name;
    @JsonProperty
    int ownerId;
    @JsonProperty
    boolean isMortgaged;
    @JsonProperty
    int numHouses;

    @JsonCreator
    public PropertyState() {
        name = null;
        ownerId = -1;
        isMortgaged = false;
        numHouses = -1;
    }

    public PropertyState(String name, int ownerId, boolean isMortgaged, int numHouses) {
        this.name = name;
        this.ownerId = ownerId;
        this.isMortgaged = isMortgaged;
        this.numHouses = numHouses;
    }
}
