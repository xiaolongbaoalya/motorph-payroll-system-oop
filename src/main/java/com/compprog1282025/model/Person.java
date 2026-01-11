package com.compprog1282025.model;

public class Person {
    protected String firstName;
    protected String lastName;

    // Constructor
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // Setters (optional — can omit if immutable)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Utility method
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
