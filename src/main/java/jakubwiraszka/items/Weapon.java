package jakubwiraszka.items;

public enum Weapon implements Item {
    HANDS,
    CROSSBOW,
    KATANA,
    SHORT_SWORD,
    SWORD;

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
        };
    }
}
