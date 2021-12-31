package jakubwiraszka.gamefiles;

public class Statistics {
    private double health;
    private int power;
    private int agility;

    public Statistics(double health, int power, int agility) {
        this.health = health;
        this.power = power;
        this.agility = agility;
    }

    public double getHealth() {
        return health;
    }

    void setHealth(double health) {
        this.health = health;
    }

    public int getPower() {
        return power;
    }


    void setPower(int power) {
        this.power = power;
    }

    public int getAgility() {
        return agility;
    }

    void setAgility(int agility) {
        this.agility = agility;
    }

    @Override
    public String toString() {
        return "Health = " + getHealth() + ", Power = " + getPower() + ", Agility = " + getAgility();
    }
}

