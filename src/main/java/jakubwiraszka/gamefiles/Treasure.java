package jakubwiraszka.gamefiles;

public class Treasure implements LocationContent {
    private String id;

    private final String name;
    private final ItemContent content;
    private Position position;

    public Treasure(String name, ItemContent content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public ItemContent getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean isEnemy() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public String toString() {
        return (getId() + ": " + getContent().getStatistic() + " " + getContent().getValue());
    }
}

