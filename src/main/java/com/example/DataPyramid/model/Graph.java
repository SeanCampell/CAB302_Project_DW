package com.example.DataPyramid.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;

import java.util.Arrays;

/** Simple class to manage the implementation of the various graphs in the application */
public class Graph {

    public Graph(String defaultGraph, HBox pane)
    {
        switch (defaultGraph)
        {
            case "b":
                showBarChart(pane);
                break;
            case "c":
                showColumnChart(pane);
                break;
            case "p":
                showPieChart(pane);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + defaultGraph);
        }
    }

    /**
     * Removes all visible graphs by removing everything in the HBox where the graphs are located.
     * @param pane The HBox where the graphs are located.
     */
    private void removeGraph(HBox pane) {
        pane.getChildren().clear();
    }

    public void showBarChart(HBox pane) {
        removeGraph(pane);

        //TODO: Add the ability to fetch data and remove the temporary graph
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("product");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("quantity");

        BarChart barchart = new BarChart<>(xAxis, yAxis);

        XYChart.Series data = new XYChart.Series();
        data.setName("Products sold");

        data.getData().add(new XYChart.Data("Product A", 3000));
        data.getData().add(new XYChart.Data("Product B", 1500));
        data.getData().add(new XYChart.Data("Product C", 3000));

        barchart.getData().add(data);

        pane.getChildren().add(barchart);
    }

    /**
     * Creates a new Stacked Bar Chart and displays it in the provided HBox
     * @param pane The HBox to display the graph in.
     */
    public void showColumnChart(HBox pane)
    {
        //Remove the existing graphs
        removeGraph(pane);

        //Create the Axis for the chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        ));
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time Spent");

        //Create the chart
        StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        stackedBarChart.setTitle("Sample Weekly Overview");

        //Prepare the data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Program 1");
        series1.getData().add(new XYChart.Data<>("Monday", 107));
        series1.getData().add(new XYChart.Data<>("Tuesday", 57));
        series1.getData().add(new XYChart.Data<>("Wednesday", 10));
        series1.getData().add(new XYChart.Data<>("Thursday", 80));
        series1.getData().add(new XYChart.Data<>("Friday", 107));
        series1.getData().add(new XYChart.Data<>("Saturday", 57));
        series1.getData().add(new XYChart.Data<>("Sunday", 10));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Program 2");
        series2.getData().add(new XYChart.Data<>("Monday", 17));
        series2.getData().add(new XYChart.Data<>("Tuesday", 67));
        series2.getData().add(new XYChart.Data<>("Wednesday", 37));
        series2.getData().add(new XYChart.Data<>("Thursday", 40));
        series2.getData().add(new XYChart.Data<>("Friday", 67));
        series2.getData().add(new XYChart.Data<>("Saturday", 37));
        series2.getData().add(new XYChart.Data<>("Sunday", 40));

        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.setName("Program 3");
        series3.getData().add(new XYChart.Data<>("Monday", 107));
        series3.getData().add(new XYChart.Data<>("Tuesday", 57));
        series3.getData().add(new XYChart.Data<>("Wednesday", 10));
        series3.getData().add(new XYChart.Data<>("Thursday", 80));
        series3.getData().add(new XYChart.Data<>("Friday", 107));
        series3.getData().add(new XYChart.Data<>("Saturday", 57));
        series3.getData().add(new XYChart.Data<>("Sunday", 10));

        XYChart.Series<String, Number> series4 = new XYChart.Series<>();
        series4.setName("Program 4");
        series4.getData().add(new XYChart.Data<>("Monday", 17));
        series4.getData().add(new XYChart.Data<>("Tuesday", 67));
        series4.getData().add(new XYChart.Data<>("Wednesday", 37));
        series4.getData().add(new XYChart.Data<>("Thursday", 40));
        series4.getData().add(new XYChart.Data<>("Friday", 67));
        series4.getData().add(new XYChart.Data<>("Saturday", 37));
        series4.getData().add(new XYChart.Data<>("Sunday", 40));

        //Add data to the stacked chart
        stackedBarChart.getData().addAll(series1, series2, series3, series4);

        pane.getChildren().add(stackedBarChart);
    }

    public void showPieChart(HBox pane)
    {
        //Clear existing graphs from the HBox
        removeGraph(pane);

        //TODO: Add the ability to fetch data and remove the temporary graph
        //Create a new Pie Chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Program 1", 13),
                new PieChart.Data("Program 2", 25),
                new PieChart.Data("Program 3", 10),
                new PieChart.Data("Program 4", 22)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Sample Program Data");
        pieChart.setLabelLineLength(50);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(180);

        pane.getChildren().add(pieChart);
    }

}
