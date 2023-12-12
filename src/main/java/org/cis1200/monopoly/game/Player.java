package org.cis1200.monopoly.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Player {
    private final String name;
    private final int id;
    private int money;
    private final List<PropertySpace> properties;

    public Player(String name, int id, int money) {
        this.name = name;
        this.id = id;
        this.money = money;
        properties = new LinkedList<>();
    }

    public Player(String name, int id, int money, List<PropertySpace> properties) {
        this.name = name;
        this.id = id;
        this.money = money;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public int changeMoney(int amount) {
        money += amount;
        return money;
    }

    public List<PropertySpace> getProperties() {
        return properties;
    }

    public void addAllProperties(List<PropertySpace> properties) {
        this.properties.addAll(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
