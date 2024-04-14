package com.example.apptrack;

public class App {
    // Initial Variables
    private final String name;
    private final AppType type;
    private int timeUse;
    private int timeLimit;

    // Constructor
    public App(String name, AppType type, int timeLimit) {
        this.name = name;
        this.type = type;
        timeUse = 0;
        this.timeLimit = timeLimit;
    }

    // Get and Set functions
    public String getName() {
        return name;
    }

    public AppType getType() {
        return type;
    }

    public int getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(int timeUse) {
        this.timeUse = timeUse;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    // Add time that has been used on the application
    public void addTime(int time) {
        timeUse += time;
    }

    // Check if time use has exceeded time limit
    public boolean isOverLimit() {
        if (timeLimit > 0) {
            return timeUse > timeLimit;
        }
        return false;
    }
}
