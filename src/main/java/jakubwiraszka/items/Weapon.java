package jakubwiraszka.items;

public enum Weapon implements Item {
    HANDS,
    CROSSBOW,
    KATANA,
    SHORT_SWORD,
    SWORD,
    STICK;

    public double getHitChance() {
        return switch (this) {
            case CROSSBOW -> 100;
            case KATANA -> 0.8;
            case SHORT_SWORD -> 1.3;
            default -> 1;
        };
    }

    public double getDamageMultiplier() {
        return switch (this) {
            case SHORT_SWORD -> 1.2;
            case SWORD -> 1.5;
            case KATANA -> 1.8;
            case CROSSBOW -> 1.3;
            default -> 1;
        };
    }

    public String getSourceImage() {
        return switch (this) {
            case HANDS -> "Hands.png";
            case SHORT_SWORD -> "ShortSword.png";
            case SWORD -> "Sword.png";
            case CROSSBOW -> "Crossbow.png";
            case KATANA -> "Katana.png";
            case STICK -> "Stick.png";
        };
    }


    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getName() {
        return switch (this) {
            case HANDS -> "Hands";
            case SHORT_SWORD -> "Short Sword";
            case SWORD -> "Sword";
            case CROSSBOW -> "Crossbow";
            case KATANA -> "Katana";
            case STICK -> "Stick";
        };
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
        return true;
    }

    @Override
    public boolean isArmor() {
        return false;
    }

    @Override
    public boolean isWeapon() {
        return true;
    }

    @Override
    public boolean isUsable() {
        return false;
    }


}
