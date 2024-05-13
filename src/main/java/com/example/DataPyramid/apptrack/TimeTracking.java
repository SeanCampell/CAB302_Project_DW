package com.example.DataPyramid.apptrack;

import com.example.DataPyramid.db.DatabaseInitializer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TimeTracking {
    private Map<String, Instant> programStartTimes;
    private Map<String, Duration> programTotalTimes;
    private final DatabaseInitializer dbConnection;

    public TimeTracking(DatabaseInitializer dbConnection) {
        this.dbConnection = dbConnection;
        programStartTimes = new HashMap<>();
        programTotalTimes = new HashMap<>();
    }

    public void startTracking(String appName) {
        programStartTimes.put(appName, Instant.now());
        System.out.println("Tracking started for program: " + appName);
    }

    public int getTimeSpentMinutes(String appName) {
        Duration duration = programTotalTimes.getOrDefault(appName, Duration.ZERO);
        return (int) duration.toMinutes();
    }

    public void endTracking(String appName) {
        Instant startTime = programStartTimes.get(appName);
        if (startTime != null) {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            programStartTimes.remove(appName); // Remove start time entry
            programTotalTimes.put(appName, programTotalTimes.getOrDefault(appName, Duration.ZERO).plus(duration));

            // Update timeUse in the database
            int timeSpentMinutes = (int) duration.toMinutes();
            dbConnection.updateAppTimeUse(appName, timeSpentMinutes);
        }
    }

    public Set<String> getTrackedPrograms() {
        return programStartTimes.keySet();
    }

    public void displayTotalTime() {
        Duration totalDuration = Duration.ZERO;
        for (Duration duration : programTotalTimes.values()) {
            totalDuration = totalDuration.plus(duration);
        }
        long totalSeconds = totalDuration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        System.out.printf("Total time spent on all programs: %d hours, %d minutes, %d seconds\n", hours, minutes, seconds);
    }

    public void simulateProgramUsage(String appName, int timeSpentMinutes) {
        startTracking(appName);
        try {
            Thread.sleep(timeSpentMinutes * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTracking(appName);
    }

    private void saveProgramDuration(String appName, long duration) {
        // Implement saving duration to the database
        // For demonstration purposes, you can print it for now
        System.out.println("Program: " + appName + ", Duration: " + duration + " milliseconds");
    }
}
