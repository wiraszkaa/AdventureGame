package jakubwiraszka.gamefiles;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;

public class GameData {
    private static GameData instance = new GameData();
    public static final String PATH = "C:\\Users\\Wirac\\OneDrive\\Dokumenty\\My Games\\Adventure\\";

    private ObservableList<World> worlds;
    private ObservableList<String> randomLocationName = FXCollections.observableArrayList();
    private ObservableList<String> randomLocationDescription = FXCollections.observableArrayList();
    private ObservableList<String> randomLocationContent = FXCollections.observableArrayList();

    public static GameData getInstance() {
        return instance;
    }

    public ObservableList<World> getWorlds() {
        return worlds;
    }

    public String saveOne(String pathWorldName) {
        World world = findWorld(pathWorldName);
        StringBuilder content = new StringBuilder();
        if (world == null) {
            System.out.println("World not found!");
        } else {
            content.append(
                    world.getName() + "\n" +
                            world.getHeight() + "\n" +
                            world.getWidth() + "\n" +
                            world.isStart() + "\n@\n" +
                            world.getHero().save() + "\n@\n");
            for (Location i : world.getLocations()) {
                if (world.getLocations().indexOf(i) > 0) {
                    if (world.getLocations().indexOf(i) < (world.getLocations().size() - 1)) {
                        content.append(i.save()).append("#\n");
                    } else {
                        content.append(i.save());
                    }
                }
            }
            content.append("@\n");
            for (LocationContent i : world.getEnemies()) {
                if (world.getEnemies().indexOf(i) < (world.getEnemies().size() - 1)) {
                    content.append(i.save()).append("\n#\n");
                } else {
                    content.append(i.save());
                }
            }
            content.append("\n@\n");
            for (LocationContent i : world.getTreasures()) {
                if (world.getTreasures().indexOf(i) < (world.getTreasures().size() - 1)) {
                    content.append(i.save()).append("\n#\n");
                } else {
                    content.append(i.save());
                }
            }
            String path = "C:\\Users\\Wirac\\OneDrive\\Dokumenty\\My Games\\Adventure\\" + pathWorldName + ".txt";
            if (FileEditor.write(content.toString(), path)) {
                System.out.println(pathWorldName + " successfully saved");
            } else {
                System.out.println(pathWorldName + " wasn't saved");
            }
        }
        return content.toString();
    }

    public String getContent(String pathWorldName) {
        String path = "C:\\Users\\Wirac\\OneDrive\\Dokumenty\\My Games\\Adventure\\" + pathWorldName + ".txt";
        return FileEditor.read(path);
    }

    public void loadOne(String content) {
        if (content == null) {
            System.out.println("World wasn't loaded. Wrong world name!");
        } else {
            String[] text = content.split("\n@\n");
            String[] worldParameters = text[0].split("\n");
            String[] heroParameters = text[1].split("\n");
            String[] locationsList = text[2].split("\n#\n");
            String[] enemiesList = text[3].split("\n#\n");
            String[] treasuresList;
            try {
                treasuresList = text[4].split("\n#\n");
            } catch (Exception e) {
                System.out.println("There are no treasures!");
                treasuresList = null;
            }

            String heroName = heroParameters[0];
            String[] statistics = heroParameters[1].split(" ");
            int health = Integer.parseInt(statistics[0]);
            int power = Integer.parseInt(statistics[1]);
            int agility = Integer.parseInt(statistics[2]);
            boolean isAlive = Boolean.parseBoolean(heroParameters[2]);
            String[] position = heroParameters[3].split(" ");
            int positionX = Integer.parseInt(position[0]);
            int positionY = Integer.parseInt(position[1]);
            int maxHealth = Integer.parseInt(heroParameters[4]);
            int level = Integer.parseInt(heroParameters[5]);
            int experience = Integer.parseInt(heroParameters[6]);
            int pointsToSpend = Integer.parseInt(heroParameters[7]);
            Hero hero = new Hero(heroName, new Statistics(health, power, agility), level);
            hero.getLevel().addExperience(experience);
            hero.getLevel().setPointsToSpend(pointsToSpend);
            hero.setMaxHealth(maxHealth);

            String worldName = worldParameters[0];
            int height = Integer.parseInt(worldParameters[1]) + 1;
            int width = Integer.parseInt(worldParameters[2]) + 1;
            boolean isStarted = Boolean.parseBoolean(worldParameters[3]);
            worlds.add(new World(worldName, height, width, hero));
            World world = findWorld(worldName);
            if (world == null) {
                System.out.println("World not loaded properly!");
            } else {
                if (isStarted) {
                    world.setStart(true);
                    world.getHero().setPosition(new Position(positionX, positionY));
                } else {
                    world.getHero().setPosition(new Position(world.getLocations().get(0).getPosition().getX(), world.getLocations().get(0).getPosition().getY()));
                }

                System.out.println(world.getName() + " loaded...");
                System.out.println("Hero: " + world.getHero().toString() + "\n=================================");
                world.getHero().setAlive(isAlive);

                addEnemies(enemiesList, world);

                if(treasuresList != null) {
                    addTreasures(treasuresList, world);
                }

                addLocations(locationsList, world);
            }
        }
    }



    public void loadAll() {
        worlds = FXCollections.observableArrayList();

        String input = FileEditor.read(PATH + "Worlds.txt");

        if(input != null) {
            String[] worlds = input.split("\n✹\n");

            for (int i = 0; i < (worlds.length); i++) {
                loadOne(worlds[i]);
            }
        }

        String content = FileEditor.read(GameData.PATH + "randomLocations.txt");
        String[] randomLocations = content.split("\n");
        for (String randomLocation : randomLocations) {
            String[] locationParameters = randomLocation.split("\t");
            this.randomLocationName.add(locationParameters[0]);
            this.randomLocationDescription.add(locationParameters[1]);
            this.randomLocationContent.add(locationParameters[2]);
        }
    }

    public void saveAll() {
        Iterator<World> iterator = worlds.iterator();
        StringBuilder worlds = new StringBuilder();
        while (iterator.hasNext()) {
            World world = iterator.next();
            String content = saveOne(world.getName());
            worlds.append(content);
            worlds.append("\n✹\n");
        }
        FileEditor.write(worlds.toString(), (PATH + "Worlds.txt"));
    }

    private void addLocations(String[] locationsList, World world) {
        for (String s : locationsList) {
            String[] locationParameters = s.split("\n★\n");
            String locationName = locationParameters[0];
            String locationDescription = locationParameters[1];
            String[] locationPosition = locationParameters[2].split(" ");
            String locationContent = locationParameters[3];
            String[] locationExits = locationParameters[4].split("\n");
            boolean locationIsVisited = Boolean.parseBoolean(locationParameters[5]);
            Position position = new Position(Integer.parseInt(locationPosition[0]), Integer.parseInt(locationPosition[1]));
            world.addLocation(new Location(locationName, locationDescription, position), true);
            Location location = world.findLocation(position, world.getLocations());
            if (location == null) {
                System.out.println("Location " + locationName + " not loaded properly!");
            } else {
                if(world.findEnemy(locationContent) != null) {
                    location.setContent(world.findEnemy(locationContent));
                    world.findEnemy(locationContent).setPosition(location.getPosition());
                } else if(world.findTreasure(locationContent) != null) {
                    location.setContent(world.findTreasure(locationContent));
                    world.findTreasure(locationContent).setPosition(location.getPosition());
                }
                for (String locationExit : locationExits) {
                    String[] locationExitParameters = locationExit.split(" ");
                    world.findLocation(position, world.getLocations()).addExit(locationExitParameters[0], new Position(Integer.parseInt(locationExitParameters[1]), Integer.parseInt(locationExitParameters[2])));
                }
                world.findLocation(position, world.getLocations()).setVisited(locationIsVisited);
                System.out.println(world.findLocation(position, world.getLocations()).toString() + " loaded...");
            }
        }
    }

    private void addEnemies(String[] enemiesList, World world) {
        System.out.println("=================================");
        for (String s : enemiesList) {
            String[] enemyParameters = s.split("\n");
            String enemyName = enemyParameters[0];
            String[] enemyStatistics = enemyParameters[1].split(" ");
            String enemyIsAlive = enemyParameters[2];
            String id = world.createEnemy(new Enemy(enemyName, new Statistics(Integer.parseInt(enemyStatistics[0]), Integer.parseInt(enemyStatistics[1]), Integer.parseInt(enemyStatistics[2])))).getId();
            if (world.findEnemy(id) == null) {
                System.out.println("Enemy " + enemyName + " not loaded properly!");
            } else {
                world.findEnemy(id).setAlive(Boolean.parseBoolean(enemyIsAlive));
                System.out.println(world.findEnemy(id).toString() + " loaded...");
            }
        }
    }

    private void addTreasures(String[] treasuresList, World world) {
        System.out.println("=================================");
        for (String s : treasuresList) {
            String[] treasureParameters = s.split("\n");
            String treasureName = treasureParameters[0];
            String treasureStatistic = treasureParameters[1];
            String treasureValue = treasureParameters[2];
            String id = world.createTreasure(new Treasure(treasureName, new Treasure.Content(treasureStatistic, Integer.parseInt(treasureValue)), 1)).getId();
            if (world.findTreasure(id) == null) {
                System.out.println("Treasure " + treasureName + " not loaded properly!");
            } else {
                System.out.println(world.findTreasure(id).toString() + " loaded...");
            }
        }
    }

    public World findWorld(String name) {
        for (World i : worlds) {
            if (Objects.equals(i.getName(), name)) {
                return i;
            }
        }
        return null;
    }

    public ObservableList<String> getRandomLocationName() {
        return randomLocationName;
    }

    public ObservableList<String> getRandomLocationDescription() {
        return randomLocationDescription;
    }

    public ObservableList<String> getRandomLocationContent() {
        return randomLocationContent;
    }
}
