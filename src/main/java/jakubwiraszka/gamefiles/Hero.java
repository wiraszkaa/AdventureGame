package jakubwiraszka.gamefiles;

import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Usable;
import jakubwiraszka.items.Weapon;
import jakubwiraszka.observable.Listener;

import java.util.ArrayList;

public class Hero extends Enemy {
    private double maxHealth;
    private final Level level;
    private ArrayList<Item> inventory;

    public Hero(String name, Statistics statistics) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealth();
        level = new Level(1);
        inventory = new ArrayList<>();

        inventory.add(Usable.DAMAGE_POTION);
    }

    public Hero(String name, Statistics statistics, int level) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealth();
        this.level = new Level(level);
    }

    public void equip(Item item) {
        if(item != null) {
            if (item.isWeapon()) {
                setEquippedWeapon((Weapon) item);
            } else if (item.isArmor()) {
                setEquippedArmor((Armor) item);
            }
            System.out.println(item.getName() + " equipped");
        }
    }

    public void throwAway(Item item) {
        if(item != null) {
            if (item.isArmor() && getEquippedArmor().equals(item)) {
                setEquippedArmor(Armor.CLOTHES);
            } else if (item.isWeapon() && getEquippedWeapon().equals(item)) {
                setEquippedWeapon(Weapon.HANDS);
            }
            inventory.remove(item);
        }
    }

    @Override
    public void use(ItemContent content) {
        super.use(content);
        if(content.getStatistic().equals("MaxHealth")) {
            changeHealth(content.getValue());
            addMaxHealth(content.getValue());
        }
    }

    @Override
    void notifyListeners() {
        if (!enemyListeners.isEmpty()) {
            for (Listener i : enemyListeners) {
                i.update(getStatistics().getHealth(), getMaxHealth(), getStatistics().getPower(), getStatistics().getAgility(), null);
            }
        }
    }

    @Override
    public String toString() {
        return (getName() + ": \n"
                + "Health: " + super.getStatistics().getHealth() + "/" + getMaxHealth() + " Power: " + getStatistics().getPower() + " Agility: " + getStatistics().getAgility());
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void addMaxHealth(int value) {
        this.maxHealth += value;
        notifyListeners();
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
}
