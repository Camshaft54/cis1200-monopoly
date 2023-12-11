package org.cis1200.monopoly;

import java.util.Arrays;
import java.util.Objects;

public class PropertySpace implements Space {
    private final String name;
    private final PropertyGroup group;
    private final int purchasePrice;
    private final int mortgageValue;
    private final int[] rentCosts;
    private Player owner = null;
    private boolean isMortgaged = false;
    private int numHouses = 0;

    public PropertySpace(String name, PropertyGroup group, int purchasePrice, int mortgageValue, int[] rentCosts) {
        this.name = name;
        this.group = group;
        group.registerProperty(this);
        this.purchasePrice = purchasePrice;
        this.mortgageValue = mortgageValue;
        this.rentCosts = Arrays.copyOf(rentCosts, rentCosts.length);
    }

    public PropertyResponse buyProperty(Player p) {
        if (owner != null) {
            return PropertyResponse.ALREADY_OWNED;
        } else if (p.getMoney() < purchasePrice) {
            return PropertyResponse.INSUFFICIENT_FUNDS;
        }

        owner = p;
        owner.properties.add(this);
        p.changeMoney(-purchasePrice);
        return PropertyResponse.OK;
    }

    public PropertyResponse mortgageProperty(Player p) {
        if (owner == null) {
            return PropertyResponse.NO_OWNER;
        } else if (!owner.equals(p)) {
            return PropertyResponse.NOT_OWNER;
        } else if (isMortgaged) {
            return PropertyResponse.ALREADY_MORTGAGED;
        }

        while (numHouses > 0) {
            PropertyResponse pr = sellHouse(p);
            if (!pr.equals(PropertyResponse.OK)) {
                return pr;
            }
        }
        p.changeMoney(mortgageValue);
        isMortgaged = true;
        return PropertyResponse.OK;
    }

    public PropertyResponse unmortgageProperty(Player p) {
        if (owner == null) {
            return PropertyResponse.NO_OWNER;
        } else if (!owner.equals(p)) {
            return PropertyResponse.NOT_OWNER;
        } else if (p.getMoney() < mortgageValue * 1.1) { // 10% interest on all mortgages
            return PropertyResponse.INSUFFICIENT_FUNDS;
        } else if (!isMortgaged) {
            return PropertyResponse.NOT_MORTGAGED;
        } else {
            if (numHouses != 0) {
                System.out.println("Houses found on a mortgaged property (" + name + ")!");
            }
            p.changeMoney((int) (-1.1 * mortgageValue));
            isMortgaged = false;
            return PropertyResponse.OK;
        }
    }

    public PropertyResponse buyHouse(Player p) {
        if (owner == null) {
            return PropertyResponse.NO_OWNER;
        } else if (!group.ownsGroup(p)) {
            return PropertyResponse.NOT_GROUP_OWNER;
        } else if (numHouses == 5) {
            return PropertyResponse.MAX_HOUSES;
        } else if (!group.canAddHouse(this)) {
            return PropertyResponse.UNBALANCED_GROUP;
        }

        if (numHouses == 4) {
            if (p.getMoney() < group.getHotelPrice()) {
                return PropertyResponse.INSUFFICIENT_FUNDS;
            }
            p.changeMoney(-group.getHotelPrice());
        } else {
            if (p.getMoney() < group.getHousePrice()) {
                return PropertyResponse.INSUFFICIENT_FUNDS;
            }
            p.changeMoney(-group.getHousePrice());
        }
        numHouses++;
        return PropertyResponse.OK;
    }

    public PropertyResponse sellHouse(Player p) {
        if (owner == null) {
            return PropertyResponse.NO_OWNER;
        } else if (!group.ownsGroup(p)) {
            return PropertyResponse.NOT_GROUP_OWNER;
        } else if (numHouses == 0) {
            return PropertyResponse.NO_HOUSES;
        } else if (!group.canRemoveHouse(this)) {
            return PropertyResponse.UNBALANCED_GROUP;
        }

        if (numHouses == 5) {
            p.changeMoney(group.getHotelPrice() / 2);
        } else {
            p.changeMoney(group.getHousePrice() / 2);
        }
        numHouses--;
        return PropertyResponse.OK;
    }

    @Override
    public SpacePrompt landPlayer(Player p) {
        if (owner == null) {
            return SpacePrompt.BUY_PROPERTY;
        } else if (!p.equals(owner) && !isMortgaged) {
            // If there are no houses/hotels and the property is part of a group,
            // then charge 2x rent.
            if (numHouses == 0 && group.ownsGroup(owner)) {
                p.changeMoney(-2 * rentCosts[numHouses]);
            } else {
                p.changeMoney(-rentCosts[numHouses]);
            }
            return SpacePrompt.PAID_RENT;
        } else {
            return SpacePrompt.NONE;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public Player getOwner() {
        return owner;
    }

    public int getNumHouses() {
        return numHouses;
    }

    public PropertyGroup getGroup() {
        return group;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public int getMortgageValue() {
        return mortgageValue;
    }

    public int[] getRentCosts() {
        return rentCosts;
    }

    public boolean isMortgaged() {
        return isMortgaged;
    }

    public void setNumHouses(int numHouses) {
        if (numHouses >= 0 && numHouses <= 5) {
            this.numHouses = numHouses;
        } else {
            throw new IllegalArgumentException("Given invalid number of houses to set");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpace that = (PropertySpace) o;
        return purchasePrice == that.purchasePrice &&
                mortgageValue == that.mortgageValue &&
                isMortgaged == that.isMortgaged &&
                numHouses == that.numHouses &&
                Objects.equals(name, that.name) &&
                Objects.equals(group.getColor(), that.group.getColor()) &&
                Arrays.equals(rentCosts, that.rentCosts) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name); // TODO why does this not work when the other fields are hashed?
        result = 31 * result;
        return result;
    }

    @Override
    public String toString() {
        return "PropertySpace{" +
                "name='" + name + '\'' +
                ", group=" + group +
                ", purchasePrice=" + purchasePrice +
                ", mortgageValue=" + mortgageValue +
                ", rentCosts=" + Arrays.toString(rentCosts) +
                ", owner=" + owner +
                ", isMortgaged=" + isMortgaged +
                ", numHouses=" + numHouses +
                '}';
    }
}
