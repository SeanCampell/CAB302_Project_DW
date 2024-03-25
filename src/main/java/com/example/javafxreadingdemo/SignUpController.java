package com.example.javafxreadingdemo;

import com.example.db.DatabaseInitializer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SignUpController {
    @FXML
    private Button backButton;
    @FXML
    private Button termsButton;
    @FXML
    private Button signupButton;
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

    public SignUpController() {

        dbConnection = new DatabaseInitializer();
    }

    @FXML
    protected void onTermsButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("terms-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }
    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
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

        // Save user to the database
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Create a new User object
        User newUser = new User(firstname, lastname, email, password);

        // Save user data to the database
        boolean success = dbConnection.saveUser(newUser);

        if (success) {
            // Clear form fields on successful signup
            firstnameField.clear();
            lastnameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            try {
                Stage stage = (Stage) signupButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading terms-view.fxml");
            }
        } else {
            errorLabel.setText("Error saving user data. Please try again.");
        }


    }
}
