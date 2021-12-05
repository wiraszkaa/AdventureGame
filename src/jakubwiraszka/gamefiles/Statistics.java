package jakubwiraszka.gamefiles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Statistics {
    private final IntegerProperty health;
    private int power;
    private int agility;

    public Statistics(int health, int power, int agility) {
        this.health = new SimpleIntegerProperty(health);
        this.power = power;
        this.agility = agility;
    }

    public int getHealthValue() {
        return health.intValue();
    }

    public SimpleIntegerProperty getHealth() {
        return (SimpleIntegerProperty) health;
    }

    public void setHealth(int health) {
        this.health.set(health);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    @Override
    public String toString() {
        return "Health = " + getHealthValue() + ", Power = " + getPower() + ", Agility = " + getAgility();
    }
}

