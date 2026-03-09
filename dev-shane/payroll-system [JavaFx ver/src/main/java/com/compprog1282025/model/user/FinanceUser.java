package com.compprog1282025.model.user;

// This is a subclass of User

public class FinanceUser extends User {

// Constructor - initializes the FinanceUser object with the given parameters. It calls the constructor of the superclass (User) to set the common fields like username, password hash, employee number, and enabled status.

    public FinanceUser(String username, String passwordHash, int employeeNumber, Role role, boolean enabled) {
        super(username, passwordHash, employeeNumber, Role.FINANCE, enabled);
    }

// This returns the role of the user, which is FINANCE in this case. This is used to determine what permissions the user has in the system.

    @Override
    public Role getRole() {
        return Role.FINANCE;

    }

// This converts the FinanceUser object into a string representation, which includes the role and username. This is useful for displaying user information in the UI or for debugging purposes.

    @Override
    public String toString() {
        return "Finance: " + getUsername();
    }
}