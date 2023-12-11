package org.cis1200.monopoly;

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
}
