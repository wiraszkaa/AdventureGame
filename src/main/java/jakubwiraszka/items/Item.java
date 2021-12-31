package jakubwiraszka.items;

import jakubwiraszka.gamefiles.LocationContent;

public interface Item extends LocationContent {
    @Override
    String getId();

    @Override
    String getName();

    @Override
    boolean isEnemy();

    @Override
    boolean isTreasure();

    @Override
    boolean isItem();

    boolean isArmor();

    boolean isWeapon();

    boolean isUsable();
}
