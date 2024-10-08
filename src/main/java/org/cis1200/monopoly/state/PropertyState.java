package org.cis1200.monopoly.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyState {
    @JsonProperty
    String name;
    @JsonProperty
    int ownerId;
    @JsonProperty
    boolean mortgaged;
    @JsonProperty
    int numHouses;

    @JsonCreator
    public PropertyState() {
        name = null;
        ownerId = -1;
        mortgaged = false;
        numHouses = -1;
    }

    public PropertyState(String name, int ownerId, boolean mortgaged, int numHouses) {
        this.name = name;
        this.ownerId = ownerId;
        this.mortgaged = mortgaged;
        this.numHouses = numHouses;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public int getNumHouses() {
        return numHouses;
    }
}
