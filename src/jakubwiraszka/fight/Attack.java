package jakubwiraszka.fight;

import jakubwiraszka.Randomize;
import jakubwiraszka.gamefiles.Enemy;

import java.util.Random;

public interface Attack {
    double getPower();
    double getHitChance();
    int getCharge();

    default double attack(Enemy attacker, Enemy defender) {
        Random random = new Random();
        double hitChance = getHitChance() + (double) (attacker.getStatistics().getAgility() - defender.getStatistics().getAgility()) / 50.0;
        boolean hit = Randomize.randomize(hitChance, 100);
        if(hit) {
            double attackValue = attacker.getStatistics().getPower() * getPower();
            int heroDeviation = Math.max((int) (attackValue / 5), 1);
            attackValue += random.nextInt(heroDeviation * 2) - heroDeviation;
            defender.changeHealth(-attackValue);
            return attackValue;
        } else {
            return -1;
        }
    }

    String toString();
}
