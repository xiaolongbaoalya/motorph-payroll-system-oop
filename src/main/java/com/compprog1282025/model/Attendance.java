package com.compprog1282025.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {
    private Employee employee;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;

    public Attendance(Employee employee, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employee = employee;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    @Override
    public String toString() {
        return "Employee: " + employee.getFullName() + "\n"
             + " - Date: " + date + "\n"
             + " - Time In: " + timeIn + "\n"
             + " - Time Out: " + timeOut;
    }
}
