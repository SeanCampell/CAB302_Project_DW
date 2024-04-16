package com.example.javafxreadingdemo;

import com.example.db.DatabaseInitializer;
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
import java.util.List;

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
    private HBox homeContent;

    @FXML
    private VBox insightsContent;

    @FXML
    private VBox timeLimitsContent;


    @FXML
    private Label welcomeLabel;

    private ToggleGroup toggleGroup;

    public void initialize() {
        toggleGroup = new ToggleGroup();
        homeButton.setToggleGroup(toggleGroup);
        insightButton.setToggleGroup(toggleGroup);
        timelimitButton.setToggleGroup(toggleGroup);

        homeButton.setSelected(true);

        // Add a listener to the selectedToggleProperty of the ToggleGroup
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                // If no toggle is selected, clear the style
                clearActiveButtonStyle();
            } else {
                // If a toggle is selected, apply the active style to it
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
        }
    }

    private DatabaseInitializer dbConnection;

    public MainController() {
        dbConnection = new DatabaseInitializer();
    }


    @FXML
    protected void onHomeButtonClick() throws IOException {
        clearActiveButtonStyle();
        homeButton.getStyleClass().add("active-nav-button");

        // Show Home content
        homeContent.setVisible(true);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(false);
    }

    @FXML
    protected void onInsightsButtonClick() throws IOException {
        clearActiveButtonStyle();
        insightButton.getStyleClass().add("active-nav-button");

        // Show Insights content
        homeContent.setVisible(false);
        insightsContent.setVisible(true);
        timeLimitsContent.setVisible(false);
    }

    @FXML
    protected void onTimeLimitsButtonClick() throws IOException {
        clearActiveButtonStyle();
        timelimitButton.getStyleClass().add("active-nav-button");

        // Show Time Limits content
        homeContent.setVisible(false);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(true);
    }

    private void clearActiveButtonStyle() {
        homeButton.getStyleClass().remove("active-nav-button");
        insightButton.getStyleClass().remove("active-nav-button");
        timelimitButton.getStyleClass().remove("active-nav-button");
    }


    @FXML
    protected void onLogoutButtonClick() throws IOException {
        // Clear the current user
        setCurrentUser(null);

        // Redirect to the login view
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}