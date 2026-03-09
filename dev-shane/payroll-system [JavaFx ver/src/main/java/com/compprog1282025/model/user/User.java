package com.compprog1282025.model.user;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.interfaces.Printable;

// This is our parent class guys, subclasses will be ITUser, HRUser, FinanceUser, AdminUser, MKTUser, OperationsUser

// Subclasses must implement getRole() and toString() okay

// This class serves as the base class for all user types in the system. It contains common fields such as username, password hash, employee number, and enabled status. It also defines abstract methods getRole() and toString() that must be implemented by all subclasses to specify the user's role and provide a string representation of the user, respectively.

public abstract class User implements Printable{
    private String username;
    private String passwordHash;
    private int employeeNumber;
    private Role role; // Key for Access Control
    private boolean enabled;
    private Employee employee;


    // Constructor - initializes the User object with the given parameters. It sets the username, password hash, employee number, and enabled status for the user. This constructor is called by the constructors of the subclasses to initialize these common fields.
    public User(String username, String passwordHash, int employeeNumber, Role role, boolean enabled) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.employeeNumber = employeeNumber;
        this.enabled = enabled;
        this.role = role;
    }

    public Role getRole() { 
        return role; 

    }

    // Getters and setters for the fields. These methods allow other parts of the application to access and modify the user's information as needed. For example, you can get the username or update the enabled status of a user using these methods.
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }
    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    // Add this getter so the Dashboard can see the name and ID
    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Abstract methods that must be implemented by subclasses. These methods define the contract that all user types must follow. The getRole() method should return the specific role of the user (e.g., ADMIN, HR, IT), while the toString() method should provide a string representation of the user, typically including their role and username.
    @Override
    public abstract String toString();

}