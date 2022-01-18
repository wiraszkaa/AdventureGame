package jakubwiraszka.gamefiles;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakubwiraszka.fight.Attack;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Usable;
import jakubwiraszka.items.Weapon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hildan.fxgson.FxGsonBuilder;
import java.lang.reflect.Type;
import java.util.*;

public class GameData {

    private static final ObservableList<World> worlds = FXCollections.observableArrayList();
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
        JsonSerializer<Item> itemJsonSerializer = (jsonElement, type, jsonSerializationContext) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", jsonElement.getClass().getName());
            jsonObject.add("data", jsonSerializationContext.serialize(jsonElement.getName()));
            return jsonObject;
        };
        fxGsonBuilder.builder().registerTypeHierarchyAdapter(Item.class, itemJsonSerializer);
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
        JsonDeserializer<Item> itemJsonDeserializer = (jsonElement, type, jsonDeserializerContext) -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String typeName = jsonObject.get("type").getAsString();
            String data = jsonObject.get("data").getAsString();
            return switch (typeName) {
                case "jakubwiraszka.items.Usable" -> Usable.getByName(data);
                case "jakubwiraszka.items.Armor" -> Armor.getByName(data);
                default -> Weapon.getByName(data);
            };
        };
        fxGsonBuilder.builder().registerTypeHierarchyAdapter(Item.class, itemJsonDeserializer);
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
