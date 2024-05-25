package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.App;
import com.example.DataPyramid.apptrack.AppType;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.Graph;
import com.example.DataPyramid.model.UIObserver;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import javafx.collections.FXCollections;
import com.example.DataPyramid.model.GraphDAO;
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
import java.util.Arrays;
import java.util.List;

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
    private Label firstAppLabel2;
    @FXML
    private Label secondAppLabel2;
    @FXML
    private Label thirdAppLabel2;
    @FXML
    private Label firstTimeLabel2;
    @FXML
    private Label secondTimeLabel2;
    @FXML
    private Label thirdTimeLabel2;

    @FXML
    private ListView<String> processListView;
    @FXML
    private ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();

    private ToggleGroup toggleGroup;
    private User currentUser;
    private final DatabaseInitializer dbConnection;
    private final String defaultGraph = "c";
    private Graph graphsHandler;

    private App[] topApps;
    private List<String> processes;

    private UIObserver observer;
    private final String viewName = "Main View";
    private boolean observerInit = false;

    private static final String[] excludeProcesses = {"Widgets.exe", "RuntimeBroker.exe",
        "dllhost.exe", "SenaryAudioApp.exe", "Windows.Media.BackgroundPlayback.exe",
        "ctfmon.exe", "PhoneExperienceHost.exe", "SecurityHealthSystray.exe",
        "WidgetService.exe", "TextInputHost.exe", "cncmd.exe", "AMDRSServ.exe",
        "CrossDeviceService.exe", "fsnotifier.exe", "conhost.exe", "SystemSettings.exe",
        "ApplicationFrameHost.exe", "UserOOBEBroker.exe", "ShellExperienceHost.exe",
        "AMDRSSrcExt.exe", "backgroundTaskHost.exe", "ai.exe", "tasklist.exe",
        "Image Name", "csrss.exe", "winlogon.exe", "fontdrvhost.exe", "dwm.exe",
        "atieclxx.exe", "uihost.exe", "EPDCtrl.exe", "sihost.exe", "tposd.exe",
        "svchost.exe", "shtctky.exe", "taskhostw.exe", "PowerMgr.exe", "LsaToast.exe",
        "LsaServerPartner.exe", "FaceBeautify.exe", "DAX3API.exe", "SearchHost.exe",
        "StartMenuExperienceHost.exe"};

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
                FXCollections.observableArrayList("Other", "Game", "Productive", "Internet", "Entertainment", "Social"));
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

    public MainController() {
        dbConnection = new DatabaseInitializer();
        this.graphDAO = new GraphDAO("program"); // Ensure to provide the correct table name
    }


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
        updateTopApps2();
        graphsHandler = new Graph(defaultGraph, graphLocation, graphDAO, currentUser.getEmail());
    }

    @FXML
    protected void onBarChartButtonClick() throws IOException { graphsHandler.showBarChart(graphLocation, currentUser.getEmail()); }
    @FXML
    protected void onColumnChartButtonClick() throws IOException { graphsHandler.showColumnChart(graphLocation, currentUser.getEmail()); }
    @FXML
    protected void onPieChartButtonClick() throws IOException { graphsHandler.showPieChart(graphLocation, currentUser.getEmail()); }
    @FXML
    protected void onColumnChartByTypeButtonClick() throws IOException { graphsHandler.showColumnChartByType(graphLocation, currentUser.getEmail()); }

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

    // ---- MISC ----
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
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    private void updateTopApps() {
        topApps = dbConnection.mostUsedApps(currentUser.getEmail());
        if(topApps != null){
            if (topApps[0] != null) {
                firstAppLabel.setText(topApps[0].getName());
                firstTimeLabel.setText(topApps[0].getTimeUse() + " Minutes");
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

    private void updateTopApps2() {
        topApps = dbConnection.mostUsedApps(currentUser.getEmail());
        if(topApps != null){
            if (topApps[0] != null) {
                firstAppLabel2.setText(topApps[0].getName());
                firstTimeLabel2.setText(topApps[0].getTimeUse() + " Minutes");
            }
            if (topApps[1] != null) {
                secondAppLabel2.setText(topApps[1].getName());
                secondTimeLabel2.setText(topApps[1].getTimeUse() + " Minutes");
            }
            if (topApps[2] != null) {
                thirdAppLabel2.setText(topApps[2].getName());
                thirdTimeLabel2.setText(topApps[2].getTimeUse() + " Minutes");
            }
        }
    }

    public static List<String> listProcesses() {
        List<String> processes = new ArrayList<String>();
        List<String> exclude = new ArrayList<String>(Arrays.asList(excludeProcesses));

        try {
            String line;
            String[] sessionName;
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {

                if (!line.trim().equals("")) {
                    // keep only the process name
                    sessionName = line.split(",");
                    line = line.substring(1);
                    //System.out.println(sessionName[2]);
                    if (!processes.contains(line.substring(0, line.indexOf('"'))) && !sessionName[2].contains("Services")
                        && !exclude.contains(line.substring(0, line.indexOf('"')))){
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
                firstTimeLabel.setText(topApps[0].getTimeUse() + " Minutes");
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

    @FXML
    protected void onVisible() {
        if(!observerInit) {
            observer = new UIObserver(viewName, homeContent.getScene());
            HelloApplication.uiSubject.registerObserver(observer);
            observerInit = true;
        }
    }
}