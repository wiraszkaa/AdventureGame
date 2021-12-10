package jakubwiraszka.gamefiles;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class WorldMimic {
    String name;
    ArrayList<Position> visitedLocations;
    ArrayList<Location> locations;
    ArrayList<Enemy> enemies;
    int enemyNumber;
    ArrayList<Treasure> treasures;
    int treasureNumber;
    int height;
    int width;
    boolean start = false;
    Hero hero;
    Location startLocation;
    Portal portal;
    Enemy boss;

    public WorldMimic(String name, int height, int width, Hero hero) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.visitedLocations = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.treasures = new ArrayList<>();
        this.hero = hero;
        this.portal = new Portal("Portal");
        this.boss = new Enemy("Boss", new Statistics(1, 0, 0));
    }

    public void setVisitedLocations(ObservableList<Position> visitedLocations) {
        this.visitedLocations.addAll(visitedLocations);
    }

    public void setLocations(ObservableList<Location> locations) {
        this.locations.addAll(locations);
    }

    public void setEnemies(ObservableList<Enemy> enemies) {
        for(LocationContent enemy: enemies) {
            this.enemies.add((Enemy) enemy);
        }
    }

    public void setTreasures(ObservableList<Treasure> treasures) {
        for(LocationContent treasure: treasures) {
            this.treasures.add((Treasure) treasure);
        }
    }

    public World toWorld() {
        World world = new World(name, height, width, hero);
        world.getHero().getLevel().setLevelListeners(new ArrayList<>());
        world.setBoss(boss);
        world.setPortal(portal);
        world.setStart(start);
        world.setStartLocation(startLocation);
        world.setTreasureNumber(treasureNumber);
        world.setEnemyNumber(enemyNumber);
        world.setVisitedLocations(visitedLocations);
        world.setEnemies(enemies);
        world.setTreasures(treasures);
        for(Location i: locations) {
            if(i.getContent() != null) {
                i.setContent(world.findLocationContent(i.getContent().getId()));
            }
        }
        world.setLocations(locations);
        return world;
    }
}
