package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.App;
import com.example.DataPyramid.apptrack.AppType;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.Graph;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import com.example.DataPyramid.controller.SignUpController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;
import java.util.Arrays;

public class MainController {

    // ----- NAVIGATION BAR BUTTONS ------
    @FXML
    private ToggleButton homeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ToggleButton insightButton;

    // ----- PAGE CONTENT ------
    @FXML
    private HBox homeContent;
    @FXML
    private VBox insightsContent;
    @FXML
    private VBox timeLimitsContent;
    @FXML
    private VBox addProgramContent;

    // ---- OTHER JAVAFX NODES ------
    @FXML
    private ToggleButton timelimitButton;
    @FXML
    private ToggleButton newAppButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField appNameField;
    @FXML
    private TextField appTypeField;
    @FXML
    private TextField appLimitField;
    @FXML
    private Label errorLabel;

    // ----- GRAPH TESTING ------
    @FXML
    private HBox graphLocation;
    @FXML
    private Button barChartButton;
    @FXML
    private Button columnChartButton;
    @FXML
    private Button pieChartButton;

    // ---- INTERNAL VARIABLES ------
    private ToggleGroup toggleGroup;
    private User currentUser;
    private DatabaseInitializer dbConnection;
    private String defaultGraph = "c";
    private Graph graphsHandler;

    public void initialize() {
        toggleGroup = new ToggleGroup();
        homeButton.setToggleGroup(toggleGroup);
        insightButton.setToggleGroup(toggleGroup);
        timelimitButton.setToggleGroup(toggleGroup);
        newAppButton.setToggleGroup(toggleGroup);

        homeButton.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                clearActiveButtonStyle();
            } else {
                ((ToggleButton) newValue).getStyleClass().add("active-nav-button");
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeLabel();
    }

    private void updateWelcomeLabel() {
        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFirstname());
        }
    }

    public MainController() { dbConnection = new DatabaseInitializer(); }


    @FXML
    protected void onHomeButtonClick() throws IOException {
        clearActiveButtonStyle();
        homeButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(true);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(false);
        addProgramContent.setVisible(false);
    }

    // ---- INSIGHTS MENU ----
    @FXML
    protected void onInsightsButtonClick() throws IOException {
        clearActiveButtonStyle();
        insightButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(true);
        timeLimitsContent.setVisible(false);
        addProgramContent.setVisible(false);

        graphsHandler = new Graph(defaultGraph, graphLocation);
    }

    @FXML
    protected void onBarChartButtonClick() throws IOException { graphsHandler.showBarChart(graphLocation); }
    @FXML
    protected void onColumnChartButtonClick() throws IOException { graphsHandler.showColumnChart(graphLocation); }
    @FXML
    protected void onPieChartButtonClick() throws IOException { graphsHandler.showPieChart(graphLocation); }

    @FXML
    protected void onTimeLimitsButtonClick() throws IOException {
        clearActiveButtonStyle();
        timelimitButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(true);
        addProgramContent.setVisible(false);
    }

    private void clearActiveButtonStyle() {
        homeButton.getStyleClass().remove("active-nav-button");
        insightButton.getStyleClass().remove("active-nav-button");
        timelimitButton.getStyleClass().remove("active-nav-button");
        newAppButton.getStyleClass().remove("active-nav-button");
    }


    @FXML
    protected void onLogoutButtonClick() throws IOException {
        setCurrentUser(null);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onAddAppButtonClick() {
        errorLabel.setText("");

        if (appNameField.getText().isEmpty() || appTypeField.getText().isEmpty() ||
                appLimitField.getText().isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }

        String appName = appNameField.getText();
        AppType appType = AppType.valueOf(appTypeField.getText());
        int appLimit = Integer.parseInt(appLimitField.getText());

        App existApp = dbConnection.getAppByName(appName, currentUser);
        if (existApp != null) {
            errorLabel.setText("Application is already being tracked");
            return;
        }
        App newApp = new App(appName, appType, appLimit);

        boolean success = dbConnection.saveApp(newApp, currentUser);

        if (success) {
            appNameField.clear();
            appTypeField.clear();
            appLimitField.clear();

            showFlashMessage("Add Program Success", "You have successfully added a program to track");
        } else {
            errorLabel.setText("Error adding program. Please try again.");
        }
    }

    private void showFlashMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onNewAppButtonClick() {
        clearActiveButtonStyle();
        newAppButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(false);
        addProgramContent.setVisible(true);
    }
}
