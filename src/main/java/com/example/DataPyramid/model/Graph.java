package com.example.DataPyramid.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.util.Map;


import java.util.Arrays;

/** Simple class to manage the implementation of the various graphs in the application */
public class Graph {
    private final GraphDAO graphDAO; // Assuming GraphDAO is properly set up to handle database operations.

    /** Current User's email */
    private String userEmail;

    private Label noDataLabel = new Label("Start tracking app usage to see your data here!");

    public Graph(String defaultGraph, Pane pane, GraphDAO graphDAO, String email) {

        noDataLabel.getStyleClass().add("error-graph");

        this.graphDAO = graphDAO;
        this.userEmail = email;

        switch (defaultGraph)
        {
            case "b":
                showBarChart(pane, userEmail);
                break;
            case "c":
                showColumnChart(pane, userEmail);
                break;
            case "p":
                showPieChart(pane, userEmail);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + defaultGraph);
        }
    }

    /**
     * Removes all visible graphs by removing everything in the pane where the graphs are located.
     * @param pane The pane where the graphs are located.
     */
    private void removeGraph(Pane pane) {
        pane.getChildren().clear();
    }

    /**
     * Creates a new Bar chart and displays it in the provided pane
     * @param pane The pane to display the bar graph in
     */
    public void showBarChart(Pane pane, String userEmail) {
        removeGraph(pane);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("App");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Time Used (in minutes)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("App Usage");

        Map<String, Integer> data = graphDAO.getAppTimeUsageByUser(userEmail);
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if(entry.getValue() != 0) { series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())); }
        }

        if(!series.getData().isEmpty())
        {
            barChart.getData().add(series);
            pane.getChildren().add(barChart);
        }
        else { pane.getChildren().add(noDataLabel); }
    }

    /**
     * Creates a new Stacked Bar Chart and displays it in the provided pane
     * @param pane The pane to display the graph in.
     */
    public void showColumnChart(Pane pane, String userEmail) {
        removeGraph(pane);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(
                Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        ));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time Spent");

        StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        stackedBarChart.setTitle("Weekly App Usage Overview");

        Map<String, Map<String, Integer>> data = graphDAO.getWeeklyAppUsageByUser(userEmail);
        for (Map.Entry<String, Map<String, Integer>> appEntry : data.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(appEntry.getKey());
            Map<String, Integer> weeklyData = appEntry.getValue();
            for (Map.Entry<String, Integer> dayEntry : weeklyData.entrySet()) {
                series.getData().add(new XYChart.Data<>(dayEntry.getKey(), dayEntry.getValue()));
            }
            stackedBarChart.getData().add(series);
        }

        pane.getChildren().add(stackedBarChart);
    }

    /**
     * Creates a new pie chart and displays it in the provided pane.
     * @param pane The pane to display the graph in.
     */
    public void showPieChart(Pane pane, String userEmail) {
        removeGraph(pane); // Clear any existing graphs from the pane

        Map<String, Integer> appUsageData = graphDAO.getAppUsageByUser(userEmail);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        if (appUsageData.isEmpty()) {
            pieChartData.add(new PieChart.Data("No Data Available", 1));
        } else {
            int totalUsage = appUsageData.values().stream().mapToInt(Integer::intValue).sum();

            if (totalUsage == 0) {
                // All data points are zero but we still display them
                for (Map.Entry<String, Integer> entry : appUsageData.entrySet()) {
                    pieChartData.add(new PieChart.Data(entry.getKey() + " (0 Usage)", 1)); // Show zero usage distinctly
                }
            } else {
                // There are actual usage values to display
                for (Map.Entry<String, Integer> entry : appUsageData.entrySet()) {
                    if (entry.getValue() > 0) {
                        pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                    } else {
                        // Handle zero values explicitly
                        pieChartData.add(new PieChart.Data(entry.getKey() + " (0 Usage)", 1));
                    }
                }
            }
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("App Usage Data");
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        pane.getChildren().add(pieChart);
    }
}