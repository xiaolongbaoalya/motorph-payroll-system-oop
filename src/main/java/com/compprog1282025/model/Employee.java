package com.compprog1282025.model;

import java.time.LocalDate;

public class Employee extends Person {
    protected int employeeNumber;
    protected LocalDate birthday;
    protected ContactInfo contact;
    protected GovernmentID governmentID;
    protected Position position;
    protected Compensation compensation;
    protected String status;
    protected String password;

    // Full Constructor
    public Employee(int employeeNumber, String firstName, String lastName, LocalDate birthday,
                    ContactInfo contact, GovernmentID governmentID, Position position,
                    Compensation compensation, String status, String password) {
        super(firstName, lastName);
        this.employeeNumber = employeeNumber;
        this.birthday = birthday;
        this.contact = contact;
        this.governmentID = governmentID;
        this.position = position;
        this.compensation = compensation;
        this.status = status;
        this.password = password;
    }

    // ✅ Minimal constructor for creating new supervisor from name
    public Employee(String firstName, String lastName) {
        super(firstName, lastName);
        this.employeeNumber = -1;
        this.birthday = null;
        this.contact = null;
        this.governmentID = null;
        this.position = null;
        this.compensation = null;
        this.status = null;
        this.password = null;
    }

    // Getters and setters

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(int newEmpNum) {
        this.employeeNumber = newEmpNum;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public GovernmentID getGovernmentID() {
        return governmentID;
    }

    public Position getPosition() {
        return position;
    }

    public Compensation getCompensation() {
        return compensation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public double getHourlyRate() {
        return compensation.getHourlyRate();
    }

    public void setHourlyRate(double hourlyRate) {
        this.compensation.setHourlyRate(hourlyRate);
    }

    public String getBasicInfo() {
        return String.format("ID: %d, Name: %s, Position: %s, Status: %s",
                employeeNumber, getFullName(), position.getPosition(), status);
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return "[" + employeeNumber + "] " + firstName + " " + lastName + "\n"
                + " - Birthday: " + birthday + "\n"
                + " - Address: " + (contact != null ? contact.getAddress() : "N/A") + "\n"
                + " - Phone: " + (contact != null ? contact.getPhoneNumber() : "N/A") + "\n"
                + " - Position: " + (position != null ? position.getPosition() : "N/A") + "\n"
                + " - Supervisor: " + (position != null && position.getSupervisor() != null
                                        ? position.getSupervisor().getFirstName() + " " + position.getSupervisor().getLastName()
                                        : "None") + "\n"
                + " - Salary: " + (compensation != null ? compensation.getBasicSalary() : "N/A") + "\n"
                + " - Status: " + status + "\n"
                + " - SSS #: " + (governmentID != null ? governmentID.getSss() : "N/A") + "\n"
                + " - PhilHealth #: " + (governmentID != null ? governmentID.getPhilhealth() : "N/A") + "\n"
                + " - TIN #: " + (governmentID != null ? governmentID.getTin() : "N/A") + "\n"
                + " - Pag-IBIG #: " + (governmentID != null ? governmentID.getPagibig() : "N/A") + "\n"
                + " - Rice Subsidy: " + (compensation != null ? compensation.getRiceSubsidy() : "N/A") + "\n"
                + " - Phone Allowance: " + (compensation != null ? compensation.getPhoneAllowance() : "N/A") + "\n"
                + " - Clothing Allowance: " + (compensation != null ? compensation.getClothingAllowance() : "N/A") + "\n"
                + " - Gross Semi-monthly Rate: " + (compensation != null ? compensation.getSemiGross() : "N/A") + "\n"
                + " - Hourly Rate: " + (compensation != null ? compensation.getHourlyRate() : "N/A");
    }
}
