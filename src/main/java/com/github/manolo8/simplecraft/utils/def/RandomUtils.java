package com.github.manolo8.simplecraft.utils.def;

import java.util.Random;

public class RandomUtils {

    public static double nextDouble(Random random, int times) {
        double d = random.nextDouble();

        for (int i = 1; i < times; i++)
            d *= (random.nextDouble() / 2 + 0.5);

        return d;
    }

    public static int randomBetween(Random random, int max, int min) {
        int bound = max - min;

        boolean negative = bound < 0;

        int rnd = random.nextInt(negative ? -bound : bound);

        return min + (negative ? -rnd : rnd);
    }

    public static double nextDouble(Random random, int times, int bound, int min) {
        return (nextDouble(random, times) * (bound - min)) + min;
    }

    public static int nextInt(Random random, int bound, int min) {
        return random.nextInt(bound - min) + min;
    }
}
