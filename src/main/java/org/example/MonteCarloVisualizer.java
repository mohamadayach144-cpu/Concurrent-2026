package org.example;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.style.markers.None;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloVisualizer {

    public static void visualize(int totalPoints) {
        List<Double> insideX = new ArrayList<>();
        List<Double> insideY = new ArrayList<>();
        List<Double> outsideX = new ArrayList<>();
        List<Double> outsideY = new ArrayList<>();

        Random random = new Random();
        int inside = 0;

        for (int i = 0; i < totalPoints; i++) {
            double x = 2 * random.nextDouble() - 1;
            double y = 2 * random.nextDouble() - 1;
            if (x * x + y * y <= 1) {
                inside++;
                insideX.add(x);
                insideY.add(y);
            } else {
                outsideX.add(x);
                outsideY.add(y);
            }
        }

        double piEstimate = 4.0 * inside / totalPoints;

        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(600)
                .title(String.format("n = %,d | inside = %,d | π ≈ %.4f", totalPoints, inside, piEstimate))
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(2);

        XYSeries in = chart.addSeries("Inside", insideX, insideY);
        in.setMarkerColor(new Color(0, 120, 0));
        in.setMarker(SeriesMarkers.CIRCLE);

        XYSeries out = chart.addSeries("Outside", outsideX, outsideY);
        out.setMarkerColor(Color.BLUE);
        out.setMarker(SeriesMarkers.CIRCLE);

        // Draw unit circle outline
        List<Double> circleX = new ArrayList<>();
        List<Double> circleY = new ArrayList<>();
        for (double angle = 0; angle <= 2 * Math.PI; angle += 0.01) {
            circleX.add(Math.cos(angle));
            circleY.add(Math.sin(angle));
        }

        XYSeries circle = chart.addSeries("Circle", circleX, circleY);
        circle.setLineColor(Color.RED);
        circle.setLineStyle(new BasicStroke(2.5f));

        new SwingWrapper<>(chart).displayChart();
    }
}
