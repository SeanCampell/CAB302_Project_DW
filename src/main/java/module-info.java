module com.example.javafxreadingdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.sun.jna;
    requires com.sun.jna.platform;



    opens com.example.DataPyramid to javafx.fxml;
    exports com.example.DataPyramid;
    exports com.example.DataPyramid.db;
    opens com.example.DataPyramid.db to javafx.fxml;
    exports com.example.DataPyramid.controller;
    opens com.example.DataPyramid.controller to javafx.fxml;
    exports com.example.DataPyramid.model;
    opens com.example.DataPyramid.model to javafx.fxml;
}