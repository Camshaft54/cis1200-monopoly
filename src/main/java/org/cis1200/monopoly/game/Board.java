package org.cis1200.monopoly.game;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.cis1200.monopoly.SpacePrompt;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Board {
    private static final int COLLECT_GO_AMOUNT = 200;
    private final List<Space> board = new LinkedList<>();
    private final Random rand;
    private int[] locations = new int[2];
    private final Map<String, PropertySpace> properties;

    public Board(List<Space> spaces, Map<String, PropertySpace> properties) {
        this.properties = properties;
        board.addAll(spaces);
        rand = new Random();
    }

    public Board(List<Space> spaces, Map<String, PropertySpace> properties, int[] locations) {
        this(spaces, properties);
        this.locations = locations;
    }

    /**
     * Generates dice roll and moves player that number of spaces.
     *
     * @return player's balance after turn
     */
    public SpacePrompt movePlayer(Player p) {
        int diceRoll = rand.nextInt(1, 7) + rand.nextInt(1, 7);
        return movePlayer(p, diceRoll);
    }

    public SpacePrompt movePlayer(Player p, int spacesToMove) {
        // TODO Jail? Doubles? Add method to space canMoveFrom()
        if (p.getId() != 1 && p.getId() != 2) {
            throw new IllegalArgumentException("Player is not Player 1 or 2");
        }
        // If we loop around to the end of the board, we must have landed on go or passed it, so player gets paid
        if (locations[p.getId() - 1] + spacesToMove > board.size() - 1) {
            p.changeMoney(COLLECT_GO_AMOUNT);
        }
        locations[p.getId() - 1] = (locations[p.getId() - 1] + spacesToMove) % board.size();
        return board.get(locations[p.getId() - 1]).landPlayer(p);
    }

    public int getPlayerLocation(Player p) {
        if (p.getId() == 1 || p.getId() == 2) {
            return locations[p.getId() - 1];
        } else {
            throw new IllegalArgumentException("Player is not Player 1 or 2");
        }
    }

    public Space getPlayerSpace(Player p) {
        if (p.getId() == 1 || p.getId() == 2) {
            return board.get(locations[p.getId() - 1]);
        } else {
            throw new IllegalArgumentException("Player is not Player 1 or 2");
        }
    }

    public PropertySpace getProperty(String name) {
        return properties.get(name);
    }

    public List<Space> getSpaces() {
        return board;
    }

    public List<String> getSpaceNames() {
        List<String> names = new LinkedList<>();
        for (Space space : board) {
            System.out.println(space.getName());
            names.add(space.getName());
        }
        return names;
    }

    public List<Color> getSpaceColors() {
        return board.stream()
                .map(s -> (s instanceof PropertySpace) ? ((PropertySpace) s).getGroup().getColor() : null).toList();
    }
}
