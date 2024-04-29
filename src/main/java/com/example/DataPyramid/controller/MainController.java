package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.App;
import com.example.DataPyramid.apptrack.AppType;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import com.example.DataPyramid.controller.SignUpController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class MainController {

    @FXML
    private ToggleButton homeButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ToggleButton insightButton;

    @FXML
    private ToggleButton timelimitButton;

    @FXML
    private ToggleButton newAppButton;

    @FXML
    private HBox homeContent;

    @FXML
    private VBox insightsContent;

    @FXML
    private VBox timeLimitsContent;

    @FXML
    private VBox addProgramContent;

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

    private ToggleGroup toggleGroup;

    private App[] topApps;

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

    private User currentUser;

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

    private DatabaseInitializer dbConnection;

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

    @FXML
    protected void onInsightsButtonClick() throws IOException {
        clearActiveButtonStyle();
        insightButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(true);
        timeLimitsContent.setVisible(false);
        addProgramContent.setVisible(false);
    }

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
        int appLimit = parseInt(appLimitField.getText());

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

    private void updateTopApps() {
        topApps = dbConnection.mostUsedApps(currentUser.getEmail());
        firstAppLabel.setText(topApps[0].getName());
        firstTimeLabel.setText(Integer.toString(topApps[0].getTimeUse()) + " Minutes");
        secondAppLabel.setText(topApps[1].getName());
        secondTimeLabel.setText(Integer.toString(topApps[1].getTimeUse()) + " Minutes");
        thirdAppLabel.setText(topApps[2].getName());
        thirdTimeLabel.setText(Integer.toString(topApps[2].getTimeUse()) + " Minutes");
    }
}