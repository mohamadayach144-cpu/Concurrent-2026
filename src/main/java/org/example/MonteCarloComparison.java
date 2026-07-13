package org.example;

import org.knowm.xchart.*;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MonteCarloComparison {

    public static void main(String[] args) throws Exception {
        MonteCarloVisualizer.visualize(10000);
        runComparison();
    }

    public static void runComparison() throws Exception {
        int[] sampleSizes = MonteCarloConfig.SAMPLE_SIZES;
        int cores = MonteCarloConfig.NUM_THREADS;

        List<Integer> xData = new ArrayList<>();
        List<Double> sequentialTimes = new ArrayList<>();
        List<Double> parallelTimes = new ArrayList<>();
        List<Double> speedUps = new ArrayList<>();

        System.out.println("============================================");
        System.out.println("      Monte Carlo \u03c0 Estimation ");
        System.out.println("============================================");
        System.out.printf("Using %d CPU cores%n%n", cores);

        System.out.printf("%-15s %-20s %-20s %-15s%n", "Samples", "Sequential Time (s)", "Parallel Time (s)", "Speed-Up");
        System.out.println("---------------------------------------------------------------------------------------");

        double maxSpeedUp = 0;
        int maxSpeedUpSample = 0;

        for (int points : sampleSizes) {
            PiEstimator seq = new SequentialEstimator();
            double seqTime = seq.estimatePi(points);

            PiEstimator par = new ParallelEstimator(cores);
            double parTime = par.estimatePi(points);

            double speedUp = seqTime / parTime;
            if (speedUp > maxSpeedUp) {
                maxSpeedUp = speedUp;
                maxSpeedUpSample = points;
            }

            xData.add(points / 1_000_000);
            sequentialTimes.add(seqTime);
            parallelTimes.add(parTime);
            speedUps.add(speedUp);

            System.out.printf("%-15s %-20.4f %-20.4f %-15.2f%n", String.format("%,d", points), seqTime, parTime, speedUp);
        }

        System.out.println("⚙️  Summary Analysis:");
        System.out.printf("- Max speed-up achieved: %.2fx (at %,d samples)%n", maxSpeedUp, maxSpeedUpSample);
        System.out.println("- Parallel computation is significantly faster for large-scale simulations.");
        System.out.println("- The benefit increases as input size grows.\n");

        displayChart(xData, sequentialTimes, parallelTimes, cores);
    }

    public static void displayChart(List<Integer> xData, List<Double> seq, List<Double> par, int cores) {
        XYChart chart = new XYChartBuilder()
                .width(800).height(600)
                .title("Monte Carlo \u03c0 Estimation Performance")
                .xAxisTitle("Sample Size (Millions)")
                .yAxisTitle("Execution Time (s)")
                .build();

        chart.getStyler().setMarkerSize(6);
        chart.getStyler().setLegendPosition(XYStyler.LegendPosition.InsideNE);

        XYSeries seqSeries = chart.addSeries("Sequential", xData, seq);
        seqSeries.setMarker(SeriesMarkers.CIRCLE);

        XYSeries parSeries = chart.addSeries("Parallel (" + cores + " threads)", xData, par);
        parSeries.setMarker(SeriesMarkers.SQUARE);

        // Docker-friendly: Saves chart as a PNG file instead of displaying a GUI popup window
        try {
            System.out.println("💾 Saving performance chart to 'performance_chart.png'...");
            BitmapEncoder.saveBitmap(chart, "./performance_chart", BitmapEncoder.BitmapFormat.PNG);
            System.out.println("✅ Chart saved successfully!");
        } catch (IOException e) {
            System.err.println("❌ Failed to save the chart image: " + e.getMessage());
        }
    }
}