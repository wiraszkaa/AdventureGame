package jakubwiraszka.fight;

import jakubwiraszka.gamefiles.*;
import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Usable;
import jakubwiraszka.items.Weapon;

import java.util.Random;

public class Randomize {

    private static final Random random = new Random();

    public static boolean randomize(double chance, int iterations) {
        int counter = 0;
        for (int i = 0; i < iterations; i++) {
            if (random.nextDouble() <= chance) {
                counter++;
            }
        }
        return counter > (iterations / 2);
    }

    public static Enemy randomEnemy(Hero hero, Difficulty difficulty, String name) {
        double health = (int) (hero.getMaxHealth() * 0.7 + random.nextInt((int) (hero.getMaxHealth() * 0.6)) + 1);
        int power = (int) ((double) hero.getStatistics().getPower() * 0.7 + random.nextInt((int) (hero.getStatistics().getPower() * 0.6)));
        int agility = (int) ((double) hero.getStatistics().getAgility() * 0.7 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.6)));
        switch (difficulty) {
            case EASY -> {
                health = (int) (hero.getMaxHealth() * 0.7 + random.nextInt((int) (hero.getMaxHealth() * 0.3)) + 1);
                power = (int) ((double) hero.getStatistics().getPower() * 0.7 + random.nextInt((int) (hero.getStatistics().getPower() * 0.3)));
                agility = (int) ((double) hero.getStatistics().getAgility() * 0.7 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.3)));
            }
            case HARD -> {
                health = (int) (hero.getMaxHealth() * 0.8 + random.nextInt((int) (hero.getMaxHealth() * 0.7)) + 1);
                power = (int) ((double) hero.getStatistics().getPower() * 0.8 + random.nextInt((int) (hero.getStatistics().getPower() * 0.7)));
                agility = (int) ((double) hero.getStatistics().getAgility() * 0.8 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.7)));
            }
        }
        return new Enemy(name, new Statistics(health, power, agility));
    }

    public static Treasure randomTreasure(Difficulty difficulty) {
        String statistic = switch (random.nextInt(3)) {
            case 1 -> "Power";
            case 2 -> "Agility";
            default -> "Health";
        };
        int value = switch (difficulty) {
            case EASY -> random.nextInt(2) + 1;
            case HARD -> random.nextInt(3) - 1;
            default -> 1;
        };
        return new Treasure("Chest", new Treasure.Content(statistic, value));
    }

    public static Item randomItem(Difficulty difficulty) {
        switch (random.nextInt(6)) {
            case 0:
            case 1: {
                return randomWeapon(difficulty);
            }
            case 2: {
                return randomArmor(difficulty);
            }
            default:
                return randomUsable();
        }
    }

    private static Armor randomArmor(Difficulty difficulty) {
        if(difficulty.equals(Difficulty.EASY)) {
            if (random.nextBoolean()) {
                return Armor.LIGHT_ARMOR;
            }
            return Armor.HEAVY_ARMOR;
        } else if(difficulty.equals(Difficulty.HARD)) {
            return switch (random.nextInt(6)) {
                default -> Armor.CLOTHES;
                case 0, 1, 2 -> Armor.LIGHT_ARMOR;
                case 3 -> Armor.HEAVY_ARMOR;
            };
        } else {
            return switch (random.nextInt(3)) {
                default -> Armor.LIGHT_ARMOR;
                case 1, 2 -> Armor.HEAVY_ARMOR;
            };
        }
    }

    private static Weapon randomWeapon(Difficulty difficulty) {
        if(difficulty.equals(Difficulty.EASY)) {
            return switch (random.nextInt(4)) {
                case 0 -> Weapon.KATANA;
                case 1 -> Weapon.SWORD;
                case 2 -> Weapon.SHORT_SWORD;
                default -> Weapon.CROSSBOW;
            };
        } else if(difficulty.equals(Difficulty.HARD)) {
            return switch (random.nextInt(13)) {
                default -> Weapon.STICK;
                case 0, 1, 2 -> Weapon.SWORD;
                case 3, 4 , 5 -> Weapon.SHORT_SWORD;
                case 6, 7 -> Weapon.KATANA;
                case 8 -> Weapon.CROSSBOW;
            };
        } else {
            return switch (random.nextInt(7)) {
                default -> Weapon.SWORD;
                case 0, 1 -> Weapon.SHORT_SWORD;
                case 2, 3 -> Weapon.KATANA;
                case 4 -> Weapon.CROSSBOW;
            };
        }
    }

    private static Usable randomUsable() {
        return switch (random.nextInt(4)) {
            case 1 -> Usable.DAMAGE_POTION;
            case 2 -> Usable.POWER_POTION;
            case 3 -> Usable.AGILITY_POTION;
            default -> Usable.HEALTH_POTION;
        };
    }
}
