package org.cis1200.monopoly;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GameTest {
    @Test
    public void testPropertyAndGroupBasic() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        PropertySpace property2 = new PropertySpace("Baltic Avenue", purple, 120, 60, new int[]{4, 20, 60, 180, 350, 450});
        purple.registerProperty(property1);
        purple.registerProperty(property2);
        assertEquals(new Color(128, 0, 128), purple.getColor());
        assertEquals(50, purple.getHousePrice());
        assertEquals(100, purple.getHotelPrice());
        assertEquals(2, purple.getPropertySpaces().size());
        assertTrue(purple.getPropertySpaces().contains(property1));
        assertTrue(purple.getPropertySpaces().contains(property2));
        assertEquals(purple, property1.getGroup());
        assertEquals("Mediterranean Avenue", property1.getName());
        assertEquals(60, property1.getPurchasePrice());
        assertEquals(30, property1.getMortgageValue());
        assertEquals(0, property1.getNumHouses());
        assertNull(property1.getOwner());
    }

    @Test
    public void testPlayer() {
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(1, player1.getId());
        assertEquals(1500, player1.getMoney());
        assertEquals("Player 1", player1.getName());
        assertEquals(1600, player1.changeMoney(100));
        assertEquals(1600, player1.getMoney());
    }

    @Test
    public void testBuyProperty() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        purple.registerProperty(property1);
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(PropertyResponse.OK, property1.buyProperty(player1));
        assertEquals(1440, player1.getMoney(), "Player ");
        assertEquals(property1.getOwner(), player1);
        assertEquals(PropertyResponse.ALREADY_OWNED, property1.buyProperty(player1));
        assertEquals(1440, player1.getMoney());
        assertEquals(property1.getOwner(), player1);
        property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        player1 = new Player("Player 1", 1, 0);
        assertEquals(PropertyResponse.INSUFFICIENT_FUNDS, property1.buyProperty(player1));
    }

    @Test
    public void testBuyHouse() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        PropertySpace property2 = new PropertySpace("Baltic Avenue", purple, 120, 60, new int[]{4, 20, 60, 180, 350, 450});
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(PropertyResponse.NO_OWNER, property1.buyHouse(player1));
        property1.buyProperty(player1);
        assertEquals(PropertyResponse.NOT_GROUP_OWNER, property1.buyHouse(player1));
        property2.buyProperty(player1);
        assertEquals(0, player1.changeMoney(-1320));
        assertEquals(PropertyResponse.INSUFFICIENT_FUNDS, property1.buyHouse(player1));
        player1.changeMoney(1500);
        assertEquals(PropertyResponse.OK, property1.buyHouse(player1)); // 1, 0
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property1.buyHouse(player1)); // 1, 0
        assertEquals(PropertyResponse.OK, property2.buyHouse(player1)); // 1, 1
        assertEquals(PropertyResponse.OK, property2.buyHouse(player1)); // 1, 2
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property2.buyHouse(player1)); // 1, 2
        assertEquals(1350, player1.getMoney());
        assertEquals(1, property1.getNumHouses());
        assertEquals(2, property2.getNumHouses());
        assertEquals(PropertyResponse.OK, property1.buyHouse(player1)); // 2, 2
        assertEquals(PropertyResponse.OK, property1.buyHouse(player1)); // 3, 2
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property1.buyHouse(player1)); // 3, 2
        assertEquals(PropertyResponse.OK, property2.buyHouse(player1)); // 3, 3
        assertEquals(PropertyResponse.OK, property2.buyHouse(player1)); // 3, 4
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property2.buyHouse(player1)); // 3, 4
        assertEquals(PropertyResponse.OK, property1.buyHouse(player1)); // 4, 4
        assertEquals(PropertyResponse.OK, property1.buyHouse(player1)); // 5, 4
        assertEquals(PropertyResponse.MAX_HOUSES, property1.buyHouse(player1)); // 5, 4
        assertEquals(PropertyResponse.OK, property2.buyHouse(player1)); // 5, 5
        assertEquals(PropertyResponse.MAX_HOUSES, property2.buyHouse(player1)); // 5, 5
        assertEquals(900, player1.getMoney());
        assertEquals(5, property1.getNumHouses());
        assertEquals(5, property2.getNumHouses());
    }

    @Test
    public void testBuyThenSellHouses() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        PropertySpace property2 = new PropertySpace("Baltic Avenue", purple, 120, 60, new int[]{4, 20, 60, 180, 350, 450});
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(PropertyResponse.NO_OWNER, property1.sellHouse(player1));
        property1.buyProperty(player1);
        assertEquals(PropertyResponse.NOT_GROUP_OWNER, property1.sellHouse(player1));
        property2.buyProperty(player1);
        assertEquals(1500, player1.changeMoney(180));
        assertEquals(PropertyResponse.NO_HOUSES, property1.sellHouse(player1));
        property1.buyHouse(player1);
        property2.buyHouse(player1);
        property1.buyHouse(player1);
        property2.buyHouse(player1);
        property1.buyHouse(player1);
        property2.buyHouse(player1);
        property1.buyHouse(player1);
        property2.buyHouse(player1);
        property1.buyHouse(player1);
        property2.buyHouse(player1);
        assertEquals(900, player1.getMoney());
        assertEquals(5, property1.getNumHouses());
        assertEquals(5, property2.getNumHouses());

        assertEquals(PropertyResponse.OK, property1.sellHouse(player1)); // 4, 5
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property1.sellHouse(player1)); // 4, 5
        assertEquals(PropertyResponse.OK, property2.sellHouse(player1)); // 4, 4
        assertEquals(PropertyResponse.OK, property2.sellHouse(player1)); // 4, 3
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property2.sellHouse(player1)); // 4, 3
        assertEquals(1025, player1.getMoney());
        assertEquals(4, property1.getNumHouses());
        assertEquals(3, property2.getNumHouses());
        assertEquals(PropertyResponse.OK, property1.sellHouse(player1)); // 3, 3
        assertEquals(PropertyResponse.OK, property1.sellHouse(player1)); // 2, 3
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property1.sellHouse(player1)); // 2, 3
        assertEquals(PropertyResponse.OK, property2.sellHouse(player1)); // 2, 2
        assertEquals(PropertyResponse.OK, property2.sellHouse(player1)); // 2, 1
        assertEquals(PropertyResponse.UNBALANCED_GROUP, property2.sellHouse(player1)); // 2, 1
        assertEquals(PropertyResponse.OK, property1.sellHouse(player1)); // 1, 1
        assertEquals(PropertyResponse.OK, property1.sellHouse(player1)); // 0, 1
        assertEquals(PropertyResponse.NO_HOUSES, property1.sellHouse(player1)); // 0, 1
        assertEquals(PropertyResponse.OK, property2.sellHouse(player1)); // 0, 0
        assertEquals(PropertyResponse.NO_HOUSES, property2.sellHouse(player1)); // 0, 0
        assertEquals(1200, player1.getMoney());
        assertEquals(0, property1.getNumHouses());
        assertEquals(0, property2.getNumHouses());
    }

    @Test
    public void testMortgageProperty() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(PropertyResponse.NO_OWNER, property1.mortgageProperty(player1));
        property1.buyProperty(player1);
        property1.setNumHouses(5);
        assertEquals(PropertyResponse.OK, property1.mortgageProperty(player1));
        assertEquals(1620, player1.getMoney());
        assertEquals(PropertyResponse.ALREADY_MORTGAGED, property1.mortgageProperty(player1));
        assertEquals(1620, player1.getMoney());
        assertTrue(property1.isMortgaged());
        assertEquals(0, property1.getNumHouses());
    }

    @Test
    public void testUnmortgageProperty() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 100);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(PropertyResponse.NO_OWNER, property1.unmortgageProperty(player1));
        property1.buyProperty(player1);
        assertEquals(PropertyResponse.NOT_MORTGAGED, property1.unmortgageProperty(player1));
        property1.mortgageProperty(player1);
        player1.changeMoney(-1470);
        assertEquals(0, player1.getMoney());
        assertEquals(PropertyResponse.INSUFFICIENT_FUNDS, property1.unmortgageProperty(player1));
        assertTrue(property1.isMortgaged());
        player1.changeMoney(33);
        assertEquals(PropertyResponse.OK, property1.unmortgageProperty(player1));
        assertEquals(0, player1.getMoney());
        assertFalse(property1.isMortgaged());
    }

    @Test
    public void testMovePlayerEmptySpace() {
        Board b = setupBoard();
        Player player1 = new Player("Player 1", 1, 1500);
        b.movePlayer(player1, 3);
        assertEquals(1500, player1.getMoney());
    }

    @Test
    public void testMovePlayerSpace() {
        Board b = setupBoard();
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(0, b.getPlayerLocation(player1));
        assertEquals(SpacePrompt.NONE, b.movePlayer(player1, 3));
        assertEquals(1500, player1.getMoney());
        assertEquals(3, b.getPlayerLocation(player1));
    }

    @Test
    public void testMovePlayerGo() {
        Board b = setupBoard();
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(0, b.getPlayerLocation(player1));
        assertEquals(SpacePrompt.NONE, b.movePlayer(player1, 4));
        assertEquals(1700, player1.getMoney(), "Collect money upon landing on go");
        assertEquals(0, b.getPlayerLocation(player1));
        b.movePlayer(player1, 7);
        assertEquals(1900, player1.getMoney(), "Collect money upon passing go");
        assertEquals(3, b.getPlayerLocation(player1));
    }

    @Test
    public void testMovePlayerProperty() {
        Board b = setupBoard();
        Player player1 = new Player("Player 1", 1, 1500);
        Player player2 = new Player("Player 2", 2, 1500);
        assertEquals(0, b.getPlayerLocation(player1));
        assertEquals(SpacePrompt.BUY_PROPERTY, b.movePlayer(player1, 1));
        assertEquals(1500, player1.getMoney());
        assertEquals(1, b.getPlayerLocation(player1));
        ((PropertySpace) b.getPlayerSpace(player1)).buyProperty(player1);
        assertEquals(SpacePrompt.PAID_RENT, b.movePlayer(player2, 1));
        assertEquals(1498, player2.getMoney());
        assertEquals(1, b.getPlayerLocation(player2));
        ((PropertySpace) b.getPlayerSpace(player1)).setNumHouses(5);
        b.movePlayer(player2, 4);
        assertEquals(1448, player2.getMoney()); // 1500 - 2 (rent 1) - 250 (rent 2) + 200 (go) = 1448
        assertEquals(1, b.getPlayerLocation(player2));
        ((PropertySpace) b.getPlayerSpace(player1)).setNumHouses(0);
        assertEquals(PropertyResponse.OK, ((PropertySpace) b.getPlayerSpace(player1)).mortgageProperty(player1));
        b.movePlayer(player2, 4);
        assertEquals(1648, player2.getMoney());
    }

    @Test
    public void testMovePlayerRandom() {
        // Construct 13-space board
        Board b = new Board(List.of(
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1"),
                new BlankSpace("1")));
        Player player1 = new Player("Player 1", 1, 1500);
        assertEquals(0, b.getPlayerLocation(player1));
        assertEquals(SpacePrompt.NONE, b.movePlayer(player1));
        assertTrue(b.getPlayerLocation(player1) > 0 && b.getPlayerLocation(player1) < 13);
    }

    @Test
    public void testMovePlayerBad() {
        Board b = new Board(List.of(
                new BlankSpace("1"),
                new BlankSpace("1")));
        Player player1 = new Player("Player 3", 0, 1500);
        assertThrows(IllegalArgumentException.class, () -> b.movePlayer(player1));
    }

    private Board setupBoard() {
        PropertyGroup purple = new PropertyGroup(new Color(128, 0, 128), 50, 50);
        PropertySpace property1 = new PropertySpace("Mediterranean Avenue", purple, 60, 30, new int[]{2, 10, 30, 90, 160, 250});
        PropertySpace property2 = new PropertySpace("Baltic Avenue", purple, 120, 60, new int[]{4, 20, 60, 180, 350, 450});
        List<Space> spaces = new LinkedList<>();
        spaces.add(new BlankSpace("Go"));
        spaces.add(property1);
        spaces.add(property2);
        spaces.add(new BlankSpace("Test"));
        return new Board(spaces);
    }

    @Test
    public void testLoadConfig() throws IOException {
        Board b = SaveMonopoly.loadBoard("files/classicBoard.json");

    }
}
