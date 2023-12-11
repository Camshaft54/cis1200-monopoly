package org.cis1200.monopoly.state;

public class PropertyState {
    String name;
    int ownerId;
    boolean isMortgaged;
    int numHouses;

    public PropertyState(String name, int ownerId, boolean isMortgaged, int numHouses) {
        this.name = name;
        this.ownerId = ownerId;
        this.isMortgaged = isMortgaged;
        this.numHouses = numHouses;
    }
}
