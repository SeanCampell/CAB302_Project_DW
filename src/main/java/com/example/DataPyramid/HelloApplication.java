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
    public static UISubject uiSubject;
    @Override
    public void start(Stage stage) throws IOException {
        uiSubject = new UISubject(WIDTH, HEIGHT);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), uiSubject.getWindowWidth(), uiSubject.getWindowHeight());
        scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleSmall.css")));
        stage.setTitle(TITLE);
        stage.setScene(scene);

        stage.show();

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> uiSubject.update(stage.getWidth(), stage.getHeight());

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);

        //TODO: Find a way to close the program after letting the current loop of the Timer in TimeTracking finish.
        // This works for now though. javafx.application.Platform.exit(); doesn't work and the program will remain
        // running in the background after main is visited
        stage.setOnCloseRequest(event -> { System.exit(0); });
    }

    public static void main(String[] args) {
        launch();
    }
}