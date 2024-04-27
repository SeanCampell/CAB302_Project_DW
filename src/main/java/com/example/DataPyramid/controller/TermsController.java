package com.example.DataPyramid.controller;

import com.example.DataPyramid.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class TermsController {
    @FXML
    private TextArea termsAndConditions;
    @FXML
    private Button agreeButton;
    @FXML
    private Button backButton;


    @FXML
    public void initialize() {
        termsAndConditions.setText("""
                
                TERMS AND CONDIDITONS
                
                Introduction:
                Welcome to ___! These Terms and Conditions outline the rules for using our application. By using ___, you agree to these terms. Please do not use ___ if you do not agree with these terms and conditions.
                                
                Using ___:
                When using ___, please be respectful and responsible for all your actions whilst using our services. Don't misuse our services or try to harm the reputation of ___ or other users.
                                
                Account Information:
                If you create an account on ___, please provide accurate information, as well as making sure it is secure. You are responsible for your account, so keep your password secure and do not share it with others.
                                
                Content:
                ___ may contain content from users and other third parties. We are not responsible for this content. If you as a user notice anything wrong, please let us know via our contacts.
                                
                Limitation of Liability:
                We as a company aim to provide accurate and helpful information to all our users, but ___ cannot guarantee the accuracy of the content. We are not responsible for any damages or losses related to your use of our website.
                                
                Governing Law:
                These terms are governed by in accordance with the laws of Australian Law. Any legal issues will be resolved in the courts of Australia.
                                
                By using ___, you agree to follow these terms. 
                
                Need to reach out to us? Our administrators are here to assist you with any questions or inquiries you might have. We value your feedback and are dedicated to making your experience as seamless as possible. 
                
                Thank you for using ___!""");
    }


    @FXML
    protected void onBackButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onNextButtonClick() throws IOException {
        Stage stage = (Stage) agreeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}