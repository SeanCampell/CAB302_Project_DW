package com.example.DataPyramid.db;

import com.example.DataPyramid.model.User;

import java.sql.*;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    public DatabaseInitializer() {
        createTable();
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
}
