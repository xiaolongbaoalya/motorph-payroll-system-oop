package com.compprog1282025.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.compprog1282025.model.Employee;

public class CSVWriter {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static final String EMPLOYEE_CSV_PATH = "data/employees.csv";

    public static void appendEmployeeToCSV(Employee employee) throws IOException {
    File file = new File(EMPLOYEE_CSV_PATH);
    boolean fileExists = file.exists();

    try (com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(file, true))) {
        // Write header if file doesn't exist or is empty
        if (!fileExists || file.length() == 0) {
            writer.writeNext(new String[]{
                "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone",
                "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Supervisor",
                "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
                "Gross Semi-monthly Rate", "Hourly Rate"
            });
        }

        String[] record = new String[]{
            String.valueOf(employee.getEmployeeNumber()),
            employee.getLastName(),
            employee.getFirstName(),
            employee.getBirthday().format(dateFormatter),
            employee.getContact().getAddress(),
            employee.getContact().getPhoneNumber(),
            employee.getGovernmentID().getSss(),
            employee.getGovernmentID().getPhilhealth(),
            employee.getGovernmentID().getTin(),
            employee.getGovernmentID().getPagibig(),
            employee.getStatus(),
            employee.getPosition().getPosition(),
            employee.getPosition().getSupervisor() != null ? employee.getPosition().getSupervisor().getFullName() : "",
            formatDouble(employee.getCompensation().getBasicSalary()),
            formatDouble(employee.getCompensation().getRiceSubsidy()),
            formatDouble(employee.getCompensation().getPhoneAllowance()),
            formatDouble(employee.getCompensation().getClothingAllowance()),
            formatDouble(employee.getCompensation().getSemiGross()),
            formatDouble(employee.getCompensation().getHourlyRate())
        };

        writer.writeNext(record);
        }
    }

    private static String formatDouble(double value) {
        return String.valueOf(value); // Write as plain number, no quotes, no commas
    }

    public static void updateEmployeeInCSV(Employee updatedEmployee) throws IOException {
    List<Employee> allEmployees;
    try {
        allEmployees = CSVReader.readEmployeesFromCSV(EMPLOYEE_CSV_PATH);
    } catch (Exception e) {
        throw new IOException("Failed to read employees from CSV", e);
    }

    boolean found = false;
    for (int i = 0; i < allEmployees.size(); i++) {
        if (allEmployees.get(i).getEmployeeNumber() == updatedEmployee.getEmployeeNumber()) {
            allEmployees.set(i, updatedEmployee);
            found = true;
            break;
        }
    }

    if (!found) {
        throw new IOException("Employee not found: " + updatedEmployee.getEmployeeNumber());
    }

        writeAllEmployeesToCSV(allEmployees);
    }


    public static void deleteEmployeeFromCSV(int employeeNumber) throws IOException {
    List<Employee> allEmployees;
    try {
        allEmployees = CSVReader.readEmployeesFromCSV(EMPLOYEE_CSV_PATH);
    } catch (Exception e) {
        throw new IOException("Failed to read employees from CSV: " + e.getMessage(), e);
    }

    boolean removed = allEmployees.removeIf(emp -> emp.getEmployeeNumber() == employeeNumber);

    if (!removed) {
        throw new IOException("Employee not found: " + employeeNumber);
    }

        writeAllEmployeesToCSV(allEmployees);
    }


public static void writeAllEmployeesToCSV(List<Employee> employees) throws IOException {
    Path path = Paths.get(EMPLOYEE_CSV_PATH); // resolved relative to project root
    try (com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(path.toFile()))) {
        // Write header
        writer.writeNext(new String[]{
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Supervisor",
            "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
            "Gross Semi-monthly Rate", "Hourly Rate"
        });

        for (Employee emp : employees) {
            writer.writeNext(new String[]{
                String.valueOf(emp.getEmployeeNumber()),
                emp.getLastName(),
                emp.getFirstName(),
                emp.getBirthday().format(dateFormatter),
                emp.getContact().getAddress(),
                emp.getContact().getPhoneNumber(),
                emp.getGovernmentID().getSss(),
                emp.getGovernmentID().getPhilhealth(),
                emp.getGovernmentID().getTin(),
                emp.getGovernmentID().getPagibig(),
                emp.getStatus(),
                emp.getPosition().getPosition(),
                emp.getPosition().getSupervisor() != null ? emp.getPosition().getSupervisor().getFullName() : "",
                formatDouble(emp.getCompensation().getBasicSalary()),
                formatDouble(emp.getCompensation().getRiceSubsidy()),
                formatDouble(emp.getCompensation().getPhoneAllowance()),
                formatDouble(emp.getCompensation().getClothingAllowance()),
                formatDouble(emp.getCompensation().getSemiGross()),
                formatDouble(emp.getCompensation().getHourlyRate())
            });
        }

        System.out.println("Successfully wrote to: " + path.toAbsolutePath());
        }
    }

    public static void writeEmployees(List<Employee> employees) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void writeEmployees(List<Employee> employees, File csvFile) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



}
