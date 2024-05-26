package com.example.DataPyramid.apptrack;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class App {
    // Initial Variables
    private final String name;
    private final AppType type;
    private int timeUse;
    private int mondayUse;
    private int tuesdayUse;
    private int wednesdayUse;
    private int thursdayUse;
    private int fridayUse;
    private int saturdayUse;
    private int sundayUse;
    private int timeLimit;

    private int timeNotif;

    private boolean isTracking;

    // Constructor
    public App(String name, AppType type, int timeLimit, boolean isTracking) {
        this.name = name;
        this.type = type;
        timeUse = 0;
        this.timeLimit = timeLimit;
        timeNotif = timeLimit / 10;
        mondayUse = 0;
        tuesdayUse = 0;
        wednesdayUse = 0;
        thursdayUse = 0;
        fridayUse = 0;
        saturdayUse = 0;
        sundayUse = 0;
        this.isTracking = isTracking;
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

    public int getMondayUse() {
        return mondayUse;
    }

    public void setMondayUse(int mondayUse) { this.mondayUse = mondayUse; }

    public int getTuesdayUse() {
        return tuesdayUse;
    }

    public void setTuesdayUse(int tuesdayUse) {
        this.tuesdayUse = tuesdayUse;
    }

    public int getWednesdayUse() {
        return wednesdayUse;
    }

    public void setWednesdayUse(int wednesdayUse) {
        this.wednesdayUse = wednesdayUse;
    }

    public int getThursdayUse() {
        return thursdayUse;
    }

    public void setThursdayUse(int thursdayUse) {
        this.thursdayUse = thursdayUse;
    }

    public int getFridayUse() {
        return fridayUse;
    }

    public void setFridayUse(int fridayUse) {
        this.fridayUse = fridayUse;
    }

    public int getSaturdayUse() {
        return saturdayUse;
    }

    public void setSaturdayUse(int saturdayUse) {
        this.saturdayUse = saturdayUse;
    }

    public int getSundayUse() {
        return sundayUse;
    }

    public void setSundayUse(int sundayUse) {
        this.sundayUse = sundayUse;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public  int getTimeNotif() { return timeNotif; }

    public void setTimeNotif(int timeNotif) { this.timeNotif = timeNotif; }

    public boolean isTracking() { return isTracking; }

    public void setTracking(boolean tracking) { isTracking = tracking; }

    /**
     * Add time that has been used on the application.
     * @param time The time to be added.
     */
    public void addTime(int time) {
        timeUse += time;

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        if (dayOfWeek.getValue() == 1) {
            mondayUse += time;
        } else if (dayOfWeek.getValue() == 2) {
            tuesdayUse += time;
        } else if (dayOfWeek.getValue() == 3) {
            wednesdayUse += time;
        } else if (dayOfWeek.getValue() == 4) {
            thursdayUse += time;
        } else if (dayOfWeek.getValue() == 5) {
            fridayUse += time;
        } else if (dayOfWeek.getValue() == 6) {
            saturdayUse += time;
        } else {
            sundayUse += time;
        }
    }

    // Check if time use has exceeded time limit
    public boolean isOverLimit() {
        if (timeLimit > 0) {
            return timeUse > timeLimit;
        }
        return false;
    }

    // Check if time use has exceeded time warning
    public boolean isOverWarning() {
        return timeLimit - timeUse < timeNotif;
    }

    // Return total time use of last seven days
    public int sevenDaysUse() {
        return mondayUse+tuesdayUse+wednesdayUse+
                thursdayUse+fridayUse+saturdayUse+sundayUse;
    }

    // Return total time use for today
    public int todayUse() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        if (dayOfWeek.getValue() == 1) {
            return mondayUse;
        } else if (dayOfWeek.getValue() == 2) {
            return tuesdayUse;
        } else if (dayOfWeek.getValue() == 3) {
            return wednesdayUse;
        } else if (dayOfWeek.getValue() == 4) {
            return thursdayUse;
        } else if (dayOfWeek.getValue() == 5) {
            return fridayUse;
        } else if (dayOfWeek.getValue() == 6) {
            return saturdayUse;
        } else {
            return sundayUse;
        }
    }
}
