package org.cis1200.monopoly;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Board {
    private static final int COLLECT_GO_AMOUNT = 200;
    private final String filePath;
    private final List<Space> board = new LinkedList<>();
    private final Random rand;
    private final int[] locations = new int[2];

    public Board(String filepath, List<Space> spaces) {
        this.filePath = filepath;
        board.addAll(spaces);
        rand = new Random();
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

    public List<Space> getSpaces() {
        return board;
    }

    public String getFilePath() {
        return filePath;
    }
}
