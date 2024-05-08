package com.example.DataPyramid.model;

/**
 * Class to provide dynamic UI capability.
 */
public class UIListener {

    /**
     * windowWidth and windowHeight should always be greater than 0
     */
    //TODO: Determine if theres a minimum size that the application needs and update the set methods accordingly
    private double windowWidth = 800;
    private double windowHeight = 530;

    /**
     * Basic constructor. Needs no values.
     */
    public UIListener() {}

    public double getWindowWidth() { return windowWidth; }
    public void setWindowWidth(double value) { if(value > 0) { windowWidth = value; } }
    public double getWindowHeight() { return windowHeight; }
    public void setWindowHeight(double value) { if(value > 0) { windowHeight = value; } }
}
