package jakubwiraszka.gamefiles;

import javafx.beans.property.*;

public class Statistics {
    private final DoubleProperty health;
    private int power;
    private int agility;

    public Statistics(double health, int power, int agility) {
        this.health = new SimpleDoubleProperty(health);
        this.power = power;
        this.agility = agility;
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

