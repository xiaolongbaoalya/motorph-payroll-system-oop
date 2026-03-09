package com.compprog1282025.model.user;


// This is a subclass of User

public class MKTUser extends User {

// Constructor - initializes the MKTUser object with the given parameters. It calls the constructor of the superclass (User) to set the common fields like username, password hash, employee number, and enabled status.

    public MKTUser(String username, String passwordHash, int employeeNumber, Role role, boolean enabled) {
    	super(username, passwordHash, employeeNumber, Role.MKT, enabled);
    }

// This returns the role of the user, which is MKT in this case. This is used to determine what permissions the user has in the system.

    @Override
    public Role getRole() {
        return Role.MKT;
    }

// This converts the MKTUser object into a string representation, which includes the role and username. This is useful for displaying user information in the UI or for debugging purposes.

    @Override
    public String toString() {
        return "Marketing: " + getUsername();
    }


}
