package com.compprog1282025.model;

public class Account {
    private String username;
    private String hashedPassword; // hash for security
    private String role; //for admin or employee

    public Account(String username, String hashedPassword, String role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getRole() {
        return role;
    }
}
