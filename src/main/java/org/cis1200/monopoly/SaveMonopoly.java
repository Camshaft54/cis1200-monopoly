package org.cis1200.monopoly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveMonopoly {
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
        return new Board(boardSpaces);
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