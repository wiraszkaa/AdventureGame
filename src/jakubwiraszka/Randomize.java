package jakubwiraszka;

import java.util.Random;

public interface Randomize {

    default boolean randomize(double chance, int iterations) {
        Random random = new Random();
        int counter = 0;
        for (int i = 0; i < iterations; i++) {
            if (random.nextDouble() <= chance) {
                counter++;
            }
        }
        if(counter > (iterations / 2)) {
            return true;
        } else {
            return false;
        }
    }
}
