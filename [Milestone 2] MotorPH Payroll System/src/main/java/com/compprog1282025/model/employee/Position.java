package com.compprog1282025.model.employee;

// This is a Mini Data Container for Employees' position at MotorPH. This stores Job Title and department

public class Position {
    private String jobTitle;
    private String department;

// Constructor - initializes the job title field for an employee

    public Position(String jobTitle, String department) {
        this.jobTitle = jobTitle;
        this.department = department;
    }

// Getters and Setters - allow access and modification of the job title field for an employee

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

// This makes sure it returns Chief Executive Officer - or whatever the job title is

    @Override
    public String toString() {
        return jobTitle;
    }
    
}
