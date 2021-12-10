package jakubwiraszka.fight;

public class ChargedAttack implements Attack {
    private final double power;
    private final double hitChance;
    private final int charge;

    public ChargedAttack() {
        power = 2.2;
        hitChance = 0.6;
        charge = 2;
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
        return "Charged Attack";
    }
}
