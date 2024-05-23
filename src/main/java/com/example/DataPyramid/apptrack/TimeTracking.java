package com.example.DataPyramid.apptrack;

import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.User;
import javafx.scene.control.Label;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TimeTracking {
    private final Map<String, Instant> programStartTimes;
    private final Map<String, Duration> programTotalTimes;
    private final DatabaseInitializer dbConnection;
    private String currentActiveApp;
    private int totalScreenTime;
    private Timer timer;

    public TimeTracking(DatabaseInitializer dbConnection, User user) {
        this.dbConnection = dbConnection;
        this.programStartTimes = new HashMap<>();
        this.programTotalTimes = new HashMap<>();
        this.totalScreenTime = dbConnection.loadTotalScreenTime(user);
        setCurrentUser(user);
    }

    public void setCurrentUser(User currentUser) {
        startActiveAppMonitoring(currentUser);

    }

    public Map<String, Duration> getProgramTotalTimes() {
        return programTotalTimes;
    }



    public void startTracking(String appName) {
        programStartTimes.put(appName, Instant.now());
        System.out.println("Tracking started for program: " + appName);
    }

    public int getTimeSpentMinutes(String appName) {
        Duration duration = programTotalTimes.getOrDefault(appName, Duration.ZERO);
        return (int) duration.toMinutes();
    }

    public void endTracking(String appName, User user) {
        Instant startTime = programStartTimes.get(appName);
        if (startTime != null) {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            programStartTimes.remove(appName);
            programTotalTimes.put(appName, programTotalTimes.getOrDefault(appName, Duration.ZERO).plus(duration));

            int timeSpentMinutes = (int) duration.toMinutes();
            dbConnection.updateAppTimeUse(appName, timeSpentMinutes);
            dbConnection.updateTotalScreenTime(user, calculateTotalScreenTime());
            System.out.println("Tracking ended for program: " + appName + ". Time spent: " + timeSpentMinutes + " minutes.");

        }
    }

    private int calculateTotalScreenTime() {
        int totalScreenTime = this.totalScreenTime;
        for (Duration duration : programTotalTimes.values()) {
            totalScreenTime += duration.toMinutes();
        }
        return totalScreenTime;
    }

    public Set<String> getTrackedPrograms() {
        return programStartTimes.keySet();
    }


    private void startActiveAppMonitoring(User currentUser) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String activeApp = ActiveWindowDetector.getActiveProcessName();
                    if (activeApp != null && !activeApp.equals(currentActiveApp)) {
                        if (currentActiveApp != null) {
                            endTracking(currentActiveApp, currentUser);
                        }
                        currentActiveApp = activeApp;
                        startTracking(currentActiveApp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    public void stopActiveAppMonitoring() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (currentActiveApp != null) {
            endTracking(currentActiveApp, null);
            currentActiveApp = null;
        }
    }
}
