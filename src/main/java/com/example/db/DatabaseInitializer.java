package com.example.db;

import com.example.javafxreadingdemo.User;

import java.sql.*;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    public DatabaseInitializer() {
        // Call createTable() during initialization
        createTable();
    }

    public void createTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            // SQL statement to create the "user" table
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
            // Handle error (e.g., logging)
        }
    }

    public boolean saveUser(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            // Prepare an SQL query to insert user data
            String sql = "INSERT INTO user (firstname, lastname, email, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getFirstname());
                preparedStatement.setString(2, user.getLastname());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());

                // Execute the query
                int rowsAffected = preparedStatement.executeUpdate();

                // Return true if the user was successfully saved
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred while saving user
        }
    }

    // Other methods (e.g., retrieveUser, updateUser, etc.) can be added here
}
