package com.compprog1282025.service;

import com.compprog1282025.model.Employee;
import com.compprog1282025.model.Attendance;
import com.compprog1282025.util.CSVReader;

import java.util.List;

public class DataLoaderService {

    private List<Employee> employees;
    private List<Attendance> attendanceRecords;

    public void loadAllData() {
        try {
            // Load employees from CSV
            employees = CSVReader.readEmployeesFromCSV(CSVReader.EMPLOYEE_CSV_PATH);

            // Load attendance using employees list
            attendanceRecords = CSVReader.readAttendanceFromCSV(CSVReader.ATTENDANCE_CSV_PATH, employees);

        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Attendance> getAttendanceRecords() {
        return attendanceRecords;
    }
}