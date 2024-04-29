package com.example.DataPyramid.model;

import java.sql.Connection;

public class GraphDAO {

    private Connection connection;
    private String tableName;

    public GraphDAO()
    {
        connection = SqliteConnection.getInstance();
        tableName = "";
    }
}
