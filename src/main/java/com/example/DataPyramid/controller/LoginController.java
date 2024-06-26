package com.example.DataPyramid.controller;

import com.example.DataPyramid.apptrack.TrackingSwitch;
import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.UIObserver;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for login-view.fxml.
 */
public class LoginController {
    // ----- FXML NODES -----
    @FXML
    private Button backButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    // ----- INTERFACE SCALING -----
    private UIObserver observer;
    private boolean observerInit = false;
    private final String viewName = "Login View";

    // ----- OTHER VARIABLES -----
    private DatabaseInitializer dbConnection;

    public LoginController() { dbConnection = new DatabaseInitializer(); }

    /**
     * Attempts to log the user into their account and sets them as the current user for main-view.fxml.
     * @throws IOException
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        errorLabel.setText(""); 

        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }

        String email = emailField.getText();
        String password = passwordField.getText();
        User user = dbConnection.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);
            TrackingSwitch.continuePopulating = true;

            Scene scene = new Scene(root, HelloApplication.uiSubject.getWindowWidth(),
                    HelloApplication.uiSubject.getWindowHeight());
            scene.getStylesheets().add(observer.getStylesheet());
            HelloApplication.uiSubject.removeObserver(observer);
            stage.setScene(scene);
        } else {
            errorLabel.setText("Invalid email or password");
        }
    }

    /**
     * Returns the user to hello-view.fxml.
     * @throws IOException
     */
    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    /**
     * Sends the user to signup-view.fxml.
     * @throws IOException
     */
    @FXML
    protected void onSignUpButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    /** Initialises the UIObserver for the scene when the user has their mouse anywhere inside the window. */
    @FXML
    protected void onVisible() {
        if(!observerInit) {
            observer = new UIObserver(viewName, backButton.getScene());
            HelloApplication.uiSubject.registerObserver(observer);
            observerInit = true;
        }
    }
}
