package com.example.DataPyramid.apptrack;

import com.example.DataPyramid.db.DatabaseInitializer;
import com.example.DataPyramid.model.User;
import com.example.DataPyramid.controller.MainController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TimeTracking {
    private final Map<String, Instant> programStartTimes;
    private final Map<String, Integer> programTotalTimes;
    private final DatabaseInitializer dbConnection;
    private final MainController mainController;
    private String currentActiveApp;
    private int totalScreenTime;
    private Timer timer;
    private User currentUser;
    private final Set<String> shownAlerts;

    public TimeTracking(DatabaseInitializer dbConnection, User user, String appName, MainController mainController) {
        this.dbConnection = dbConnection;
        this.mainController = mainController;
        setCurrentUser(user);
        this.programStartTimes = new HashMap<>();
        int timeSpent = dbConnection.getTimeSpentForApp(appName, user);
        this.programTotalTimes = new HashMap<>();
        this.programTotalTimes.put(appName, timeSpent);
        this.totalScreenTime = dbConnection.loadTotalScreenTime(user);
        this.shownAlerts = new HashSet<>();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        startActiveAppMonitoring(currentUser);
    }

    public void startTracking(String appName) {
        if (TrackingSwitch.continuePopulating) {
            programStartTimes.put(appName, Instant.now());
            System.out.println("Tracking started for program: " + appName);
        }
    }

    public int getTimeSpentMinutes(String appName) {
        Integer timeSpentSeconds = programTotalTimes.get(appName);
        return timeSpentSeconds != null ? timeSpentSeconds / 60 : 0;
    }

    public void endTracking(String appName, User user) {
        if (TrackingSwitch.continuePopulating) {
            Instant startTime = programStartTimes.get(appName);
            if (startTime != null) {
                Instant endTime = Instant.now();
                Duration duration = Duration.between(startTime, endTime);

                Integer existingTime = programTotalTimes.get(appName);
                int totalSeconds = (existingTime != null) ? existingTime
                        : dbConnection.getTimeSpentForApp(appName, user) * 60;

                totalSeconds += (int) duration.getSeconds();
                programTotalTimes.put(appName, totalSeconds);
                int totalMinutes = totalSeconds / 60;
                updateAllProgramTimes(user);
                System.out.println("Tracking ended for program: " + appName + ". Time spent: " + totalMinutes + " minutes.");
                mainController.startPeriodicRefresh(dbConnection);
                sendAlertIfTimeExceedsLimit(user);
            }
        }
    }

    private void sendAlertIfTimeExceedsLimit(User currentUser) {
        List<String> appNames = dbConnection.loadStoredAppNames(currentUser);
        for (String appName : appNames) {
            int timeUse = dbConnection.getTimeSpentForApp(appName, currentUser);
            int timeLimit = dbConnection.getTimeLimitForApp(appName, currentUser);

            if (timeUse >= timeLimit && !shownAlerts.contains(appName)) {
                String message = String.format("Time limit reached for app '%s'.", appName);
                showAlert("Time Limit Reached", message, Alert.AlertType.WARNING);
                shownAlerts.add(appName);
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void updateAllProgramTimes(User user) {
        if (TrackingSwitch.continuePopulating) {
            for (Map.Entry<String, Integer> entry : programTotalTimes.entrySet()) {
                String appName = entry.getKey();
                int totalMinutes = entry.getValue() / 60;
                dbConnection.updateAppTimeUse(appName, totalMinutes);
            }
        }
    }

    private void startActiveAppMonitoring(User currentUser) {
        if (TrackingSwitch.continuePopulating) {
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
                                startTracking(currentActiveApp);
                            }
                        }
                        updateAllProgramTimes(currentUser);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }, 0, 1000);
        }
    }

    public void clearShownAlerts() {
        shownAlerts.clear();
    }
}
