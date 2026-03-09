package com.compprog1282025.model.employee;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.compprog1282025.model.interfaces.TimeTrackable;

public class Attendance implements TimeTrackable {
    private String attendanceId;
    private int employeeNumber;
    private String firstName;
    private String lastName;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private String status;

    public Attendance() {
    }

    public Attendance(String attendanceId, int employeeNumber, String firstName, String lastName, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.attendanceId = attendanceId;
        this.employeeNumber = employeeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public Attendance(String attendanceId, int employeeNumber, String firstName, String lastName, LocalDate date, LocalTime timeIn) {
        this.attendanceId = attendanceId;
        this.employeeNumber = employeeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = null;
    }

    @Override
    public double getHoursWorked() {
        if (timeIn == null || timeOut == null) return 0.0;
        return Duration.between(timeIn, timeOut).toSeconds() / 3600.0;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeInFormatted() {
        return timeIn != null ? timeIn.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
    }

    public String getTimeOutFormatted() {
        return timeOut != null ? timeOut.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
    }

    @Override
    public String toString() {
        return String.format("Attendance[%s] - Emp#%d: %s, %s | Status: %s",
                attendanceId, employeeNumber, lastName, firstName, status);
    }
}