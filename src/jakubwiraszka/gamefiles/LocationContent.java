package jakubwiraszka.gamefiles;

public interface LocationContent {
    String toString();
    String getId();
    String getName();
    String save();

    boolean isEnemy();
    boolean isTreasure();

    Position getPosition();
    void setPosition(Position position);
}
