package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.*;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.Graph;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import com.example.DataPyramid.model.GraphDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import java.lang.Process;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.lang.Integer.parseInt;


public class MainController {

    // ----- NAVIGATION BAR
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
    private final GraphDAO graphDAO;

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
    private Label totalTimeLabel;

    @FXML
    private VBox programList;
    @FXML
    private VBox rightNavbar;
    @FXML
    private VBox timeLimitPrograms;


    @FXML
    private ListView<String> processListView;
    @FXML
    private ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();

    private ToggleGroup toggleGroup;
    private User currentUser;
    private DatabaseInitializer dbConnection;
    private final String defaultGraph = "c";
    private Graph graphsHandler;

    private App[] topApps;
    private List<String> processes;
    private TimeTracking timeTracker;
    private int totalScreenTime;
    private static final int REFRESH_INTERVAL_SECONDS = 60;



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
        dbConnection = new DatabaseInitializer();
        typeChoiceBox.setItems(
                FXCollections.observableArrayList("Other", "Game", "Productive", "Internet", "Entertainment", "Social"));
    }


    public void setCurrentUser(User user) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        this.currentUser = user;
        updateWelcomeLabel();
        initializeOutsideInitialize();
    }

    private void updateWelcomeLabel() {
        if (currentUser != null) {
            welcomeLabel.setText("Hello, " + currentUser.getFirstname());
        }
    }


    public void initializeOutsideInitialize() {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        timeTracker = new TimeTracking(dbConnection, currentUser, "", this);
        totalScreenTime = dbConnection.loadTotalScreenTime(currentUser);
        displayTotalTime(totalTimeLabel, totalScreenTime);
        loadTimeSpentList(currentUser);
        List<String> topThreeAppNames = dbConnection.getFirstThreeProgramNames(currentUser);
        List<Integer> topThreeTimeSpentList = dbConnection.getTimeSpentForFirstThreePrograms(currentUser);
        updateTopApps(topThreeAppNames, topThreeTimeSpentList);
        Timeline timeline = getTimeline();
        timeline.play();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbConnection.updateTotalScreenTime(currentUser, totalScreenTime);
        }));
    }



    public void startPeriodicRefresh(DatabaseInitializer dbConnection) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            System.out.println("Refreshing data...");
            clearAndRefreshUI(dbConnection, currentUser);
        }, 0, REFRESH_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void clearAndRefreshUI(DatabaseInitializer dbConnection, User currentUser) {
        programList.getChildren().clear();
        rightNavbar.getChildren().clear();
        timeLimitPrograms.getChildren().clear();

        List<String> appNames = dbConnection.loadStoredAppNames(currentUser);
        List<Integer> timeSpentList = dbConnection.loadTimeSpentList(currentUser);
        List<Integer> timeLimitList = dbConnection.loadTimeLimitForProgram(currentUser);
        List<String> topThreeAppNames = dbConnection.getFirstThreeProgramNames(currentUser);
        List<Integer> topThreeTimeSpentList = dbConnection.getTimeSpentForFirstThreePrograms(currentUser);

        populateUIWithAppNames(appNames, timeSpentList);
        updateTopApps(topThreeAppNames, topThreeTimeSpentList);
        populateRightNavbarWithAppNames(appNames, timeSpentList);
        populatePrograms(appNames, timeLimitList);
    }


    private Timeline getTimeline() {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1), event -> {
                    totalScreenTime += 1;
                    displayTotalTime(totalTimeLabel, totalScreenTime);
                    dbConnection.updateTotalScreenTime(currentUser, totalScreenTime);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }


    public void displayTotalTime(Label totalTimeLabel, int totalScreenTime) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        long hours = totalScreenTime / 3600;
        long minutes = (totalScreenTime % 3600) / 60;
        totalTimeLabel.setText(String.format("%dh %dmin", hours, minutes));
    }

    private void loadTimeSpentList(User currentUser) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        List<String> appNames = dbConnection.loadStoredAppNames(currentUser);
        List<Integer> timeSpentList = dbConnection.loadTimeSpentList(currentUser);
        List<Integer> timeLimitList = dbConnection.loadTimeLimitForProgram(currentUser);
        populateUIWithAppNames(appNames, timeSpentList);
        populateRightNavbarWithAppNames(appNames, timeSpentList);
        populatePrograms(appNames, timeLimitList);
    }


    private HBox createAppEntry(String appName, int timeSpentMinutes) {
        HBox appContainer = new HBox();
        appContainer.setId("program-list-item");
        appContainer.setSpacing(5);
        appContainer.setAlignment(Pos.CENTER_LEFT);

        Label appNameLabel = new Label(appName);
        appNameLabel.getStyleClass().add("program-name");

        Label timeSpentLabel = new Label("Time Spent: " + timeSpentMinutes + " min");
        timeSpentLabel.getStyleClass().add("time-spent");

        Button stopTrackingButton = new Button("Stop Tracking");
        Button startTrackingButton = new Button("Start Tracking");

        startTrackingButton.setStyle("-fx-font-size: 10px;");
        startTrackingButton.setOnAction(startEvent -> {
            startTrackingApp(appName);
            appContainer.setStyle("");
            appContainer.getChildren().remove(startTrackingButton);
            appContainer.getChildren().add(stopTrackingButton);
        });

        stopTrackingButton.setStyle("-fx-font-size: 10px;");
        stopTrackingButton.setOnAction(stopEvent -> {
            stopTrackingApp(appName);
            if (!isAppTracked(appName)) {
                appContainer.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
            }
            appContainer.getChildren().remove(stopTrackingButton);
            appContainer.getChildren().add(startTrackingButton);
        });

        if (!isAppTracked(appName)) {
            appContainer.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
            appContainer.getChildren().addAll(appNameLabel, timeSpentLabel, startTrackingButton);
        } else {
            appContainer.getChildren().addAll(appNameLabel, timeSpentLabel, stopTrackingButton);
        }
        return appContainer;
    }


    private void populateUIWithAppNames(List<String> appNames, List<Integer> timeSpentList) {
        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int timeSpentMinutes = timeSpentList.get(i);
            if (!TrackingSwitch.continuePopulating) {
                return;
            }
            HBox appEntry = createAppEntry(appName, timeSpentMinutes);
            programList.getChildren().add(appEntry);
        }
    }

    private VBox createAppEntryforRightNavbar(String appName, int timeSpentMinutes) {
        VBox appContainer = new VBox();
        appContainer.setSpacing(5);
        appContainer.setAlignment(Pos.CENTER_LEFT);

        Label appNameLabel = new Label(appName);
        appNameLabel.getStyleClass().add("program-name");

        Label timeSpentLabel = new Label("Time: " + timeSpentMinutes + " min");
        timeSpentLabel.getStyleClass().add("time-spent");

        appContainer.getChildren().addAll(appNameLabel, timeSpentLabel);
        return appContainer;
    }

    private void populateRightNavbarWithAppNames(List<String> appNames, List<Integer> timeSpentList) {
        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int timeSpentMinutes = timeSpentList.get(i);
            if (!TrackingSwitch.continuePopulating) {
                return;
            }
            VBox appEntry = createAppEntryforRightNavbar(appName, timeSpentMinutes);
            rightNavbar.getChildren().add(appEntry);
        }
    }

    private VBox createProgramEntry(String appName, int timeLimit) {
        VBox programContainer = new VBox();
        programContainer.setId("program-list-item");
        programContainer.setSpacing(5);
        programContainer.setAlignment(Pos.CENTER_LEFT);

        Label appNameLabel = new Label(appName);
        appNameLabel.getStyleClass().add("program-name");

        Label timeLimitLabel = new Label("Time Limit: " + timeLimit + " min");
        timeLimitLabel.getStyleClass().add("time-limit");

        programContainer.getChildren().addAll(appNameLabel, timeLimitLabel);
        return programContainer;
    }

    private void populatePrograms(List<String> appNames, List<Integer> timeLimitList) {
        for (int i = 0; i < appNames.size(); i++) {
            String appName = appNames.get(i);
            int timeLimit = timeLimitList.get(i);
            if (!TrackingSwitch.continuePopulating) {
                return;
            }
            VBox appEntry = createProgramEntry(appName, timeLimit);
            timeLimitPrograms.getChildren().add(appEntry);
        }
    }


    private boolean isAppTracked(String appName) {
        return dbConnection.isAppTracked(appName);
    }


    private void startTrackingApp(String appName) {
        dbConnection.startTrackingApp(currentUser, appName);
    }

    private void stopTrackingApp(String appName) {
        dbConnection.stopTrackingApp(currentUser, appName);
    }


    @FXML
    protected void onRemoveAllButtonClick() {
        rightNavbar.getChildren().clear();
        programList.getChildren().clear();
        timeLimitPrograms.getChildren().clear();
        firstAppLabel.setText("");
        firstTimeLabel.setText("");
        secondAppLabel.setText("");
        secondTimeLabel.setText("");
        thirdAppLabel.setText("");
        thirdTimeLabel.setText("");
        removeAllProgramsFromDatabase();
        timeTracker.clearShownAlerts();
    }

    private void removeAllProgramsFromDatabase() {
        dbConnection.removeAllPrograms();
    }

    public MainController() {
        this.graphDAO = new GraphDAO("program");
    }

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
        graphsHandler = new Graph(defaultGraph, graphLocation, graphDAO, currentUser.getEmail());
    }

    @FXML
    protected void onBarChartButtonClick() throws IOException { graphsHandler.showBarChart(graphLocation, currentUser.getEmail()); }
    @FXML
    protected void onColumnChartButtonClick() throws IOException { graphsHandler.showColumnChart(graphLocation, currentUser.getEmail()); }
    @FXML
    protected void onPieChartButtonClick() throws IOException { graphsHandler.showPieChart(graphLocation, currentUser.getEmail()); }

    // ---- TIME LIMITS MENU ----
    @FXML
    protected void onTimeLimitsButtonClick() throws IOException {
        clearActiveButtonStyle();
        timelimitButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(true);
        addProgramContent.setVisible(false);
    }

    // ---- ADD APP MENU ----
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

        timeTracker.startTracking(appName, currentUser);
        timeTracker.endTracking(appName, currentUser);
        int timeUse = timeTracker.getTimeSpentMinutes(appName);
        boolean success = dbConnection.saveApp(newApp, currentUser, timeUse);


        if (success) {
            programList.getChildren().clear();
            rightNavbar.getChildren().clear();
            timeLimitPrograms.getChildren().clear();
            List<String> appNames = dbConnection.loadStoredAppNames(currentUser);
            List<Integer> timeSpentList = dbConnection.loadTimeSpentList(currentUser);
            List<Integer> timeLimitList = dbConnection.loadTimeLimitForProgram(currentUser);
            populateUIWithAppNames(appNames, timeSpentList);
            List<String> topThreeAppNames = dbConnection.getFirstThreeProgramNames(currentUser);
            List<Integer> topThreeTimeSpentList = dbConnection.getTimeSpentForFirstThreePrograms(currentUser);
            updateTopApps(topThreeAppNames, topThreeTimeSpentList);
            populateRightNavbarWithAppNames(appNames, timeSpentList);
            populatePrograms(appNames, timeLimitList);


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

    // ---- MISC ----
    private void clearActiveButtonStyle() {
        homeButton.getStyleClass().remove("active-nav-button");
        insightButton.getStyleClass().remove("active-nav-button");
        timelimitButton.getStyleClass().remove("active-nav-button");
        newAppButton.getStyleClass().remove("active-nav-button");
    }

    @FXML
    protected void onLogoutButtonClick() throws IOException {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiListener.getWindowWidth(),
                HelloApplication.uiListener.getWindowHeight());
        stage.setScene(scene);
        stage.show();
        TrackingSwitch.continuePopulating = false;
        setCurrentUser(null);
    }


    private void updateTopApps(List<String> topThreeAppNames, List<Integer> topThreeTimeSpentList) {
        if (topThreeAppNames != null && topThreeTimeSpentList != null
                && !topThreeAppNames.isEmpty() && !topThreeTimeSpentList.isEmpty()) {
            if (topThreeAppNames.size() >= 1 && topThreeTimeSpentList.size() >= 1) {
                firstAppLabel.setText(topThreeAppNames.get(0));
                int timeSpentMinutes1 = topThreeTimeSpentList.get(0);
                String timeLabelText1 = String.format("%d Minutes", timeSpentMinutes1);
                firstTimeLabel.setText(timeLabelText1);
                firstAppLabel.getStyleClass().add("program-name");
                firstTimeLabel.getStyleClass().add("time-spent");
            }
            if (topThreeAppNames.size() >= 2 && topThreeTimeSpentList.size() >= 2) {
                secondAppLabel.setText(topThreeAppNames.get(1));
                int timeSpentMinutes2 = topThreeTimeSpentList.get(1);
                String timeLabelText2 = String.format("%d Minutes", timeSpentMinutes2);
                secondTimeLabel.setText(timeLabelText2);
                secondAppLabel.getStyleClass().add("program-name");
                secondTimeLabel.getStyleClass().add("time-spent");
            }
            if (topThreeAppNames.size() >= 3 && topThreeTimeSpentList.size() >= 3) {
                thirdAppLabel.setText(topThreeAppNames.get(2));
                int timeSpentMinutes3 = topThreeTimeSpentList.get(2);
                String timeLabelText3 = String.format("%d Minutes", timeSpentMinutes3);
                thirdTimeLabel.setText(timeLabelText3);
                thirdAppLabel.getStyleClass().add("program-name");
                thirdTimeLabel.getStyleClass().add("time-spent");
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
    }
}
