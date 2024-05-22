package com.example.DataPyramid.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.HashMap;
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
            case "t":
                showColumnChartByType(pane, userEmail);
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
        removeGraph(pane); // Clear the pane from any previous graphs or messages

        Map<String, Integer> data = graphDAO.getAppTimeUsageByUser(userEmail);

        if (data.isEmpty()) {
            // Display a message if no data is available
            Label message = new Label("Start tracking app usage to see your data here!");
            message.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            message.setWrapText(true);
            pane.getChildren().add(message); // Add the message to the pane
        } else {
            // Prepare the axes for the bar chart
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("App");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Time Used (in minutes)");

            // Create the bar chart without a legend
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setLegendVisible(false); // Hide the legend
            barChart.setTitle("App Usage");

            // Create a series for the chart and add data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChart.getData().add(series);
            pane.getChildren().add(barChart); // Add the chart to the pane
        }
    }

    /**
     * Creates a new Stacked Bar Chart and displays it in the provided pane
     * @param pane The pane to display the graph in.
     */

    public void showColumnChart(Pane pane, String userEmail) {
        removeGraph(pane); // Clear the pane from any previous graphs or messages

        Map<String, Map<String, Integer>> data = graphDAO.getWeeklyAppUsageByUser(userEmail);

        if (data.isEmpty()) {
            // If no data, display a message label instead of a chart
            Label message = new Label("Start tracking app usage to see your data here!");
            message.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            message.setWrapText(true);
            pane.getChildren().add(message); // Add the message to the pane
        } else {
            // Prepare the axes for the column chart
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setCategories(FXCollections.observableArrayList(
                    Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            ));
            xAxis.setLabel("Day");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Time Spent");

            StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
            stackedBarChart.setTitle("Weekly App Usage Overview");

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
    }

    /**
     * Creates a new pie chart and displays it in the provided pane.
     * @param pane The pane to display the graph in.
     */
    public void showPieChart(Pane pane, String userEmail) {
        removeGraph(pane); // Clear any existing graphs from the pane

        Map<String, Integer> appUsageData = graphDAO.getAppUsageByUser(userEmail);
        if (appUsageData.isEmpty()) {
            // Display a message if no apps are being tracked
            Label message = new Label("Start tracking app usage to see your data here!");
            message.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            message.setWrapText(true);
            pane.getChildren().add(message);
            return; // Exit the method after adding the message
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int totalUsage = appUsageData.values().stream().mapToInt(Integer::intValue).sum();

        if (totalUsage == 0) {
            // If all tracked apps have zero usage, display them with "(0 Usage)"
            for (Map.Entry<String, Integer> entry : appUsageData.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey() + " (0 Usage)", 1));
            }
        } else {
            // Display normal usage values
            for (Map.Entry<String, Integer> entry : appUsageData.entrySet()) {
                if (entry.getValue() > 0) {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                } else {
                    pieChartData.add(new PieChart.Data(entry.getKey() + " (0 Usage)", 1));
                }
            }
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("App Usage Data");
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(180);

        pane.getChildren().add(pieChart); // Add the pie chart to the pane only if there is data
    }

    public void showColumnChartByType(Pane pane, String userEmail) {
        removeGraph(pane);
        Map<String, Map<String, Integer>> data = graphDAO.getTimeUsageByTypeAndApp(userEmail);

        if (data.isEmpty()) {
            // Display a message if no data is available
            Label message = new Label("Start tracking app usage to see your data here!");
            message.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            message.setWrapText(true);
            pane.getChildren().add(message);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setCategories(FXCollections.observableArrayList("Game", "Productive", "Internet", "Entertainment", "Social", "Other"));
            xAxis.setLabel("App Type");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Time Spent");

            StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
            stackedBarChart.setTitle("App Usage by Type");

            // Create a map to keep track of series by app name
            Map<String, XYChart.Series<String, Number>> seriesByAppName = new HashMap<>();

            // Populate the series data
            for (Map.Entry<String, Map<String, Integer>> typeEntry : data.entrySet()) {
                String type = typeEntry.getKey();
                Map<String, Integer> apps = typeEntry.getValue();

                for (Map.Entry<String, Integer> appEntry : apps.entrySet()) {
                    String appName = appEntry.getKey();
                    Integer timeSpent = appEntry.getValue();
                    seriesByAppName.computeIfAbsent(appName, k -> new XYChart.Series<>()).getData().add(new XYChart.Data<>(type, timeSpent));
                }
            }

            // Add each series to the chart, setting the name to the app name for legend
            for (Map.Entry<String, XYChart.Series<String, Number>> seriesEntry : seriesByAppName.entrySet()) {
                XYChart.Series<String, Number> series = seriesEntry.getValue();
                series.setName(seriesEntry.getKey());
                stackedBarChart.getData().add(series);
            }

            pane.getChildren().add(stackedBarChart);
        }
    }
}