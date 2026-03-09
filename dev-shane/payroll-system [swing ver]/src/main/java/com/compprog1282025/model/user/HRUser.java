package com.compprog1282025.model.user;

// This is a subclass of User

public class HRUser extends User {

// Constructor - initializes the HRUser object with the given parameters. It calls the constructor of the superclass (User) to set the common fields like username, password hash, employee number, and enabled status.

    public HRUser(String username, String passwordHash, int employeeNumber, Role role, boolean enabled) {
        super(username, passwordHash, employeeNumber, role, enabled);
    }

// This returns the role of the user, which is HR in this case. This is used to determine what permissions the user has in the system.

    @Override
    public Role getRole() {
        return Role.HR;
    }

// This converts the HRUser object into a string representation, which includes the role and username. This is useful for displaying user information in the UI or for debugging purposes.

    @Override
    public String toString() {
        return "HR: " + getUsername();
    }


}
