package com.example.DataPyramid.apptrack;

import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.User;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TimeTracking {
    private final Map<String, Instant> programStartTimes;
    private final Map<String, Integer> programTotalTimes;
    private final DatabaseInitializer dbConnection;
    private String currentActiveApp;
    private int totalScreenTime;
    private Timer timer;

    public TimeTracking(DatabaseInitializer dbConnection, User user) {
        this.dbConnection = dbConnection;
        this.programStartTimes = new HashMap<>();
        this.programTotalTimes = dbConnection.loadProgramTotalTimes(user);
        this.totalScreenTime = dbConnection.loadTotalScreenTime(user);
        setCurrentUser(user);
    }

    public void setCurrentUser(User currentUser) {
        startActiveAppMonitoring(currentUser);
    }


    public void startTracking(String appName, User currentUser) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        programStartTimes.put(appName, Instant.now());
        System.out.println("Tracking started for program: " + appName);
    }

    public int getTimeSpentMinutes(String appName) {
        return programTotalTimes.getOrDefault(appName, 0) / 60;
    }

    public void endTracking(String appName, User user) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        Instant startTime = programStartTimes.get(appName);
        if (startTime != null) {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            Integer existingTime = programTotalTimes.get(appName);
            int totalSeconds;
            if (existingTime != null) {
                totalSeconds = existingTime;
            } else {
                totalSeconds = dbConnection.getTimeSpentForApp(appName, user) * 60;
            }

            totalSeconds += duration.getSeconds();
            programTotalTimes.put(appName, totalSeconds);
            int totalMinutes = totalSeconds / 60;
            updateAllProgramTimes(user);
            System.out.println("Tracking ended for program: " + appName + ". Time spent: " + totalMinutes + " minutes.");
        }
    }

    private void updateAllProgramTimes(User user) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        for (Map.Entry<String, Integer> entry : programTotalTimes.entrySet()) {
            String appName = entry.getKey();
            int totalMinutes = entry.getValue() / 60;
            dbConnection.updateAppTimeUse(appName, totalMinutes);
        }
    }

    private void startActiveAppMonitoring(User currentUser) {
        if (!TrackingSwitch.continuePopulating) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String activeApp = ActiveWindowDetector.getActiveProcessName();
                    if (activeApp != null && !activeApp.equals(currentActiveApp)) {
                        if (currentActiveApp != null) {
                            if (dbConnection.isAppTracked(currentActiveApp)) {
                                endTracking(currentActiveApp, currentUser);
                            }
                        }
                        currentActiveApp = activeApp;
                        if (dbConnection.isAppTracked(currentActiveApp)) {
                            startTracking(currentActiveApp, currentUser);
                        }
                    }
                    updateAllProgramTimes(currentUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
