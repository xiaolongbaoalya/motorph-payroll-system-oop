package com.compprog1282025.model.employee;

import java.time.LocalDate;

import com.compprog1282025.model.interfaces.Printable;

// This stores any Employee-related information. Think of it as a Big Data Container

/* But there's more, lmao. There are also Mini Data Containers for the ContactInfo, GovernmentID, Position, Compensation.
Just so it's organized that way, the little details also have their own place? If that makes sense lol
*/

public class Employee implements Printable {

    private int employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String status; // Regular or Probationary
    private String supervisorName;

    // Ones we have mini containers for 
    private ContactInfo contact;
    private GovernmentID governmentID;
    private Position position;
    private Compensation salary;
    private String employeeID;

// Constructor - initializes all the fields for an employee. This is the most complete version of the constructor, which requires all the information to create an employee profile.

    public Employee(int employeeNumber, String firstName, String lastName, LocalDate birthday, ContactInfo contact, GovernmentID governmentID, Position position, Compensation salary, String status, String supervisorName) {
            this.employeeNumber = employeeNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.contact = contact;
            this.governmentID = governmentID;
            this.position = position;
            this.salary = salary;
            this.status = status;
            this.supervisorName = supervisorName;

}

// Overloaded constructor for creating employees without a supervisor (Shorter version)
// The long version of this constructor is similar to the one above this 

    public Employee(int employeeNumber, String firstName, String lastName, LocalDate birthday, ContactInfo contact, GovernmentID governmentID, Position position, Compensation salary, String status) {
            this(employeeNumber, firstName, lastName, birthday, contact, governmentID, position, salary, status, "None"); // "None" is placeholder/flag. It just means that there is no one above them. They don't have a boss
    }

// Overloaded constructor for creating employees with only firstName and lastName (Shorter version)
// The long version of this constructor is similar to the one above this

    public Employee(String firstName, String lastName) {
            this(-1, firstName, lastName, null, null, null, null, null, "Probationary", "None");
    }


// Getters and Setters - allow access and modification of the employee's fields

    public int getEmployeeNumber() {
        return employeeNumber;
    }
    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupervisorName() {
        return supervisorName;
    }
    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public ContactInfo getContact() {
        return contact;
    }
    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public GovernmentID getGovernmentID() {
        return governmentID;
    }
    public void setGovernmentID(GovernmentID governmentID) {
        this.governmentID = governmentID;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public Compensation getSalary() {
        return salary;
    }
    public void setSalary(Compensation salary) {
        this.salary = salary;
    }

    //added this
    // Inside Employee.java
    public String getEmployeeID() {
        return employeeID; // Or whatever your private field is named (e.g., this.id)
    }

    public String getFullName() {
    	String fn = String.format("%s, %s", lastName, firstName);
    	return fn;
    }


// This converts the whole Employee object into a single line of text we can print - the employee's profile. We can customize this to show whatever information we want, but for now, it shows the ID, name, status, and position.

    @Override
    public String toString() {

        /* We use String.format to build a clean profile: [ID] Name [Status] Position
           The %d is for the ID number, and %s is for the text (Strings).
        */

        return String.format("[%d] %s %s \n[%s/%s] %s", 
                employeeNumber,
                firstName, lastName,
                position.getDepartment(), status,
                (position != null) ? position.getJobTitle() : "No Position Assigned"
                );
    }
}
    



