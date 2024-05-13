package com.example.DataPyramid.controller;

import com.example.DataPyramid.HelloApplication;
import com.example.DataPyramid.model.UIObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;

    private UIObserver observer;
    private final String viewName = "Hello View";

    public HelloController() { observerInit(viewName); }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    @FXML
    protected void onSignupButtonClick() throws IOException {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    private void observerInit(String viewName) {
        observer = new UIObserver(viewName);
        HelloApplication.uiSubject.registerObserver(observer);
    }
}
