package com.example.DataPyramid.model;

// ----- JAVAFX IMPORTS -----
import com.example.DataPyramid.HelloApplication;
import javafx.scene.Scene;

interface Observer {
    void update(double WIDTH, double HEIGHT);
}

public class UIObserver implements Observer {
    private final String viewName;
    private final Scene scene;
    private String currentSize;

    /**
     * Basic constructor, called in each scene.
     * @param view The name of the view or scene the observer is attached to.
     * @param currentScene The scene associated with the view.
     */
    public UIObserver(String view, Scene currentScene) {
        viewName = view;
        scene = currentScene;
    }

    /**
     * Updates the observer with the current WIDTH and HEIGHT of the window
     * @param WIDTH Current window width.
     * @param HEIGHT Current window height.
     */
    @Override
    public void update(double WIDTH, double HEIGHT) {
        System.out.println("Current view: " + viewName + ". Dimensions: " + WIDTH + ", " + HEIGHT);
        if(WIDTH > 1500) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleLarge.css")));
        }
        else if(WIDTH > 1000) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleMid.css")));
        }
        else {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleSmall.css")));
        }
    }
}
