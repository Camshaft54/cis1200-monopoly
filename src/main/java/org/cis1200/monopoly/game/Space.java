package org.cis1200.monopoly.game;

import org.cis1200.monopoly.SpacePrompt;

public interface Space {
    String getName();
    SpacePrompt landPlayer(Player p);
}
