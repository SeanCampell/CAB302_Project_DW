package com.example.DataPyramid;

import com.example.DataPyramid.model.UIListener;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static final String TITLE = "Data Pyramid Team";
    public static final int WIDTH = 800;
    public static final int HEIGHT = 530;
    public UIListener uiListener;
    @Override
    public void start(Stage stage) throws IOException {
        uiListener = new UIListener();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), uiListener.getWindowWidth(), uiListener.getWindowHeight());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                System.out.println("Height: " + stage.getHeight() + " Width: " + stage.getWidth());

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    public static void main(String[] args) {
        launch();
    }
}