package com.example.DataPyramid.model;

public class User {
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    private int totalScreenTime;

    public User(String firstname, String lastname, String email, String password, int totalScreenTime) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.totalScreenTime = totalScreenTime;
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getTotalScreenTime() {
        return totalScreenTime;
    }
}

