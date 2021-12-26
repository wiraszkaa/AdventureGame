package jakubwiraszka.gamefiles;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakubwiraszka.fight.Attack;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.observable.LevelListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hildan.fxgson.FxGsonBuilder;
import java.lang.reflect.Type;
import java.util.*;

public class GameData {

    private static ObservableList<World> worlds = FXCollections.observableArrayList();
    private static final ObservableList<String> randomLocationName = FXCollections.observableArrayList();
    private static final ObservableList<String> randomLocationDescription = FXCollections.observableArrayList();
    private static final ObservableList<String> randomLocationContent = FXCollections.observableArrayList();
    private static final String PATH = "src\\main\\resources\\saves\\";
    private static Gson fxGson;

    public static ObservableList<World> getWorlds() {
        return worlds;
    }

    public static void start() {
        createFxGson();
        load();
    }

    private static void createFxGson() {
        FxGsonBuilder fxGsonBuilder = new FxGsonBuilder();
        fxGsonBuilder.builder().setPrettyPrinting();
        JsonSerializer<LocationContent> serializer = (locationContent, type, jsonSerializationContext) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", locationContent.getId());
            return jsonObject;
        };
        fxGsonBuilder.builder().registerTypeAdapter(LocationContent.class, serializer);
        JsonDeserializer<LocationContent> deserializer = (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get("id") != null) {
                return new LocationContentID(jsonObject.get("id").getAsString());
            }
            return null;
        };
        fxGsonBuilder.builder().registerTypeAdapter(LocationContent.class, deserializer);
        JsonDeserializer<Attack> attackJsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> new StrongAttack();
        fxGsonBuilder.builder().registerTypeAdapter(Attack.class, attackJsonDeserializer);
        fxGson = fxGsonBuilder.create();
    }

    public static void save() {
        ArrayList<WorldMimic> worldMimics = new ArrayList<>();
        for(World world: worlds) {
            worldMimics.add(world.toWorldMimic());
        }
        if(FileEditor.write(fxGson.toJson(worldMimics), PATH + "Worlds.json")) {
            System.out.println("Worlds successfully saved");
        } else {
            System.out.println("Error saving Worlds");
        }
    }

    private static void load() {
        Type listType = new TypeToken<ArrayList<WorldMimic>>(){}.getType();
        ArrayList<WorldMimic> loadedWorlds = fxGson.fromJson(FileEditor.read(PATH + "Worlds.json"), listType);
        if(loadedWorlds != null) {
            for (WorldMimic worldMimic : loadedWorlds) {
                World world = worldMimic.toWorld();
                worlds.add(world);
            }
        }
        loadRandomLocations();
    }

    public static String saveOne(String pathWorldName) {
        World world = findWorld(pathWorldName);
        StringBuilder content = new StringBuilder();
        if (world == null) {
            System.out.println("World not found!");
        } else {
            content.append(world.getName())
                    .append("\n")
                    .append(world.getHeight())
                    .append("\n")
                    .append(world.getWidth())
                    .append("\n")
                    .append(world.isStart())
                    .append("\n@\n")
                    .append(world.getHero().save())
                    .append("\n@\n");
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
            for (Enemy i : world.getEnemies()) {
                if (world.getEnemies().indexOf(i) < (world.getEnemies().size() - 1)) {
                    content.append(i.save()).append("\n#\n");
                } else {
                    content.append(i.save());
                }
            }
            content.append("\n@\n");
            for (Treasure i : world.getTreasures()) {
                if (world.getTreasures().indexOf(i) < (world.getTreasures().size() - 1)) {
                    content.append(i.save()).append("\n#\n");
                } else {
                    content.append(i.save());
                }
            }
            String path = PATH + pathWorldName + ".txt";
            if (FileEditor.write(content.toString(), path)) {
                System.out.println(pathWorldName + " successfully saved");
            } else {
                System.out.println(pathWorldName + " wasn't saved");
            }
        }
        return content.toString();
    }

    private static void loadOne(String content) {
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
            int height = Integer.parseInt(worldParameters[1]);
            int width = Integer.parseInt(worldParameters[2]);
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



    private static void loadAll() {
        worlds = FXCollections.observableArrayList();

        String input = FileEditor.read(PATH + "Worlds.txt");

        if(input != null) {
            String[] worlds = input.split("\n✹\n");

            for (String world : worlds) {
                loadOne(world);
            }
        }

        loadRandomLocations();
    }

    private static void loadRandomLocations() {
        String content = FileEditor.read(PATH + "randomLocations.txt");
        if(content != null) {
            String[] randomLocations = content.split("\n");
            for (String randomLocation : randomLocations) {
                String[] locationParameters = randomLocation.split("\t");
                randomLocationName.add(locationParameters[0]);
                randomLocationDescription.add(locationParameters[1]);
                randomLocationContent.add(locationParameters[2]);
            }
        }
    }

    public static void saveAll() {
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

    private static void addLocations(String[] locationsList, World world) {
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

    private static void addEnemies(String[] enemiesList, World world) {
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

    private static void addTreasures(String[] treasuresList, World world) {
        System.out.println("=================================");
        for (String s : treasuresList) {
            String[] treasureParameters = s.split("\n");
            String treasureName = treasureParameters[0];
            String treasureStatistic = treasureParameters[1];
            String treasureValue = treasureParameters[2];
            String id = world.createTreasure(new Treasure(treasureName, new Treasure.Content(treasureStatistic, Integer.parseInt(treasureValue)))).getId();
            if (world.findTreasure(id) == null) {
                System.out.println("Treasure " + treasureName + " not loaded properly!");
            } else {
                System.out.println(world.findTreasure(id).toString() + " loaded...");
            }
        }
    }

    public static World findWorld(String name) {
        for (World i : worlds) {
            if (Objects.equals(i.getName(), name)) {
                return i;
            }
        }
        return null;
    }

    public static ObservableList<String> getRandomLocationName() {
        return randomLocationName;
    }

    public static ObservableList<String> getRandomLocationDescription() {
        return randomLocationDescription;
    }

    public static ObservableList<String> getRandomLocationContent() {
        return randomLocationContent;
    }
}