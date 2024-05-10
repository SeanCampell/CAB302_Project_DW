package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.App;
import com.example.DataPyramid.apptrack.AppType;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.Graph;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

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
    private TextField appLimitField;
    @FXML
    private CheckBox isTrack;
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
    @FXML
    private Label firstAppLabel;
    @FXML
    private Label secondAppLabel;
    @FXML
    private Label thirdAppLabel;
    @FXML
    private Label firstTimeLabel;
    @FXML
    private Label secondTimeLabel;
    @FXML
    private Label thirdTimeLabel;

    @FXML
    private ListView<String> processListView;
    @FXML
    private ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();

    private ToggleGroup toggleGroup;
    private User currentUser;
    private DatabaseInitializer dbConnection;
    private String defaultGraph = "c";
    private Graph graphsHandler;

    private App[] topApps;
    private List<String> processes;

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
        syncProcesses();
        typeChoiceBox.setItems(
                FXCollections.observableArrayList("Other", "Game", "Productive", "Internet", "Entertainment"));
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeLabel();
    }

    private void updateWelcomeLabel() {
        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFirstname());
            updateTopApps();
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
        updateTopApps();
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
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiListener.getWindowWidth(),
                HelloApplication.uiListener.getWindowHeight());
        stage.setScene(scene);
    }

    @FXML
    protected void onAddAppButtonClick() {
        errorLabel.setText("");

        if (typeChoiceBox.getSelectionModel().getSelectedItem().isEmpty() || appLimitField.getText().isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }

        String appName = processListView.getSelectionModel().getSelectedItem();
        AppType appType = AppType.valueOf(typeChoiceBox.getValue());
        int appLimit = parseInt(appLimitField.getText());
        boolean isTracking = isTrack.isSelected();

        App existApp = dbConnection.getAppByName(appName, currentUser);
        if (existApp != null) {
            errorLabel.setText("Application is already added");
            return;
        }
        App newApp = new App(appName, appType, appLimit, isTracking);

        boolean success = dbConnection.saveApp(newApp, currentUser);

        if (success) {
            typeChoiceBox.getSelectionModel().clearSelection();
            appLimitField.clear();
            isTrack.setSelected(false);

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

    private void updateTopApps() {
        topApps = dbConnection.mostUsedApps(currentUser.getEmail());
        if(topApps != null){
            if (topApps[0] != null) {
                firstAppLabel.setText(topApps[0].getName());
                firstTimeLabel.setText(Integer.toString(topApps[0].getTimeUse()) + " Minutes");
            }
            if (topApps[1] != null) {
                secondAppLabel.setText(topApps[1].getName());
                secondTimeLabel.setText(topApps[1].getTimeUse() + " Minutes");
            }
            if (topApps[2] != null) {
                thirdAppLabel.setText(topApps[2].getName());
                thirdTimeLabel.setText(topApps[2].getTimeUse() + " Minutes");
            }
        }
    }

    public static List<String> listProcesses() {
        List<String> processes = new ArrayList<String>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe /svc /fo csv /nh");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {

                if (!line.trim().equals("")) {
                    // keep only the process name
                    line = line.substring(1);
                    if (!processes.contains(line.substring(0, line.indexOf('"')))){
                        processes.add(line.substring(0, line.indexOf('"')));
                    }
                }

            }
            input.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return processes;
    }

    private void syncProcesses() {
        String currentProcess = processListView.getSelectionModel().getSelectedItem();
        processListView.getItems().clear();
        processes = listProcesses();
        boolean hasProcess = !processes.isEmpty();
        if (hasProcess) {
            processListView.getItems().addAll(processes);
            String nextProcess = processes.contains(currentProcess) ? currentProcess : processes.get(0);
            processListView.getSelectionModel().select(nextProcess);
        }
        if(topApps != null) {
            if (topApps[0] != null) {
                firstAppLabel.setText(topApps[0].getName());
                firstTimeLabel.setText(Integer.toString(topApps[0].getTimeUse()) + " Minutes");
            }
            if (topApps[1] != null) {
                secondAppLabel.setText(topApps[1].getName());
                secondTimeLabel.setText(Integer.toString(topApps[1].getTimeUse()) + " Minutes");
            }
            if (topApps[2] != null) {
                thirdAppLabel.setText(topApps[2].getName());
                thirdTimeLabel.setText(Integer.toString(topApps[2].getTimeUse()) + " Minutes");
            }
        }
    }
}
