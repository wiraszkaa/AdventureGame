package jakubwiraszka.fight;

public class StrongAttack implements Attack {
    private final double power;
    private final double hitChance;
    private final int charge;

    public StrongAttack() {
        power = 1;
        hitChance = 0.5;
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
        return "Strong Attack";
    }
}
