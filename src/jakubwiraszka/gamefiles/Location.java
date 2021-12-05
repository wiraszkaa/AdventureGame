package jakubwiraszka.gamefiles;

import java.util.HashMap;
import java.util.Map;

public class Location {
    private String name;
    private String description;
    private final Position position;
    private LocationContent content;
    private Map<String, Position> exits;
    private boolean visited = false;

    public Location(String name, String description, Position position) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.exits = new HashMap<>();
        this.exits.put("Q", new Position(-1, -1));
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

    }

    public HashMap<String, Position> getExits() {
        return new HashMap<>(exits);
    }

    public void setExits(HashMap<String, Position> exits) {
        this.exits = exits;
    }

    public String save() {
        String locationContent = "empty";
        if(getContent() != null) {
            locationContent = getContent().getId();
        }
        StringBuilder content = new StringBuilder(getName() + "\n★\n"
                + getDescription() + "\n★\n"
                + position.getX() + " " + position.getY() + "\n★\n"
                + locationContent + "\n★\n");
        for (String key : exits.keySet()) {
            content.append(key)
                    .append(" ")
                    .append(exits.get(key).getX())
                    .append(" ")
                    .append(exits.get(key).getY()).append("\n");
        }
        content.append("★\n")
                .append(isVisited())
                .append("\n");
        return content.toString();
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


