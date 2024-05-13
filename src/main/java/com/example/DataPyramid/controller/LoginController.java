package com.example.DataPyramid.controller;

import com.example.DataPyramid.db.DatabaseInitializer;
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

public class LoginController {
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

    private DatabaseInitializer dbConnection;

    public LoginController() {

        dbConnection = new DatabaseInitializer();
    }

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
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            errorLabel.setText("Invalid email or password");
        }
    }


    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        stage.setScene(scene);
    }

    @FXML
    protected void onSignUpButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        stage.setScene(scene);
    }
}
