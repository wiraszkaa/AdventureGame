package jakubwiraszka.gamefiles;

public class Portal implements LocationContent {

    private String name;
    private boolean isActive;
    private Position position;

    public Portal(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnemy() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String save() {
        return getId();
    }
}
