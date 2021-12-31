package jakubwiraszka.items;

public enum Armor implements Item {
    CLOTHES,
    LIGHT_ARMOR,
    HEAVY_ARMOR;

    public double getDamageMultiplier() {
        return switch (this) {
            case HEAVY_ARMOR -> 0.7;
            case LIGHT_ARMOR -> 0.85;
            default -> 1;
        };
    }

    public String getSourceImage() {
        return switch (this) {
            case CLOTHES -> "Clothes.png";
            case LIGHT_ARMOR -> "LightArmor.png";
            case HEAVY_ARMOR -> "HeavyArmor.png";
        };
    }


    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getName() {
        return switch (this) {
            case CLOTHES -> "Clothes";
            case LIGHT_ARMOR -> "Light Armor";
            case HEAVY_ARMOR -> "Heavy Armor";
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
        return true;
    }

    @Override
    public boolean isWeapon() {
        return false;
    }

    @Override
    public boolean isUsable() {
        return false;
    }


}
