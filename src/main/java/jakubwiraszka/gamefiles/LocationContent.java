package jakubwiraszka.gamefiles;

public interface LocationContent {
    String toString();
    String getId();
    String getName();

    boolean isEnemy();
    boolean isTreasure();
    boolean isItem();
}
