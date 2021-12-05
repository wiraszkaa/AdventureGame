package jakubwiraszka.fight;

public class Dodge implements Attack {
    private double power;
    private double hitChance;
    private int charge;

    public Dodge() {
        power = 0;
        hitChance = 1;
        charge = 1;
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public double getHitChance() {
        return hitChance;
    }

    @Override
    public int getCharge() {
        return charge;
    }

    @Override
    public String toString() {
        return "Dodge";
    }
}
