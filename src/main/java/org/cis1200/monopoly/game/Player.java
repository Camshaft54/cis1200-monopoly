package org.cis1200.monopoly.game;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
    private final String name;
    private final int id;
    private int money;
    private final ArrayList<PropertySpace> properties = new ArrayList<>(30);

    public Player(String name, int id, int money) {
        this.name = name;
        this.id = id;
        this.money = money;
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

    public ArrayList<PropertySpace> getProperties() {
        return properties;
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
