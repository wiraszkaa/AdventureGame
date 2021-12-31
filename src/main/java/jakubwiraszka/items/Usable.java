package jakubwiraszka.items;

import jakubwiraszka.gamefiles.ItemContent;
import jakubwiraszka.gamefiles.Treasure;

public enum Usable implements Item {
    HEALTH_POTION,
    DAMAGE_POTION,
    POWER_POTION,
    AGILITY_POTION;

    public ItemContent getContent() {
        return switch (this) {
            case HEALTH_POTION -> new ItemContent("Health", 5);
            case DAMAGE_POTION -> new ItemContent("Damage", 5);
            case POWER_POTION -> new ItemContent("Power", 2);
            case AGILITY_POTION -> new ItemContent("Agility", 2);
        };
    }

    public String getSourceImage() {
        return switch (this) {
            case HEALTH_POTION -> "HealthPotion.png";
            case DAMAGE_POTION -> "DamagePotion.png";
            case POWER_POTION -> "PowerPotion.png";
            case AGILITY_POTION -> "AgilityPotion.png";
        };
    }


    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getName() {
        return switch (this) {
            case HEALTH_POTION -> "Health Potion";
            case DAMAGE_POTION -> "Damage Potion";
            case POWER_POTION -> "Power Potion";
            case AGILITY_POTION -> "Agility Potion";
        };
    }

    @Override
    public boolean isEnemy() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public boolean isArmor() {
        return false;
    }

    @Override
    public boolean isWeapon() {
        return false;
    }

    @Override
    public boolean isUsable() {
        return true;
    }


}
