package com.example.DataPyramid.model;

interface Observer {
    void update(double WIDTH, double HEIGHT);
}

public class UIObserver implements Observer {
    private final String viewName;
    public UIObserver(String view) { viewName = view; }
    @Override
    public void update(double WIDTH, double HEIGHT) {

    }
}
