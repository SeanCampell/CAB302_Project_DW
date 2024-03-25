package com.example.javafxreadingdemo;

public interface UserDAO {
    void save(User user);
    User getUserByUsername(String username);
    // Other methods as needed
}
