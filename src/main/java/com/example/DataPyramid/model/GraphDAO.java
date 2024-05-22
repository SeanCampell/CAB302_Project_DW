package com.example.DataPyramid.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphDAO {

    private Connection connection;
    private String tableName;

    // Constructor that allows setting the table name
    public GraphDAO(String tableName) {
        this.connection = SqliteConnection.getInstance();
        this.tableName = tableName;
    }

    // Setter for table name, in case it needs to be changed after instantiation
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Retrieves the sum of time use for each app, grouped by app name for a specific user.
     * @param userEmail The email of the user for which to retrieve the app usage data.
     * @return A map with app names as keys and total time use as values.
     */
    public Map<String, Integer> getAppTimeUsageByUser(String userEmail) {
        Map<String, Integer> appUsage = new LinkedHashMap<>();
        String sql = "SELECT name, SUM(timeUse) as totalUsage FROM " + tableName +
                " WHERE userEmail = ? GROUP BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);  // Set the userEmail parameter in the query
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appUsage.put(rs.getString("name"), rs.getInt("totalUsage"));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving app time usage: " + e.getMessage());
            e.printStackTrace();
        }
        return appUsage;
    }

    public Map<String, Map<String, Integer>> getWeeklyAppUsageByUser(String userEmail) {
        Map<String, Map<String, Integer>> appUsage = new LinkedHashMap<>();
        String sql = "SELECT name, mondayUse, tuesdayUse, wednesdayUse, thursdayUse, fridayUse, saturdayUse, sundayUse FROM program WHERE userEmail = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                Map<String, Integer> weeklyUsage = new LinkedHashMap<>();
                weeklyUsage.put("Monday", rs.getInt("mondayUse"));
                weeklyUsage.put("Tuesday", rs.getInt("tuesdayUse"));
                weeklyUsage.put("Wednesday", rs.getInt("wednesdayUse"));
                weeklyUsage.put("Thursday", rs.getInt("thursdayUse"));
                weeklyUsage.put("Friday", rs.getInt("fridayUse"));
                weeklyUsage.put("Saturday", rs.getInt("saturdayUse"));
                weeklyUsage.put("Sunday", rs.getInt("sundayUse"));
                appUsage.put(name, weeklyUsage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appUsage;
    }

    public Map<String, Integer> getAppUsageByUser(String userEmail) {
        Map<String, Integer> usageData = new LinkedHashMap<>();
        String sql = "SELECT name, SUM(timeUse) as totalUsage FROM program WHERE userEmail = ? GROUP BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                usageData.put(rs.getString("name"), rs.getInt("totalUsage"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usageData;
    }

    public Map<String, Map<String, Integer>> getTimeUsageByTypeAndApp(String userEmail) {
        Map<String, Map<String, Integer>> usageByTypeAndApp = new LinkedHashMap<>();
        String sql = "SELECT type, name, SUM(timeUse) as totalUsage FROM program WHERE userEmail = ? GROUP BY type, name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                String appName = rs.getString("name");
                int totalUsage = rs.getInt("totalUsage");
                usageByTypeAndApp.computeIfAbsent(type, k -> new LinkedHashMap<>()).put(appName, totalUsage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usageByTypeAndApp;
    }

}
