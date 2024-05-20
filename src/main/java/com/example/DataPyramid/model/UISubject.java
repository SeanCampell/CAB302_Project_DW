package com.example.DataPyramid.model;

import java.util.ArrayList;
import java.util.List;

interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

/**
 * Class to provide dynamic UI capability.
 */
public class UISubject implements Subject {

    /**
     * windowWidth and windowHeight should always be greater than 0
     */
    //TODO: Determine if theres a minimum size that the application needs and update the set methods accordingly
    private double windowWidth;
    private double windowHeight;
    private List<Observer> observers;

    /**
     * Basic constructor.
     */
    public UISubject(double WIDTH, double HEIGHT)
    {
        windowWidth = WIDTH;
        windowHeight = HEIGHT;
        observers = new ArrayList<>();
    }

    public double getWindowWidth() { return windowWidth; }
    public void setWindowWidth(double value) { if(value > 0) { windowWidth = value; } }
    public double getWindowHeight() { return windowHeight; }
    public void setWindowHeight(double value) { if(value > 0) { windowHeight = value; } }

    /**
     * Updates internal variables and prints the current window size to the console.
     * @param WIDTH Current window width.
     * @param HEIGHT Current window height.
     */
    public void update(double WIDTH, double HEIGHT)
    {
        //TODO: figure out why these numbers need to be these exactly.
        // If they aren't these numbers the window grows upon scene change
        setWindowWidth(WIDTH - 16);
        setWindowHeight(HEIGHT - 39);
        System.out.println("Height: " + HEIGHT + " Width: " + WIDTH);
    }

    @Override
    public void registerObserver(Observer observer) {
       observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) { observer.update(getWindowWidth(), getWindowHeight());}
    }
}
