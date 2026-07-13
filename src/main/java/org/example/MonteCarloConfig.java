package org.example;

public class MonteCarloConfig {
    public static final int[] SAMPLE_SIZES = {
            10_000_000, 25_000_000, 50_000_000, 75_000_000, 100_000_000
    };

    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
}
