package jakubwiraszka.gamefiles;

import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Usable;
import jakubwiraszka.items.Weapon;
import jakubwiraszka.observable.EnemyListener;

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
        if(item.isWeapon()) {
            setEquippedWeapon((Weapon) item);
        } else if(item.isArmor()) {
            setEquippedArmor((Armor) item);
        }
        System.out.println(item.getName() + " equipped");
    }

    public void throwAway(Item item) {
        if(item.isArmor() && getEquippedArmor().equals(item)) {
            setEquippedArmor(Armor.CLOTHES);
        } else if(item.isWeapon() && getEquippedWeapon().equals(item)) {
            setEquippedWeapon(Weapon.HANDS);
        }
        inventory.remove(item);
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
            for (EnemyListener i : enemyListeners) {
                i.update(getStatistics().getHealth(), getMaxHealth(), getStatistics().getPower(), getStatistics().getAgility());
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
