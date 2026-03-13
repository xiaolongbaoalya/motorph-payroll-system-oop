package com.compprog1282025.service.dto;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;

import java.time.LocalDate;

public class EmployeeCreateRequest {
    private int employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private ContactInfo contactInfo;
    private GovernmentID governmentID;
    private Position position;
    private Compensation compensation;
    private String status;
    private String supervisorName;

    public int getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(int employeeNumber) { this.employeeNumber = employeeNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    public GovernmentID getGovernmentID() { return governmentID; }
    public void setGovernmentID(GovernmentID governmentID) { this.governmentID = governmentID; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public Compensation getCompensation() { return compensation; }
    public void setCompensation(Compensation compensation) { this.compensation = compensation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
}
