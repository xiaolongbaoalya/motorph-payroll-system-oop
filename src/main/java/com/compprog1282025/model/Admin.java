package com.compprog1282025.model;

import java.time.LocalDate;

public class Admin extends Employee {
    private String username;

    // Constructor
    public Admin(int employeeNumber, String firstName, String lastName, LocalDate birthday,
                 ContactInfo contact, GovernmentID governmentID, Position position,
                 Compensation compensation, String status, String password, String username) {
        super(employeeNumber, firstName, lastName, birthday,
              contact, governmentID, position, compensation, status, password);
        this.username = username;
    }

    // Getter
    public String getUsername() {
        return username;
    }

    // Methods
    public String getAdminInfo() {
        return String.format("Admin: %s (%s)", getFullName(), username);
    }

    public boolean authenticate(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && super.authenticate(inputPassword);
    }
}
