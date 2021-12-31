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
