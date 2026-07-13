package org.example;

import java.util.Random;
import java.util.concurrent.*;

public class ParallelEstimator implements PiEstimator {

    private final int numThreads;

    public ParallelEstimator(int numThreads) {
        this.numThreads = numThreads;
    }

    static class PiTask implements Callable<Long> {

        private final long samples;

        PiTask(long samples) {
            this.samples = samples;
        }

        @Override
        public Long call() {
            Random rand = new Random();
            long inside = 0;
            for (long i = 0; i < samples; i++) {
                double x = rand.nextDouble();
                double y = rand.nextDouble();
                if (x * x + y * y <= 1.0) {
                    inside++;
                }
            }
            return inside;
        }
    }

    @Override
    public double estimatePi(long totalPoints) throws InterruptedException, ExecutionException {
        long pointsPerThread = totalPoints / numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CompletionService<Long> completion = new ExecutorCompletionService<>(executor);

        long start = System.nanoTime();

        for (int i = 0; i < numThreads; i++) {
            completion.submit(new PiTask(pointsPerThread));
        }

        long totalInside = 0;
        for (int i = 0; i < numThreads; i++) {
            totalInside += completion.take().get();
        }

        long end = System.nanoTime();
        executor.shutdown();

        double pi = 4.0 * totalInside / totalPoints;
        double time = (end - start) / 1e9;

        System.out.printf("Parallel: Pi ≈ %.6f | Time = %.3f s%n", pi, time);
        return time;
    }
}
