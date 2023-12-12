package org.cis1200.monopoly.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlayerState {
    @JsonProperty
    String name;
    @JsonProperty
    List<String> properties;
    @JsonProperty
    int money;
    @JsonProperty
    int location;

    @JsonCreator
    public PlayerState() {
        name = null;
        properties = null;
        money = -1;
        location = -1;
    }

    public PlayerState(String name, List<String> properties, int money, int location) {
        this.name = name;
        this.properties = properties;
        this.money = money;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public List<String> getProperties() {
        return properties;
    }

    public int getMoney() {
        return money;
    }

    public int getLocation() {
        return location;
    }
}
