package org.example;
import java.util.Random;

public class SequentialEstimator implements PiEstimator {

    @Override
    public double estimatePi(long totalPoints) {
        Random rand = new Random();
        long inside = 0;

        long start = System.nanoTime();
        for (long i = 0; i < totalPoints; i++) {
            double x = rand.nextDouble();
            double y = rand.nextDouble();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }
        long end = System.nanoTime();

        double pi = 4.0 * inside / totalPoints;
        double time = (end - start) / 1e9;

        System.out.printf("Sequential: Pi ≈ %.6f | Time = %.3f s%n", pi, time);
        return time;
    }
}
