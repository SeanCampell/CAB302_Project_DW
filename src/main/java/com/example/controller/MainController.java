package com.example.controller;

import com.example.db.DatabaseInitializer;
import com.example.javafxreadingdemo.HelloApplication;
import com.example.model.User;
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
        homeContent.setVisible(true);
        insightsContent.setVisible(false);
        timeLimitsContent.setVisible(false);
    }

    @FXML
    protected void onInsightsButtonClick() throws IOException {
        clearActiveButtonStyle();
        insightButton.getStyleClass().add("active-nav-button");
        homeContent.setVisible(false);
        insightsContent.setVisible(true);
        timeLimitsContent.setVisible(false);
    }

    @FXML
    protected void onTimeLimitsButtonClick() throws IOException {
        clearActiveButtonStyle();
        timelimitButton.getStyleClass().add("active-nav-button");
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
        setCurrentUser(null);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}
