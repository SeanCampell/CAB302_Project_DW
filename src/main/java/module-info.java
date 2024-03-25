module com.example.javafxreadingdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;



    opens com.example.javafxreadingdemo to javafx.fxml;
    exports com.example.javafxreadingdemo;
    exports com.example.db;
    opens com.example.db to javafx.fxml;
}