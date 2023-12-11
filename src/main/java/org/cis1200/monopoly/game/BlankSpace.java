package org.cis1200.monopoly.game;

import org.cis1200.monopoly.SpacePrompt;

import java.util.Objects;

public class BlankSpace implements Space {
    private final String name;

    public BlankSpace(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SpacePrompt landPlayer(Player p) {
        return SpacePrompt.NONE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlankSpace that = (BlankSpace) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BlankSpace{" +
                "name='" + name + '\'' +
                '}';
    }
}
