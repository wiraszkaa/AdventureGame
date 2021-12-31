package jakubwiraszka.gamefiles;

public class Portal implements LocationContent {

    private final String name;
    private final String id;
    private boolean isActive;

    public Portal(String name) {
        this.name = name;
        this.id = "Portal";
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String getId() {
        return id;
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
    public boolean isItem() {
        return false;
    }
}
