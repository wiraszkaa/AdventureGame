package jakubwiraszka.gamefiles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class World {
    private final String name;
    private final ObservableList<Position> visitedLocations;
    private final ObservableList<Location> locations;
    private final ObservableList<Enemy> enemies;
    private int enemyNumber;
    private final ObservableList<Treasure> treasures;
    private int treasureNumber;
    private final int height;
    private final int width;
    private boolean start = false;
    private final Hero hero;
    private Location startLocation;
    private Portal portal;
    private Enemy boss;

    public World(String name, int height, int width, Hero hero) {
        this.name = name;
        System.out.println("Set name to " + name);
        this.height = height;
        System.out.println("Set " + name + "'s height to " + height);
        this.width = width;
        System.out.println("Set " + name + "'s width to " + width);
        this.visitedLocations = FXCollections.observableArrayList();
        this.locations = FXCollections.observableArrayList();
        this.enemies = FXCollections.observableArrayList();
        this.treasures = FXCollections.observableArrayList();
        this.hero = hero;
        this.portal = new Portal("Portal");
        this.boss = new Enemy("Boss", new Statistics(1, 0, 0));
        this.boss.setId("Boss");
    }

    public void createStart() {
        startLocation = new Location("Start", "You are at the start of Your-ney. Have fun playing! Creator: Jakub Wiraszka", new Position((width / 2), (height / 2)));
        startLocation.setVisited(true);
        locations.add(startLocation);
        addVisited(startLocation.getPosition());
        hero.setPosition(startLocation.getPosition());
        System.out.println("Set Starting location to position " + startLocation.getPosition().toString());
    }

    public void createRandom(List<String> locationName, List<String> locationDescription, List<String> locationContent, Difficulty difficulty) {
        Random random = new Random();

        double value = (double) (random.nextInt(4) + 5) / 10 * (double) height * (double) width;
        int operations = (int) (value);
        double level = switch (difficulty) {
            case EASY -> 40;
            case MEDIUM -> 20;
            case HARD -> 10;
        };
        System.out.println("=====================");
        System.out.println("Map randomization started...");
        System.out.println("I will create " + operations + " locations");
        int i = 0;
        while (i < operations) {
            Location newLocation;
            int index = random.nextInt(locationName.size());
            int locationX;
            int locationY;
            if(locations.size() < 2) {
                locationX = startLocation.getPosition().getX();
                locationY = startLocation.getPosition().getY();
            } else {
                Location location = locations.get(random.nextInt(locations.size()));
                locationX = location.getPosition().getX();
                locationY = location.getPosition().getY();
            }
            if (random.nextInt(2) == 0) {
                int y = -1;
                if (findLocation(locationX, (locationY + 1), locations) == null) {
                    if(locationY + 1 <= height) {
                        y = locationY + 1;
                    }
                } else if (findLocation(locationX, (locationY - 1), locations) == null) {
                    y = locationY - 1;
                }
                if (y >= 0) {
                    newLocation = new Location(locationName.get(index), locationDescription.get(index), new Position(locationX, y));
                    if (addLocation(newLocation, false)) {
                        i++;
                    }
                }
            } else {
                int x = -1;
                if (findLocation(locationX + 1, (locationY), locations) == null) {
                    if(locationX + 1 <= width) {
                        x = locationX + 1;
                    }
                } else if (findLocation(locationX - 1, (locationY), locations) == null) {
                    x = locationX - 1;
                }
                if (x >= 0) {
                    newLocation = new Location(locationName.get(index), locationDescription.get(index), new Position(x, locationY));
                    if (addLocation(newLocation, false)) {
                        i++;
                    }
                }
            }
        }
        ArrayList<Location> locationsToRemove = new ArrayList<>();
        for (Location location : locations) {
            if (location.getExits().size() == 4) {
                locationsToRemove.add(location);
            }
        }
        System.out.println("=====================");
        System.out.println("I have " + locationsToRemove.size() + " locations with 4 exits, I will try to delete some of them");
        for (int j = 0; j < (locationsToRemove.size() / 2); j++) {
            int index = random.nextInt(locationsToRemove.size() - 1) + 1;
            removeLocation(locationsToRemove.get(index));
            locationsToRemove.remove(locationsToRemove.get(index));
        }

        addRandomContent(locationContent, random, level);
    }

    private void addRandomContent(List<String> locationContent, Random random, double level) {
        int counter = 0;
        double numberOfEnemies = (double) locations.size() / 20.0 + (double) locations.size() / level;
        System.out.println("I will try to create " + (int) numberOfEnemies + " enemies");
        while (counter < (int) numberOfEnemies) {
            Location location = locations.get(random.nextInt(locations.size()));
            if(location.getContent() == null && !location.equals(startLocation)) {
                int health = Math.max(getHero().getStatistics().getHealthValue() - 5 + random.nextInt(9), 5);
                int power = Math.max(getHero().getStatistics().getPowerValue() - 5 + random.nextInt(6), 5);
                int agility = Math.max(getHero().getStatistics().getAgilityValue() - 5 + random.nextInt(9), 0);
                Enemy enemy = createEnemy(new Enemy(locationContent.get(random.nextInt(locationContent.size() - 1) + 1), new Statistics(health, power, agility)));
                addEnemy(enemy.getId(), location.getPosition());
            }
            counter++;
        }
        System.out.println("I will try to create " + (int) (numberOfEnemies / 2) + " treasures");
        counter = 0;
        while (counter < (int) (numberOfEnemies / 2)) {
            Location location = locations.get(random.nextInt(locations.size()));
            if(location.getContent() == null) {
                String statistic = switch (random.nextInt(3)) {
                    case 1 -> "Power";
                    case 2 -> "Agility";
                    default -> "Health";
                };
                int value = random.nextInt(2) + 1;
                Treasure treasure = createTreasure(new Treasure("Chest", new Treasure.Content(statistic, value)));
                addTreasure(treasure.getId(), location.getPosition());
            }
            counter++;
        }
    }

    public Enemy createEnemy(Enemy enemy) {
        if (enemy.getStatistics().getHealthValue() <= 0 || enemy.getStatistics().getPowerValue() < 0 || enemy.getStatistics().getAgilityValue() < 0) {
            System.out.println("Enemy not created. Try different statistics.");
        } else if (findEnemy(enemy.getId()) == null) {
            enemies.add(enemy);
            enemy.setId(enemy.getName() + enemyNumber);
            enemyNumber++;
            System.out.println("Successfully created " + enemy);
        } else {
            System.out.println("Enemy already created.");
        }

        return enemy;
    }

    public Treasure createTreasure(Treasure treasure) {
        if (findTreasure(treasure.getId()) == null) {
            treasures.add(treasure);
            treasure.setId(treasure.getName() + treasureNumber);
            treasureNumber++;
            System.out.println("Successfully created " + treasure);
        } else {
            System.out.println("Treasure already created.");
        }
        return treasure;
    }

    public void removeContent(LocationContent locationContent) {
        if (findEnemy(locationContent.getId()) != null) {
            enemies.remove((Enemy) locationContent);
        } else if (findTreasure(locationContent.getId()) != null) {
            treasures.remove((Treasure) locationContent);
        }
        handleContent(locationContent.getId());
        System.out.println(locationContent + " was removed");
    }

    private void handleContent(String contentId) {
        for (Location i : locations) {
            if(i.getContent() != null) {
                if (i.getContent().getId().equals(contentId)) {
                    i.setContent(null);
                }
            }
        }
    }

    public boolean addLocation(Location location, boolean ignoreCounter) {
        if (findLocation(location.getPosition().getX(), location.getPosition().getY(), locations) == null) {
            int counter = 0;
            for (Location i : locations) {
                if ((i.getPosition().getX() == location.getPosition().getX()) && Math.abs(i.getPosition().getY() - location.getPosition().getY()) == 1) {
                    counter++;
                }
                if ((i.getPosition().getY() == location.getPosition().getY()) && Math.abs(i.getPosition().getX() - location.getPosition().getX()) == 1) {
                    counter++;
                }
            }
            if (counter > 0 || ignoreCounter) {
                handleExits(location, locations, true);
                locations.add(location);
                System.out.println("Successfully added " + location.getName() + " on position " + location.getPosition().toString());
                return true;
            }
        }

        System.out.println("Couldn't add " + location.getName() + ". Try different name or position.");
        return false;
    }

    public void removeLocation(Location location) {
        if (findLocation(location.getPosition().getX(), location.getPosition().getY(), locations) != null) {
            if (checkDeletedLocation(location)) {
                handleExits(location, locations, false);
                locations.remove(location);
                System.out.println("Successfully removed " + location.getName() + " on position " + location.getPosition().toString());
                return;
            }
        }
        System.out.println("Couldn't remove " + location.getName() + ". Try different name or position.");
    }

    public void removeLocationNotSafely(Location location) {
        handleExits(location, locations, false);
        locations.remove(location);
        System.out.println("Successfully removed " + location.getName() + " on position " + location.getPosition().toString());
    }

    private boolean checkDeletedLocation(Location location) {
        if(findLocation(location.getPosition(), locations) != null) {
            ArrayList<Location> nearLocations = getNearLocations(location);
            ObservableList<Location> locationsToCheck = FXCollections.observableArrayList();
            for (Location i : locations) {
                Location copyLocation = new Location(i.getName(), i.getDescription(), i.getPosition());
                copyLocation.setExits(new HashMap<>(i.getExits()));
                locationsToCheck.add(copyLocation);
            }
            handleExits(findLocation(location.getPosition(), locationsToCheck), locationsToCheck, false);
            locationsToCheck.remove(findLocation(location.getPosition(), locationsToCheck));
            for (Location i : nearLocations) {
                if (!goToStart(i, locationsToCheck)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Location> getNearLocations(Location location) {
        ArrayList<Location> nearLocations = new ArrayList<>();
        for (Location i : locations) {
            if ((i.getPosition().getX() == location.getPosition().getX()) && (i.getPosition().getY() - location.getPosition().getY()) == 1) { //UP - location
                nearLocations.add(i);
            }
            if ((i.getPosition().getX() == location.getPosition().getX()) && (i.getPosition().getY() - location.getPosition().getY()) == -1) { //DOWN
                nearLocations.add(i);
            }
            if ((i.getPosition().getY() == location.getPosition().getY()) && (i.getPosition().getX() - location.getPosition().getX()) == 1) { //LEFT
                nearLocations.add(i);
            }
            if ((i.getPosition().getY() == location.getPosition().getY()) && (i.getPosition().getX() - location.getPosition().getX()) == -1) { //RIGHT
                nearLocations.add(i);
            }
        }
        return nearLocations;
    }

    private boolean goToStart(Location location, ObservableList<Location> locations) {
        if (findLocation(location.getPosition(), locations) != null) {
            Location start = locations.get(0);
            int startX = start.getPosition().getX();
            int startY = start.getPosition().getY();
            int beforePreviousX;
            int beforePreviousY;
            int previousX = -1;
            int previousY = -1;
            int x = location.getPosition().getX();
            int y = location.getPosition().getY();
            int moves = 0;
            int stuckMoves = 0;
            if (location.getPosition() == start.getPosition()) {
                return true;
            } else {
                while (moves < (width * height)) {
                    HashMap<String, Position> exits = findLocation(x, y, locations).getExits();
                    HashMap<String, Double> distances = new HashMap<>();
                    if (exits.get("N") != null) {
                        double newDistance = (x - startX) * (x - startX) + (y - startY - 1) * (y - startY - 1);
                        distances.put("N", newDistance);
                    }
                    if (exits.get("E") != null) {
                        double newDistance = (x - startX + 1) * (x - startX + 1) + (y - startY) * (y - startY);
                        distances.put("E", newDistance);
                    }
                    if (exits.get("W") != null) {
                        double newDistance = (x - startX - 1) * (x - startX - 1) + (y - startY) * (y - startY);
                        distances.put("W", newDistance);
                    }
                    if (exits.get("S") != null) {
                        double newDistance = (x - startX) * (x - startX) + (y - startY + 1) * (y - startY + 1);
                        distances.put("S", newDistance);
                    }
                    String direction = findMin(distances);
                    beforePreviousX = previousX;
                    beforePreviousY = previousY;
                    previousX = x;
                    previousY = y;
                    if (direction == null) {
                        return false;
                    } else if (direction.equals("N")) {
                        y -= 1;
                    } else if (direction.equals("E")) {
                        x += 1;
                    } else if (direction.equals("W")) {
                        x -= 1;
                    } else if (direction.equals("S")) {
                        y += 1;
                    }
                    if (beforePreviousX == x && beforePreviousY == y) {
                        stuckMoves++;
                    }
                    if (stuckMoves > 3) {
                        double xDistance = (x - startX) * (x - startX) + (y - startY) * (y - startY);
                        double previousDistance = (previousX - startX) * (previousX - startX) + (previousY - startY) * (previousY - startY);
                        if (xDistance > previousDistance) {
                            handleExits(findLocation(previousX, previousY, locations), locations, false);
                            locations.remove(findLocation(previousX, previousY, locations));
                        } else {
                            handleExits(findLocation(x, y, locations), locations, false);
                            locations.remove(findLocation(x, y, locations));
                            x = previousX;
                            y = previousY;
                        }
                        stuckMoves = 0;
                    }
                    if (x == startX && y == startY) {
                        return true;
                    }


                    moves++;
                }
            }
        }
        return false;
    }

    private String findMin(HashMap<String, Double> hashMap) {
        double min = Double.MAX_VALUE;
        String key = null;
        for (String i : hashMap.keySet()) {
            if (hashMap.get(i) < min) {
                min = hashMap.get(i);
                key = i;
            }
        }
        return key;
    }

    public void addVisited(Position position) {
        if (findVisitedLocation(position) == null) {
            findLocation(position.getX(), position.getY(), locations).setVisited(true);
            visitedLocations.add(position);
        }
    }

    public void handleVisited() {
        for (Location i : locations) {
            if (i.isVisited()) {
                if (findVisitedLocation(i.getPosition()) == null) {
                    visitedLocations.add(i.getPosition());
                }
            }
        }
    }

    public void addEnemy(String enemyId, Position position) {
        Enemy enemy = findEnemy(enemyId);
        Location location = findLocation(position, locations);
        if (location == null || enemy == null) {
            System.out.println("Enemy not added. Try different enemy name or location name.");
        } else {
            enemy.setPosition(position);
            location.setContent(enemy);
            System.out.println("Successfully added " + enemyId + " to " + position.toString());
        }
    }

    public void addTreasure(String treasureId, Position position) {
        Treasure treasure = findTreasure(treasureId);
        Location location = findLocation(position, locations);
        if (location == null || treasure == null) {
            System.out.println("Treasure not added. Try different treasure name or location name");
        } else {
            treasure.setPosition(position);
            location.setContent(treasure);
            System.out.println("Successfully added " + treasureId + " to " + position.toString());
        }
    }

    public boolean allEnemiesDead() {
        for(LocationContent i: enemies) {
            Enemy enemy = (Enemy) i;
            if(enemy.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public Portal getPortal() {
        return portal;
    }

    public Enemy findEnemy(String id) {
        for (LocationContent i : enemies) {
            if (Objects.equals(i.getId(), id)) {
                return (Enemy) i;
            }
        }
        return null;
    }

    public Treasure findTreasure(String id) {
        for (LocationContent i : treasures) {
            if (Objects.equals(i.getId(), id)) {
                return (Treasure) i;
            }
        }
        return null;
    }

    public LocationContent findLocationContent(String id) {
        for(LocationContent i: enemies) {
            if(id.equals(i.getId())) {
                return i;
            }
        }
        for (LocationContent i : treasures) {
            if (Objects.equals(i.getId(), id)) {
                return i;
            }
        }
        return null;
    }

    public Location findLocation(int x, int y, ObservableList<Location> locations) {
        Position position = new Position(x, y);
        for (Location i : locations) {
            if (i.getPosition().compareTo(position) == 0) {
                return i;
            }
        }
        return null;
    }

    public Location findLocation(Position position, ObservableList<Location> locations) {
        for (Location i : locations) {
            if (i.getPosition().compareTo(position) == 0) {
                return i;
            }
        }
        return null;
    }

    public Location findLocationByContent(String id) {
        for(Location i: locations) {
            if(i.getContent() != null) {
                if (i.getContent().getId().equals(id)) {
                    return i;
                }
            }
        }
        return null;
    }

    public Position findVisitedLocation(Position position) {
        for (Position i : visitedLocations) {
            if (i.equals(position)) {
                return i;
            }
        }
        return null;
    }

    private void handleExits(Location location, ObservableList<Location> locations, boolean trueIfAdd) {
        for (Location i : locations) {
            if ((i.getPosition().getX() == location.getPosition().getX()) && (i.getPosition().getY() - location.getPosition().getY()) == 1) { //UP
                if (trueIfAdd) {
                    i.addExit("N", location.getPosition());
                    location.addExit("S", i.getPosition());
                } else {
                    i.removeExit("N");
                    location.removeExit("S");
                }
            }
            if ((i.getPosition().getX() == location.getPosition().getX()) && (i.getPosition().getY() - location.getPosition().getY()) == -1) { //DOWN
                if (trueIfAdd) {
                    i.addExit("S", location.getPosition());
                    location.addExit("N", i.getPosition());
                } else {
                    i.removeExit("S");
                    location.removeExit("N");
                }
            }
            if ((i.getPosition().getY() == location.getPosition().getY()) && (i.getPosition().getX() - location.getPosition().getX()) == 1) { //LEFT
                if (trueIfAdd) {
                    i.addExit("W", location.getPosition());
                    location.addExit("E", i.getPosition());
                } else {
                    i.removeExit("W");
                    location.removeExit("E");
                }
            }
            if ((i.getPosition().getY() == location.getPosition().getY()) && (i.getPosition().getX() - location.getPosition().getX()) == -1) { //RIGHT
                if (trueIfAdd) {
                    i.addExit("E", location.getPosition());
                    location.addExit("W", i.getPosition());
                } else {
                    i.removeExit("E");
                    location.removeExit("W");
                }
            }
        }
    }

    @Override
    public String toString() {
        return getName() + ": " + (isStart() ? "Started" : "Not Started");
    }

    public String getName() {
        return name;
    }

    public ObservableList<Location> getLocations() {
        return locations;
    }

    public ObservableList<Position> getVisitedLocations() {
        return visitedLocations;
    }

    public ObservableList<Enemy> getEnemies() {
        return enemies;
    }

    public ObservableList<Treasure> getTreasures() {
        return treasures;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Hero getHero() {
        return hero;
    }

    public Enemy getBoss() {
        return boss;
    }

    public void setVisitedLocations(ArrayList<Position> visitedLocations) {
        this.visitedLocations.addAll(visitedLocations);
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations.addAll(locations);
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies.addAll(enemies);
    }

    public void setTreasures(ArrayList<Treasure> treasures) {
        this.treasures.addAll(treasures);
    }

    public void setEnemyNumber(int enemyNumber) {
        this.enemyNumber = enemyNumber;
    }

    public void setTreasureNumber(int treasureNumber) {
        this.treasureNumber = treasureNumber;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    public void setBoss(Enemy boss) {
        this.boss = boss;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public WorldMimic toWorldMimic() {
        WorldMimic worldMimic = new WorldMimic(name, height, width, hero);
        worldMimic.boss = boss;
        worldMimic.portal = portal;
        worldMimic.startLocation = startLocation;
        worldMimic.treasureNumber = treasureNumber;
        worldMimic.enemyNumber = enemyNumber;
        worldMimic.setEnemies(enemies);
        worldMimic.setVisitedLocations(visitedLocations);
        worldMimic.setTreasures(treasures);
        worldMimic.setLocations(locations);
        return worldMimic;
    }
}
