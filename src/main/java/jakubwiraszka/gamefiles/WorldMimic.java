package jakubwiraszka.gamefiles;

import javafx.collections.ObservableList;

import java.util.ArrayList;

public class WorldMimic {
    final String name;
    final ArrayList<Position> visitedLocations;
    final ArrayList<Location> locations;
    final ArrayList<Enemy> enemies;
    int enemyNumber;
    final ArrayList<Treasure> treasures;
    int treasureNumber;
    final int height;
    final int width;
    boolean start = false;
    final Hero hero;
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
        this.enemies.addAll(enemies);
    }

    public void setTreasures(ObservableList<Treasure> treasures) {
        this.treasures.addAll(treasures);
    }

    public World toWorld() {
        World world = new World(name, height, width, hero);
        world.getHero().getLevel().setLevelListeners(new ArrayList<>());
        world.getHero().setEnemyListeners(new ArrayList<>());
        world.setBoss(boss);
        world.getBoss().setEnemyListeners(new ArrayList<>());
        world.setPortal(portal);
        world.setStart(start);
        world.setStartLocation(startLocation);
        world.setTreasureNumber(treasureNumber);
        world.setEnemyNumber(enemyNumber);
        world.setVisitedLocations(visitedLocations);
        world.setEnemies(enemies);
        for(Enemy i: enemies) {
            i.setEnemyListeners(new ArrayList<>());
        }
        world.setTreasures(treasures);
        for(Location i: locations) {
            i.setLocationContentListeners(new ArrayList<>());
            if(i.getContent() != null) {
                i.setContent(world.findLocationContent(i.getContent().getId()));
            }
        }
        world.setLocations(locations);
        return world;
    }
}
