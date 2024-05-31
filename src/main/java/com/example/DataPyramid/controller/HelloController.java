package com.example.DataPyramid.controller;

import com.example.DataPyramid.HelloApplication;
import com.example.DataPyramid.model.UIObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for hello-view.fxml. Controls the initial scene that the user loads when opening the program.
 */
public class HelloController {
    // ----- FXML NODES -----
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;

    // ----- INTERFACE SCALING -----
    private UIObserver observer;
    private final String viewName = "Hello View";
    private boolean observerInit = false;

    public HelloController() {}

    /**
     * Moves the user to the Login View of the application.
     * @throws IOException
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.uiSubject.getWindowWidth(),
                HelloApplication.uiSubject.getWindowHeight());
        scene.getStylesheets().add(observer.getStylesheet());
        HelloApplication.uiSubject.removeObserver(observer);
        stage.setScene(scene);
    }

    /**
     * Moves the user to the Sign-Up View of the application.
     * @throws IOException
     */
    @FXML
    protected void onSignupButtonClick() throws IOException {
        Stage stage = (Stage) signUpButton.getScene().getWindow();
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
            observer = new UIObserver(viewName, loginButton.getScene());
            HelloApplication.uiSubject.registerObserver(observer);
            observerInit = true;
        }
    }
}
