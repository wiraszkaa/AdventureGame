package jakubwiraszka.gamefiles;

import jakubwiraszka.items.Item;

import java.util.ArrayList;

public class Hero extends Enemy {
    private int maxHealth;
    private final Level level;
    private ArrayList<Item> inventory;

    public Hero(String name, Statistics statistics) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealthValue();
        level = new Level(1);
        inventory = new ArrayList<>();
    }

    public Hero(String name, Statistics statistics, int level) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealthValue();
        this.level = new Level(level);
    }

    @Override
    public String save() {
        return (super.save() + getMaxHealth() + "\n" +
                getLevel().getCurrentLevel() + "\n" +
                getLevel().getCurrentExperience() + "\n" +
                getLevel().getPointsToSpend().intValue());
    }

    @Override
    public String toString() {
        return (getName() + ": \n"
                + "Health: " + super.getStatistics().getHealthValue() + "/" + getMaxHealth() + " Power: " + getStatistics().getPowerValue() + " Agility: " + getStatistics().getAgilityValue());
    }

    public String statsToString() {
        return ("Health: " + super.getStatistics().getHealthValue() + "/" + getMaxHealth() + " Power: " + getStatistics().getPowerValue() + " Agility: " + getStatistics().getAgilityValue());
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void addMaxHealth(int value) {
        this.maxHealth += value;
    }

    public Level getLevel() {
        return level;
    }

    public void setName(String name) {
        super.name = name;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<Item> inventory) {
        this.inventory = inventory;
    }
}
