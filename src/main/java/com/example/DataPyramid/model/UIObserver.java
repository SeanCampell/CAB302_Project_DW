package com.example.DataPyramid.model;

// ----- JAVAFX IMPORTS -----
import javafx.scene.Node;

// ----- OTHER IMPORTS -----
import java.util.List;

interface Observer {
    void update(double WIDTH, double HEIGHT);
}

public class UIObserver implements Observer {
    private final String viewName;
    private List<Node> connectedNodes;
    public UIObserver(String view) { viewName = view; }

    /**
     * Updates the observer with the current WIDTH and HEIGHT of the window
     * @param WIDTH Current window width.
     * @param HEIGHT Current window height.
     */
    @Override
    public void update(double WIDTH, double HEIGHT) {
        System.out.println("Current view: " + viewName + ". Dimensions: " + WIDTH + ", " + HEIGHT);
        for (int i = 0; i < connectedNodes.size(); i++ ) { System.out.println("Updating node " + i);}
    }

    /**
     * Connect a node to be manually manipulated by the observer.
     * @param node The JavaFX node to connect to the observer.
     */
    public void addNode(Node node) { connectedNodes.add(node); }
    /**
     * Remove a node connection to the observer.
     * @param node The JavaFX node to disconnect from the observer.
     */
    public void removeNode(Node node) { connectedNodes.remove(node); }

}
