package jakubwiraszka.gamefiles;

import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Usable;
import jakubwiraszka.items.Weapon;
import java.util.ArrayList;
import java.util.Random;

public class Randomize {

    private static final Random random = new Random();
    private final World world;
    private final Difficulty difficulty;
    private final int maxEnemies;
    private final int maxItems;
    private int createdEnemies;
    private int createdItems;
    private int moves;

    public Randomize(World world) {
        this.world = world;
        this.difficulty = world.getDifficulty();
        double CONST_ENEMIES = world.getLocations().size() * 0.15;
        this.maxEnemies = switch (difficulty) {
            case EASY -> (int) (CONST_ENEMIES * 0.7);
            case HARD -> (int) (CONST_ENEMIES * 1.3);
            default -> (int) CONST_ENEMIES;
        };
        double CONST_ITEMS = (world.getLocations().size() - maxEnemies) * 0.15;
        this.maxItems = switch (difficulty) {
            case EASY -> (int) (CONST_ITEMS * 1.3);
            case HARD -> (int) (CONST_ITEMS * 0.7);
            default -> (int) CONST_ITEMS;
         };
    }

    public void addRandomContent(Location location) {
        boolean stopEnemiesFlag = ((double) world.getVisitedLocations().size() / (double) world.getLocations().size()) > ((double) createdEnemies / (double) maxEnemies);
        boolean stopItemsFlag = ((double) world.getVisitedLocations().size() / (double) world.getLocations().size()) > ((double) createdItems / (double) maxItems);
        if(moves > 1 && (createdEnemies < maxEnemies)) {
            moves = 0;
            Random random = new Random();
            double level = switch (world.getDifficulty()) {
                case EASY -> 0.35;
                case HARD -> 0.5;
                default -> 0.4;
            };
            int createdCount = 0;
            ArrayList<Location> nearLocations = world.getNearLocations(location);
            for (Location i : nearLocations) {
                if (!i.isVisited() && (random.nextFloat() < level) && createdCount < 1 && stopEnemiesFlag) {
                    String name = GameData.getRandomLocationContent().get(random.nextInt(GameData.getRandomLocationContent().size()));
                    Enemy enemy = world.createEnemy(randomEnemy(world.getHero(), name));
                    world.addEnemy(enemy.getId(), i.getPosition());
                    createdCount++;
                    createdEnemies++;

                } else if (!i.isVisited() && (random.nextFloat() < 1 - level) && createdCount < 1 && stopItemsFlag) {
                    if (random.nextFloat() < 0.3) {
                        Treasure treasure = world.createTreasure(randomTreasure());
                        world.addTreasure(treasure.getId(), i.getPosition());
                    } else {
                        Item item = randomItem();
                        i.setContent(item);
                    }
                    createdCount++;
                    createdItems++;
                }
            }
        }
    }

    public Enemy randomEnemy(Hero hero, String name) {
        double health = (int) (hero.getMaxHealth() * 0.7 + random.nextInt((int) (hero.getMaxHealth() * 0.6)) + 1);
        int power = (int) ((double) hero.getStatistics().getPower() * 0.7 + random.nextInt((int) (hero.getStatistics().getPower() * 0.6 + 1)));
        int agility = (int) ((double) hero.getStatistics().getAgility() * 0.7 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.6 + 1)));
        switch (difficulty) {
            case EASY -> {
                health = (int) (hero.getMaxHealth() * 0.7 + random.nextInt((int) (hero.getMaxHealth() * 0.3)) + 1);
                power = (int) ((double) hero.getStatistics().getPower() * 0.7 + random.nextInt((int) (hero.getStatistics().getPower() * 0.3 + 1)));
                agility = (int) ((double) hero.getStatistics().getAgility() * 0.7 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.3 + 1)));
            }
            case HARD -> {
                health = (int) (hero.getMaxHealth() * 0.8 + random.nextInt((int) (hero.getMaxHealth() * 0.7)) + 1);
                power = (int) ((double) hero.getStatistics().getPower() * 0.8 + random.nextInt((int) (hero.getStatistics().getPower() * 0.7 + 1)));
                agility = (int) ((double) hero.getStatistics().getAgility() * 0.8 + random.nextInt((int) (hero.getStatistics().getAgility() * 0.7 + 1)));
            }
        }
        return new Enemy(name, new Statistics(health, power, agility));
    }

    public Treasure randomTreasure() {
        String statistic = switch (random.nextInt(3)) {
            case 1 -> "Power";
            case 2 -> "Agility";
            default -> "MaxHealth";
        };
        int value = switch (difficulty) {
            case EASY -> random.nextInt(2) + 1;
            case HARD -> random.nextInt(3) - 1;
            default -> 1;
        };
        return new Treasure("Chest", new ItemContent(statistic, value));
    }

    public Item randomItem() {
        switch (random.nextInt(6)) {
            case 0:
            case 1: {
                return randomWeapon();
            }
            case 2: {
                return randomArmor();
            }
            default:
                return randomUsable();
        }
    }

    private Armor randomArmor() {
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

    private Weapon randomWeapon() {
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

    private Usable randomUsable() {
        return switch (random.nextInt(4)) {
            case 1 -> Usable.DAMAGE_POTION;
            case 2 -> Usable.POWER_POTION;
            case 3 -> Usable.AGILITY_POTION;
            default -> Usable.HEALTH_POTION;
        };
    }

    public void move() {
        moves++;
    }
}
