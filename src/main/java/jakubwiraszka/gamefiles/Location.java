package jakubwiraszka.gamefiles;

import jakubwiraszka.observable.LocationContentListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Location {
    private String name;
    private String description;
    private final Position position;
    private LocationContent content;
    private HashMap<String, Position> exits;
    private boolean visited = false;

    private transient ArrayList<LocationContentListener> locationContentListeners;

    public Location(String name, String description, Position position) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.exits = new HashMap<>();

        this.locationContentListeners = new ArrayList<>();
    }

    private void notifyListeners() {
        if (!locationContentListeners.isEmpty()) {
            for (LocationContentListener i : locationContentListeners) {
                i.update(this);
            }
        }
    }

    public void addListener(LocationContentListener locationContentListener) {
        locationContentListeners.add(locationContentListener);
    }

    public void setLocationContentListeners(ArrayList<LocationContentListener> locationContentListeners) {
        this.locationContentListeners = locationContentListeners;
    }

    public void addExit(String direction, Position position) {
        exits.put(direction, position);
    }

    public void removeExit(String direction) {
        exits.remove(direction);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public LocationContent getContent() {
        return content;
    }

    public void setContent(LocationContent content) {
        this.content = content;
        notifyListeners();
    }

    public HashMap<String, Position> getExits() {
        return exits;
    }

    public void setExits(HashMap<String, Position> exits) {
        this.exits = exits;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return (getName() + ": " + getPosition().toString());
    }
}


