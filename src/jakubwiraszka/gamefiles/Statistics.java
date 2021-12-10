package jakubwiraszka.gamefiles;

import javafx.beans.property.*;

public class Statistics {
    private final DoubleProperty health;
    private final IntegerProperty power;
    private final IntegerProperty agility;

    public Statistics(double health, int power, int agility) {
        this.health = new SimpleDoubleProperty(health);
        this.power = new SimpleIntegerProperty(power);
        this.agility = new SimpleIntegerProperty(agility);
    }

    public int getHealthValue() {
        return health.intValue();
    }

    public SimpleDoubleProperty getHealth() {
        return (SimpleDoubleProperty) health;
    }

    public void setHealth(double health) {
        this.health.set(health);
    }

    public int getPowerValue() {
        return power.intValue();
    }

    public SimpleIntegerProperty getPower() {
        return (SimpleIntegerProperty) power;
    }

    public void setPower(int power) {
        this.power.set(power);
    }

    public int getAgilityValue() {
        return agility.intValue();
    }

    public SimpleIntegerProperty getAgility() {
        return (SimpleIntegerProperty) agility;
    }

    public void setAgility(int agility) {
        this.agility.set(agility);
    }

    @Override
    public String toString() {
        return "Health = " + getHealthValue() + ", Power = " + getPowerValue() + ", Agility = " + getAgilityValue();
    }
}

