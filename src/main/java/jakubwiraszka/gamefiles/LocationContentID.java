package jakubwiraszka.gamefiles;

public class LocationContentID implements LocationContent {
    public final String id;

    public LocationContentID(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return id;
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
