package jakubwiraszka.gamefiles;

public class Enemy implements LocationContent {
    private String id;

    String name;
    private final Statistics statistics;
    private boolean alive;
    private Position position;

    public Enemy(String name, Statistics statistics) {
        this.name = name;
        this.statistics = statistics;
        this.alive = true;
        this.setPosition(new Position(-1, -1));
    }

    public void changeHealth(double value) {
        statistics.setHealth(statistics.getHealthValue() + value);
    }

    public void changePower(int value) {
        statistics.setPower(statistics.getPower() + value);
    }

    public void changeAgility(int value) {
        statistics.setAgility(statistics.getAgility() + value);
    }

    public String getName() {
        return name;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String save() {
        return (getName() + "\n"
                + statistics.getHealthValue() + " " + statistics.getPower() + " " + statistics.getAgility()) +"\n" +
                isAlive() + "\n" +
                position.getX() + " " + position.getY() + "\n";
    }

    @Override
    public String toString() {
        return (getId() + ": \n"
                + "Health: " + getStatistics().getHealthValue() + " Power: " + getStatistics().getPower() + " Agility: " + getStatistics().getAgility());
    }

    @Override
    public boolean isEnemy() {
        return true;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }
}

