package com.example.DataPyramid.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Simple utility class to fetch connections to a SQLite database. Code taken from activity 6.3. */
public class SqliteConnection {
    private static Connection instance = null;

    private SqliteConnection() {
        String url = "jdbc:sqlite:database.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteConnection();
        }
        return instance;
    }
}
