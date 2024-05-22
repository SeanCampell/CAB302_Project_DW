package com.example.DataPyramid.model;

// ----- JAVAFX IMPORTS -----
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

// ----- OTHER IMPORTS -----
import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(double WIDTH, double HEIGHT);
}

public class UIObserver implements Observer {
    private final String viewName;
    private List<Node> connectedNodes;

    /**
     * Basic constructor, called in each scene.
     * @param view The name of the view or scene the observer is attached to.
     */
    public UIObserver(String view) {
        viewName = view;
        connectedNodes = new ArrayList<>();
    }

    /**
     * Updates the observer with the current WIDTH and HEIGHT of the window
     * @param WIDTH Current window width.
     * @param HEIGHT Current window height.
     */
    @Override
    public void update(double WIDTH, double HEIGHT) {
        System.out.println("Current view: " + viewName + ". Dimensions: " + WIDTH + ", " + HEIGHT);
        if(connectedNodes != null) {
            for (int i = 0; i < connectedNodes.size(); i++ ) {
                System.out.println("Updating node " + i + connectedNodes.get(i).getClass());

            }
        }
    }

    /**
     * Connect a node to be manually manipulated by the observer.
     * @param node The JavaFX node to connect to the observer.
     */
    public void addNode(Node node) { connectedNodes.add(node); }

    /**
     * Connect all children nodes to the observer. Doesn't add nodes of the following classes to the list:
     * HBox, VBox, Line, Region.
     * @param node The parent node.
     */
    public void addAllNodes(Parent node) {
        ObservableList<Node> allNodes = node.getChildrenUnmodifiable();
        for(int i = 0; i < allNodes.size(); i++ ) {
            String classString = String.valueOf(allNodes.get(i).getClass());
            System.out.println(i + " " + classString);
            switch (classString) {
                case "class javafx.scene.layout.VBox", "class javafx.scene.layout.HBox":
                    addAllNodes((Parent) allNodes.get(i));
                    break;
                case "class javafx.scene.layout.Region", "class javafx.scene.shape.Line":
                    break;
                default:
                    addNode(allNodes.get(i));
                    System.out.println(classString);
                    break;
            }
        }
    }

    /**
     * Remove a node connection to the observer.
     * @param parent The JavaFX node to disconnect from the observer.
     */
    public void removeNode(Parent parent) { connectedNodes.remove(parent); }

}
