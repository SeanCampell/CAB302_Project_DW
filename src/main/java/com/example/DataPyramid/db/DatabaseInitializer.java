package com.example.DataPyramid.db;

import com.example.DataPyramid.apptrack.App;
import com.example.DataPyramid.apptrack.AppType;
import com.example.DataPyramid.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {
    public static final String DB_URL = "jdbc:sqlite:database.db";

    public DatabaseInitializer() {
        createTable();
        createAppTable();
    }

    public void createTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS user ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstname TEXT NOT NULL,"
                    + "lastname TEXT NOT NULL,"
                    + "email TEXT NOT NULL,"
                    + "password TEXT NOT NULL)";

            // Create the table
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public void createAppTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS program ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userEmail TEXT NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "type TEXT NOT NULL,"
                    + "timeUse INTEGER NOT NULL,"
                    + "timeLimit INTEGER NOT NULL,"
                    + "timeNotif INTEGER NOT NULL,"
                    + "mondayUse INTEGER NOT NULL,"
                    + "tuesdayUse INTEGER NOT NULL,"
                    + "wednesdayUse INTEGER NOT NULL,"
                    + "thursdayUse INTEGER NOT NULL,"
                    + "fridayUse INTEGER NOT NULL,"
                    + "saturdayUse INTEGER NOT NULL,"
                    + "sundayUse INTEGER NOT NULL,"
                    + "isTracking BIT)";

            // Create the table
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public boolean saveUser(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            String sql = "INSERT INTO user (firstname, lastname, email, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getFirstname());
                preparedStatement.setString(2, user.getLastname());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());

                int rowsAffected = preparedStatement.executeUpdate();

                // Return true if the user was successfully saved
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred while saving user
        }
    }


    public User getUserByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM user WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // User found, create and return User object
                    return new User(
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found
    }

    public boolean saveApp(App app, User user, int timeUse) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            String sql = "INSERT INTO program (userEmail, name, type, timeUse, timeLimit, timeNotif, mondayUse, " +
                    "tuesdayUse, wednesdayUse, thursdayUse, fridayUse, saturdayUse, sundayUse, isTracking) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, app.getName());
                preparedStatement.setString(3, app.getType().toString());
                preparedStatement.setInt(4, timeUse);
                preparedStatement.setInt(5, app.getTimeLimit());
                preparedStatement.setInt(6, app.getTimeNotif());
                preparedStatement.setInt(7, app.getMondayUse());
                preparedStatement.setInt(8, app.getTuesdayUse());
                preparedStatement.setInt(9, app.getWednesdayUse());
                preparedStatement.setInt(10, app.getThursdayUse());
                preparedStatement.setInt(11, app.getFridayUse());
                preparedStatement.setInt(12, app.getSaturdayUse());
                preparedStatement.setInt(13, app.getSundayUse());
                preparedStatement.setBoolean(14, app.isTracking());

                int rowsAffected = preparedStatement.executeUpdate();

                // Return true if the user was successfully saved
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred while saving user
        }
    }

    public App getAppByName(String name, User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM program WHERE name = ? AND userEmail = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2,user.getEmail());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // App found, create and return App object
                    App returnApp = new App(
                            resultSet.getString("name"),
                            AppType.valueOf(resultSet.getString("type")),
                            resultSet.getInt("timeLimit"),
                            resultSet.getBoolean("isTracking")
                    );
                    returnApp.setTimeNotif(resultSet.getInt("timeNotif"));
                    returnApp.setTimeUse(resultSet.getInt("timeUse"));
                    returnApp.setMondayUse(resultSet.getInt("mondayUse"));
                    returnApp.setTuesdayUse(resultSet.getInt("tuesdayUse"));
                    returnApp.setWednesdayUse(resultSet.getInt("wednesdayUse"));
                    returnApp.setThursdayUse(resultSet.getInt("thursdayUse"));
                    returnApp.setFridayUse(resultSet.getInt("fridayUse"));
                    returnApp.setSaturdayUse(resultSet.getInt("saturdayUse"));
                    returnApp.setSundayUse(resultSet.getInt("sundayUse"));
                    returnApp.setTracking(resultSet.getBoolean("isTracking"));

                    return returnApp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // User not found
    }

    public List<String> loadStoredAppNames(User currentUser) {
        List<String> appNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT DISTINCT name FROM program WHERE userEmail = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, currentUser.getEmail());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    appNames.add(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appNames;
    }


    public void removeAllPrograms() { // Debugging
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM program";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public App[] mostUsedApps(String email) {
        App[] mostUsedApps = new App[3];
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM program WHERE userEmail = ? ORDER BY timeUse DESC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    App firstApp = new App(
                            resultSet.getString("name"),
                            AppType.valueOf(resultSet.getString("type")),
                            resultSet.getInt("timeLimit"),
                            resultSet.getBoolean("isTracking")
                    );
                    firstApp.setTimeNotif(resultSet.getInt("timeNotif"));
                    firstApp.setTimeUse(resultSet.getInt("timeUse"));
                    firstApp.setMondayUse(resultSet.getInt("mondayUse"));
                    firstApp.setTuesdayUse(resultSet.getInt("tuesdayUse"));
                    firstApp.setWednesdayUse(resultSet.getInt("wednesdayUse"));
                    firstApp.setThursdayUse(resultSet.getInt("thursdayUse"));
                    firstApp.setFridayUse(resultSet.getInt("fridayUse"));
                    firstApp.setSaturdayUse(resultSet.getInt("saturdayUse"));
                    firstApp.setSundayUse(resultSet.getInt("sundayUse"));
                    firstApp.setTracking(resultSet.getBoolean("isTracking"));

                    mostUsedApps[0] = firstApp;

                    if (resultSet.next()) {
                        App secondApp = new App(
                                resultSet.getString("name"),
                                AppType.valueOf(resultSet.getString("type")),
                                resultSet.getInt("timeLimit"),
                                resultSet.getBoolean("isTracking")
                        );
                        secondApp.setTimeNotif(resultSet.getInt("timeNotif"));
                        secondApp.setTimeUse(resultSet.getInt("timeUse"));
                        secondApp.setMondayUse(resultSet.getInt("mondayUse"));
                        secondApp.setTuesdayUse(resultSet.getInt("tuesdayUse"));
                        secondApp.setWednesdayUse(resultSet.getInt("wednesdayUse"));
                        secondApp.setThursdayUse(resultSet.getInt("thursdayUse"));
                        secondApp.setFridayUse(resultSet.getInt("fridayUse"));
                        secondApp.setSaturdayUse(resultSet.getInt("saturdayUse"));
                        secondApp.setSundayUse(resultSet.getInt("sundayUse"));
                        secondApp.setTracking(resultSet.getBoolean("isTracking"));

                        mostUsedApps[1] = secondApp;

                        if (resultSet.next()) {
                            App thirdApp = new App(
                                    resultSet.getString("name"),
                                    AppType.valueOf(resultSet.getString("type")),
                                    resultSet.getInt("timeLimit"),
                                    resultSet.getBoolean("isTracking")
                            );
                            thirdApp.setTimeNotif(resultSet.getInt("timeNotif"));
                            thirdApp.setTimeUse(resultSet.getInt("timeUse"));
                            thirdApp.setMondayUse(resultSet.getInt("mondayUse"));
                            thirdApp.setTuesdayUse(resultSet.getInt("tuesdayUse"));
                            thirdApp.setWednesdayUse(resultSet.getInt("wednesdayUse"));
                            thirdApp.setThursdayUse(resultSet.getInt("thursdayUse"));
                            thirdApp.setFridayUse(resultSet.getInt("fridayUse"));
                            thirdApp.setSaturdayUse(resultSet.getInt("saturdayUse"));
                            thirdApp.setSundayUse(resultSet.getInt("sundayUse"));
                            thirdApp.setTracking(resultSet.getBoolean("isTracking"));

                            mostUsedApps[2] = thirdApp;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mostUsedApps;
    }

    public void updateAppTimeUse(String appName, int newTimeUse) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE program SET timeUse = ? WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, newTimeUse);
                preparedStatement.setString(2, appName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
