package org.cis1200.monopoly;

import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyGroup {
    private final Color color;
    private final int housePrice;
    private final int hotelPrice;
    private final Set<PropertySpace> propertySpaces = new HashSet<>();

    public PropertyGroup(Color color, int housePrice, int hotelPrice) {
        this.color = color;
        this.housePrice = housePrice;
        this.hotelPrice = hotelPrice;
    }

    public void registerProperty(PropertySpace p) {
        propertySpaces.add(p);
    }

    public boolean canAddHouse(PropertySpace p) {
        if (!p.getGroup().equals(this)) {
            throw new IllegalArgumentException("Can't check property not in this group");
        }
        int pNum = p.getNumHouses();
        // If o == p: good
        // If o - p > 0: good
        // If o - p > 1: bad
        // If o - p < 0: bad
        for (PropertySpace o : propertySpaces) {
            if (!p.equals(o)) {
                int oNum = o.getNumHouses();
                if (oNum - pNum >= 0 && oNum - pNum > 1) {
                    // When a property has more houses than it should print error to console.
                    System.out.println("Invalid number of houses on a property! Reset all houses in group and refunded money.");
                    return false;
                } else if (oNum - pNum < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean ownsGroup(Player p) {
        return propertySpaces.stream().allMatch(property -> property.getOwner() != null && property.getOwner().equals(p));
    }

    public boolean canRemoveHouse(PropertySpace p) {
        if (!p.getGroup().equals(this)) {
            throw new IllegalArgumentException("Can't check property not in this group");
        }
        int pNum = p.getNumHouses();
        // If o == p: good
        // If o - p > 0: bad
        // If o - p < 0: good
        // If o - p < -1: bad
        for (PropertySpace o : propertySpaces) {
            if (!p.equals(o)) {
                int oNum = o.getNumHouses();
                if (oNum - pNum <= 0 && oNum - pNum < -1) {
                    // When a property has fewer houses than it should print error to console.
                    System.out.println("Invalid number of houses on a property! Reset all houses in group and refunded money.");
                } else if (oNum - pNum > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Color getColor() {
        return color;
    }

    public int getHousePrice() {
        return housePrice;
    }

    public int getHotelPrice() {
        return hotelPrice;
    }

    public Set<PropertySpace> getPropertySpaces() {
        return propertySpaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyGroup that = (PropertyGroup) o;
        return color.equals(that.color) &&
                housePrice == that.housePrice &&
                hotelPrice == that.hotelPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return "PropertyGroup{" +
                "color=" + color +
                ", housePrice=" + housePrice +
                ", hotelPrice=" + hotelPrice +
                ", propertySpaces=" + propertySpaces.stream().map(PropertySpace::getName).collect(Collectors.joining(", ", "[", "]")) +
                '}';
    }
}
