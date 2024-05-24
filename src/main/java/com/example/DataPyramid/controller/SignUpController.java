package com.example.DataPyramid.controller;

import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.UIObserver;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    @FXML
    private Button backButton;
    @FXML
    private Button termsButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;

    private DatabaseInitializer dbConnection;
    private UIObserver observer;
    private final String viewName = "Signup View";
    private boolean observerInit = false;

    public SignUpController() { dbConnection = new DatabaseInitializer(); }

    @FXML
    protected void onTermsButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("terms-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }
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

    @FXML
    protected void onSignUpButtonClick() {
        errorLabel.setText("");

        if (firstnameField.getText().isEmpty() || lastnameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {
            errorLabel.setText("All fields are required");
            return;
        }

        // Validate password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        User newUser = new User(firstname, lastname, email, password);
        boolean success = dbConnection.saveUser(newUser);

        if (success) {
            firstnameField.clear();
            lastnameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            showFlashMessage("Sign Up Successful", "You have successfully signed up!");

            try {
                Stage stage = (Stage) signUpButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                        HelloApplication.uiSubject.getWindowHeight());
                scene.getStylesheets().add(observer.getStylesheet());
                HelloApplication.uiSubject.removeObserver(observer);
                stage.setScene(scene);

                MainController mainController = fxmlLoader.getController();
                mainController.setCurrentUser(newUser);
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("An error occurred.");
            }
        } else {
            errorLabel.setText("Error saving user data. Please try again.");
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
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    @FXML
    protected void onVisible() {
        if(!observerInit) {
            observer = new UIObserver(viewName, backButton.getScene());
            HelloApplication.uiSubject.registerObserver(observer);
            observerInit = true;
        }
    }
}
