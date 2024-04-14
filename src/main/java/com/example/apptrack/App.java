package com.example.apptrack;

public class App {
    private final String name;
    private final AppType type;
    private int timeUse;
    private int timeLimit;

    public App(String name, AppType type, int timeLimit) {
        this.name = name;
        this.type = type;
        timeUse = 0;
        this.timeLimit = timeLimit;
    }

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

    public void addTime(int time) {
        timeUse += time;
    }

    public boolean isOverLimit() {
        if (timeLimit > 0) {
            return timeUse > timeLimit;
        }
        return false;
    }
}
