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
    private String styleSize;

    /**
     * Basic constructor, called in each scene.
     * @param view The name of the view or scene the observer is attached to.
     * @param currentScene The scene associated with the view.
     */
    public UIObserver(String view, Scene currentScene) {
        viewName = view;
        scene = currentScene;
        String currentCss = String.valueOf(currentScene.getStylesheets());
        if(currentCss.contains("StyleSmall.css")) { styleSize = "small"; }
        else if(currentCss.contains("StyleMid.css")) {styleSize = "mid"; }
        else { styleSize = "large"; }
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
            styleSize = "large";
        }
        else if(WIDTH > 1000) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleMid.css")));
            styleSize = "mid";
        }
        else {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(String.valueOf(HelloApplication.class.getResource("StyleSmall.css")));
            styleSize = "small";
        }
    }

    /**
     * Gets a string containing the location of the current stylesheet to ensures it carries over through scenes.
     * @return A string containing the location of the stylesheet.
     */
    public String getStylesheet() {
        String returnString = null;
        switch (styleSize) {
            case "small":
                returnString = String.valueOf(HelloApplication.class.getResource("StyleSmall.css"));
                break;
            case "mid":
                returnString = String.valueOf(HelloApplication.class.getResource("StyleMid.css"));
                break;
            case "large":
                returnString = String.valueOf(HelloApplication.class.getResource("StyleLarge.css"));
                break;
        }
        return returnString;
    }
}
