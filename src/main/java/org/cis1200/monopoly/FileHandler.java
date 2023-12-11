package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileHandler {
    public static void saveState(Player currentPlayer, Board b, Player p1, Player p2) throws IOException {
        PlayerState ps1 = new PlayerState(p1.getName(),
                p1.properties.stream().map(PropertySpace::getName).toList(),
                p1.getMoney(),
                b.getPlayerLocation(p1));
        PlayerState ps2 = new PlayerState(p2.getName(),
                p2.properties.stream().map(PropertySpace::getName).toList(),
                p2.getMoney(),
                b.getPlayerLocation(p2));
        MonopolyState state = new MonopolyState(b.getFilePath(),
                ps1,
                ps2,
                b.getSpaces().stream().filter(s -> s instanceof PropertySpace).map(s -> {
                    PropertySpace p = (PropertySpace) s;
                    return new PropertyState(p.getName(), p.getOwner().getId(), p.isMortgaged(), p.getNumHouses());
                }).toList(),
                currentPlayer.getId()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("files/saveFile.json"), state);
    }

    private static class MonopolyState {
        @JsonProperty
        String boardFilePath;
        @JsonProperty
        PlayerState player1;
        @JsonProperty
        PlayerState player2;
        @JsonProperty
        List<PropertyState> propertyStates;
        @JsonProperty
        int currentPlayerId;

        public MonopolyState(String boardFilePath,
                             PlayerState player1,
                             PlayerState player2,
                             List<PropertyState> propertyStates,
                             int currentPlayerId) {
            this.boardFilePath = boardFilePath;
            this.player1 = player1;
            this.player2 = player2;
            this.propertyStates = propertyStates;
            this.currentPlayerId = currentPlayerId;
        }
    }

    private static class PlayerState {
        String name;
        List<String> properties;
        int money;
        int location;

        public PlayerState(String name, List<String> properties, int money, int location) {
            this.name = name;
            this.properties = properties;
            this.money = money;
            this.location = location;
        }
    }

    private static class PropertyState {
        String name;
        int ownerId;
        boolean isMortgaged;
        int numHouses;

        public PropertyState(String name, int ownerId, boolean isMortgaged, int numHouses) {
            this.name = name;
            this.ownerId = ownerId;
            this.isMortgaged = isMortgaged;
            this.numHouses = numHouses;
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
        return new Board(filepath, boardSpaces);
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