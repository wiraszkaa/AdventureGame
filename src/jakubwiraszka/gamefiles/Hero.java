package jakubwiraszka.gamefiles;

public class Hero extends Enemy {
    private int maxHealth;
    private final Level level;

    public Hero(String name, Statistics statistics) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealthValue();
        level = new Level(1);
    }

    public Hero(String name, Statistics statistics, int level) {
        super(name, statistics);
        maxHealth = super.getStatistics().getHealthValue();
        this.level = new Level(level);
    }

    @Override
    public String save() {
        return (super.save() + getMaxHealth() + "\n" +
                getLevel().getCurrentLevel() + "\n" +
                getLevel().getCurrentExperience().intValue() + "\n" +
                getLevel().getPointsToSpend().intValue());
    }

    @Override
    public String toString() {
        return (getName() + ": \n"
                + "Health: " + super.getStatistics().getHealthValue() + "/" + getMaxHealth() + " Power: " + getStatistics().getPowerValue() + " Agility: " + getStatistics().getAgilityValue());
    }

    public String statsToString() {
        return ("Health: " + super.getStatistics().getHealthValue() + "/" + getMaxHealth() + " Power: " + getStatistics().getPowerValue() + " Agility: " + getStatistics().getAgilityValue());
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void addMaxHealth(int value) {
        this.maxHealth += value;
    }

    public Level getLevel() {
        return level;
    }

    public void setName(String name) {
        super.name = name;
    }
}
