package org.cis1200.monopoly.state;

import java.util.List;

public class PlayerState {
    String name;
    List<String> properties;
    int money;
    int location;

    public PlayerState(String name, List<String> properties, int money, int location) {
        this.name = name;
        this.properties = properties;
        this.money = money;
        this.location = location;
    }
}
