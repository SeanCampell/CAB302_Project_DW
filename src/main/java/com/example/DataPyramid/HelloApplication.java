package com.example.DataPyramid;

import com.example.DataPyramid.model.UISubject;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static final String TITLE = "Data Pyramid Team";
    public static final double WIDTH = 800;
    public static final double HEIGHT = 530;
    public static UISubject uiListener;
    @Override
    public void start(Stage stage) throws IOException {
        uiListener = new UISubject(WIDTH, HEIGHT);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), uiListener.getWindowWidth(), uiListener.getWindowHeight());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> uiListener.update(stage.getWidth(), stage.getHeight());

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    public static void main(String[] args) {
        launch();
    }
}

//TODO remove this if commits are coming through correctly
//TODO test commit number 2