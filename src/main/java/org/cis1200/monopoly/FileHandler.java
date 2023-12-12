package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cis1200.monopoly.game.*;
import org.cis1200.monopoly.state.*;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FileHandler {
    public static MonopolyState toState(Player currentPlayer, Board b, Player p1, Player p2) {
        PlayerState ps1 = null;
        PlayerState ps2 = null;
        if (p1 != null) {
            ps1 = new PlayerState(p1.getName(),
                    p1.getProperties().stream().map(PropertySpace::getName).toList(),
                    p1.getMoney(),
                    b.getPlayerLocation(p1));
        }
        if (p2 != null) {
            ps2 = new PlayerState(p2.getName(),
                    p2.getProperties().stream().map(PropertySpace::getName).toList(),
                    p2.getMoney(),
                    b.getPlayerLocation(p2));
        }
        return new MonopolyState(
                ps1,
                ps2,
                b.getSpaces().stream().filter(s -> s instanceof PropertySpace).map(s -> {
                    PropertySpace p = (PropertySpace) s;
                    return new PropertyState(p.getName(), (p.getOwner() == null) ? -1 : p.getOwner().getId(), p.isMortgaged(), p.getNumHouses());
                }).toList(),
                (currentPlayer == null) ? -1 : currentPlayer.getId()
        );
    }

    public static void saveState(Player currentPlayer, Board b, Player p1, Player p2) throws IOException {
        MonopolyState state = toState(currentPlayer, b, p1, p2);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("files/save.json"), state);
    }

    public static RecoveredState recoverState(Board board) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MonopolyState state = objectMapper.readValue(new File("files/save.json"), MonopolyState.class);
        Map<String, PropertyState> propertyMap = state.getPropertyStates().stream()
                .collect(Collectors.toMap(PropertyState::getName, p -> p));
        Player p1 = new Player(state.getPlayer1().getName(), 1, state.getPlayer1().getMoney());
        Player p2 = new Player(state.getPlayer2().getName(), 2, state.getPlayer2().getMoney());

        Map<String, PropertySpace> properties = new TreeMap<>();

        List<Space> spaces = board.getSpaces().stream().map(space -> {
            if (space instanceof PropertySpace ps) {
                int playerId = propertyMap.get(ps.getName()).getOwnerId();
                if (playerId == 1) {
                    ps.setOwner(p1);
                } else if (playerId == 2) {
                    ps.setOwner(p2);
                }
                ps.setMortgaged(propertyMap.get(ps.getName()).isMortgaged());
                ps.setNumHouses(propertyMap.get(ps.getName()).getNumHouses());
                properties.put(ps.getName(), ps);
                return ps;
            } else {
                return space;
            }
        }).toList();
        p1.addAllProperties(properties.values().stream().filter(p -> p.getOwner().equals(p1)).toList());
        p2.addAllProperties(properties.values().stream().filter(p -> p.getOwner().equals(p2)).toList());
        Board newBoard = new Board(spaces, properties, new int[]{state.getPlayer1().getLocation(), state.getPlayer1().getLocation()});
        Player currentPlayer = (state.getCurrentPlayerId() == 1) ? p1 : p2;
        return new RecoveredState(newBoard, p1, p2, currentPlayer);
    }

    public static class RecoveredState {
        Board board;
        Player player1;
        Player player2;
        Player currentPlayer;

        public RecoveredState(Board board, Player player1, Player player2, Player currentPlayer) {
            this.board = board;
            this.player1 = player1;
            this.player2 = player2;
            this.currentPlayer = currentPlayer;
        }
    }

    public static Board loadBoard(String filepath) throws IOException {
        String json = new BufferedReader(new FileReader(filepath)).lines().collect(Collectors.joining());
        ObjectMapper mapper = new ObjectMapper();
        MonopolyConfig config = mapper.readValue(json, MonopolyConfig.class);
        Map<String, PropertyGroup> propertyGroups = config.propertyGroups.stream().collect(
                Collectors.toMap(c -> c.name, c ->
                        new PropertyGroup(new Color(c.color[0], c.color[1], c.color[2]), c.houseCost, c.hotelCost)
                )
        );
        Map<String, PropertySpace> propertySpaces = config.properties.stream().collect(
                Collectors.toMap(
                        c -> c.name,
                        c -> new PropertySpace(c.name, propertyGroups.get(c.group), c.price, c.mortgage, c.rents)
                )
        );
        List<Space> boardSpaces = config.spaces.stream().map(s -> switch (s.type) {
            case "blank" -> new BlankSpace(s.name);
            case "property" -> propertySpaces.get(s.name);
            default -> throw new IllegalStateException("Unexpected value: " + s.type);
        }).toList();
        return new Board(boardSpaces, propertySpaces);
    }

    private static class MonopolyConfig {
        @JsonProperty
        List<PropertyGroupConfig> propertyGroups;
        @JsonProperty
        List<PropertyConfig> properties;
        @JsonProperty
        List<SpaceConfig> spaces;
    }

    private static class PropertyGroupConfig {
        @JsonProperty
        String name;
        @JsonProperty
        int[] color;
        @JsonProperty
        int houseCost;
        @JsonProperty
        int hotelCost;
    }

    private static class PropertyConfig {
        @JsonProperty
        String name;
        @JsonProperty
        String group;
        @JsonProperty
        int[] rents;
        @JsonProperty
        int price;
        @JsonProperty
        int mortgage;
    }

    private static class SpaceConfig {
        @JsonProperty
        String type;
        @JsonProperty
        String name;
    }
}