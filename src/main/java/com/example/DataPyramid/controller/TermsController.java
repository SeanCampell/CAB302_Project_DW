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
 * Controller for terms-view.fxml
 */
public class TermsController {
    // ----- FXML NODES -----
    @FXML
    private TextArea termsAndConditions;
    @FXML
    private Button agreeButton;
    @FXML
    private Button backButton;

    // ----- INTERFACE SCALING -----
    private UIObserver observer;
    private final String viewName = "Terms View";
    private boolean observerInit = false;

    @FXML
    public void initialize() {
        termsAndConditions.setText("""
                
                TERMS AND CONDITIONS
                
                Introduction:
                Welcome to Balance Buddy! These Terms and Conditions outline the rules for using our application. By using Balance Buddy, you agree to these terms. Please do not use Balance Buddy if you do not agree with these terms and conditions.
                                
                Using Balance Buddy:
                When using Balance Buddy, please be respectful and responsible for all your actions whilst using our services. Don't misuse our services or try to harm the reputation of Balance Buddy or other users.
                                
                Account Information:
                If you create an account on Balance Buddy, please provide accurate information, as well as making sure it is secure. You are responsible for your account, so keep your password secure and do not share it with others.
                                
                Content:
                Balance Buddy may contain content from users and other third parties. We are not responsible for this content. If you as a user notice anything wrong, please let us know via our contacts.
                                
                Limitation of Liability:
                We as a company aim to provide accurate and helpful information to all our users, but Balance Buddy cannot guarantee the accuracy of the content. We are not responsible for any damages or losses related to your use of our application.
                                
                Governing Law:
                These terms are governed by in accordance with the laws of Australian Law. Any legal issues will be resolved in the courts of Australia.
                                
                By using Balance Buddy, you agree to follow these terms. 
                
                Need to reach out to us? Our administrators are here to assist you with any questions or inquiries you might have. We value your feedback and are dedicated to making your experience as seamless as possible. 
                
                Thank you for using Balance Buddy!""");
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
     * Returns the user to signup-view.fxml.
     * @throws IOException
     */
    @FXML
    protected void onNextButtonClick() throws IOException {
        Stage stage = (Stage) agreeButton.getScene().getWindow();
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
            observer = new UIObserver(viewName, agreeButton.getScene());
            HelloApplication.uiSubject.registerObserver(observer);
            observerInit = true;
        }
    }
}