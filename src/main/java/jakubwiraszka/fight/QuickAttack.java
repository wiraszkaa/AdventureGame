package jakubwiraszka.fight;

public class QuickAttack implements Attack {
    private final double power;
    private final double hitChance;
    private final int charge;

    public QuickAttack() {
        power = 0.6;
        hitChance = 0.8;
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
        return "Quick Attack";
    }
}
