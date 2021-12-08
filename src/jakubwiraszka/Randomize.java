package jakubwiraszka;

import java.util.Random;

public class Randomize {

    public static boolean randomize(double chance, int iterations) {
        Random random = new Random();
        int counter = 0;
        for (int i = 0; i < iterations; i++) {
            if (random.nextDouble() <= chance) {
                counter++;
            }
        }
        return counter > (iterations / 2);
    }
}
