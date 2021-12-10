package jakubwiraszka.items;

import java.util.HashMap;
import java.util.Map;

public enum Usable implements Item {
    HEALTH_POTION,
    DAMAGE_POTION,
    POWER_POTION,
    AGILITY_POTION;

    public HashMap<String, Integer> getContent() {
        return switch (this) {
            case HEALTH_POTION -> new HashMap<>(Map.of("Health", 5));
            case DAMAGE_POTION -> new HashMap<>(Map.of("Damage", 5));
            case POWER_POTION -> new HashMap<>(Map.of("Power", 2));
            case AGILITY_POTION -> new HashMap<>(Map.of("Agility", 2));
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
}
