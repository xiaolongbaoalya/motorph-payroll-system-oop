package com.compprog1282025.model.user;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.interfaces.Authenticatable;
import com.compprog1282025.model.interfaces.Printable;

public abstract class User implements Printable, Authenticatable {
    private String username;
    private String passwordHash;
    private int employeeNumber;
    private Role role;
    private boolean enabled;
    private Employee employee;

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

    @Override
    public boolean authenticate(String rawPassword) {
        if (rawPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return org.mindrot.jbcrypt.BCrypt.checkpw(rawPassword, passwordHash);
    }

    @Override
    public abstract String toString();
}