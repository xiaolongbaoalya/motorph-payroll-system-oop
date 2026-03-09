package com.compprog1282025.model.employee;

import java.time.LocalDate;

import com.compprog1282025.model.interfaces.Payable;
import com.compprog1282025.model.interfaces.Printable;

public class Employee implements Printable, Payable {

    private int employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String status;
    private String supervisorName;

    private ContactInfo contact;
    private GovernmentID governmentID;
    private Position position;
    private Compensation salary;
    private String employeeID;

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

    public Employee(int employeeNumber, String firstName, String lastName, LocalDate birthday, ContactInfo contact, GovernmentID governmentID, Position position, Compensation salary, String status) {
        this(employeeNumber, firstName, lastName, birthday, contact, governmentID, position, salary, status, "None");
    }

    public Employee(String firstName, String lastName) {
        this(-1, firstName, lastName, null, null, null, null, null, "Probationary", "None");
    }

    @Override
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

    public String getEmployeeID() {
        return employeeID;
    }

    public String getFullName() {
        return String.format("%s, %s", lastName, firstName);
    }

    @Override
    public double getBasicSalary() {
        return salary == null ? 0.0 : salary.getBasicSalary();
    }

    @Override
    public String toString() {
        String department = (position != null) ? position.getDepartment() : "No Department";
        String jobTitle = (position != null) ? position.getJobTitle() : "No Position Assigned";
        return String.format("[%d] %s %s \n[%s/%s] %s",
                employeeNumber,
                firstName, lastName,
                department, status,
                jobTitle
        );
    }
}