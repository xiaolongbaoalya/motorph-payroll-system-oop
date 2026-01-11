package com.compprog1282025.util;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compprog1282025.model.Attendance;
import com.compprog1282025.model.Compensation;
import com.compprog1282025.model.ContactInfo;
import com.compprog1282025.model.Employee;
import com.compprog1282025.model.GovernmentID;
import com.compprog1282025.model.Position;

public class CSVReader {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    public static final String EMPLOYEE_CSV_PATH = "data/employees.csv";
    public static final String ATTENDANCE_CSV_PATH = "data/attendance.csv";


    /**
     * Reads employees from CSV and resolves supervisors by full name.
     * Does a two-pass approach: first creates employees without supervisors,
     * then sets supervisors in a second pass.
     */
    public static List<Employee> readEmployeesFromCSV(String filepath) throws Exception {
        List<Employee> employees = new ArrayList<>();

    try (
        FileReader fr = new FileReader(filepath);
        com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(fr)
        ) {
        csvReader.readNext(); // skip header

        Map<Integer, Employee> employeeMap = new HashMap<>();
        Map<Employee, String> supervisorNames = new HashMap<>();

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            int employeeNumber = Integer.parseInt(line[0]);
            String lastName = line[1].trim();
            String firstName = line[2].trim();
            LocalDate birthday = LocalDate.parse(line[3], dateFormatter);

            ContactInfo contact = new ContactInfo(line[4], line[5]);
            GovernmentID govID = new GovernmentID(line[6], line[7], line[8], line[9]);

            String status = line[10];
            String positionName = line[11];
            String supervisorFullName = line[12].trim();

            double basicSalary = parseDoubleSafe(line[13]);
            double riceSubsidy = parseDoubleSafe(line[14]);
            double phoneAllowance = parseDoubleSafe(line[15]);
            double clothingAllowance = parseDoubleSafe(line[16]);
            double grossSemiMonthlyRate = parseDoubleSafe(line[17]);
            double hourlyRate = parseDoubleSafe(line[18]);

            Compensation compensation = new Compensation(
                basicSalary, riceSubsidy, phoneAllowance, clothingAllowance,
                grossSemiMonthlyRate, hourlyRate
            );

            Position position = new Position(positionName, null);
            String password = "";

            Employee emp = new Employee(employeeNumber, firstName, lastName, birthday,
                contact, govID, position, compensation, status, password);

            employees.add(emp);
            employeeMap.put(employeeNumber, emp);
            supervisorNames.put(emp, supervisorFullName);
        }

        // This is where we fix the supervisor code
        for (Employee emp : employees) {
            String supName = supervisorNames.get(emp);
            if (supName == null || supName.trim().equalsIgnoreCase("none") || supName.isBlank()) {
                continue;
            }

            // This allows spaces in the supervisor column
            supName = supName.trim().replaceAll("\\s+", " ");

            Employee supervisor = findEmployeesSupervisor(employees, supName);

            if (supervisor == null) {
                // Parse supervisor name safely even if spaces are irregular
                String[] nameParts = supName.split(",", 2);
                String lastName = nameParts[0].trim();
                String firstName = nameParts.length > 1 ? nameParts[1].trim() : "";

                supervisor = new Employee(firstName, lastName);
            }

            emp.getPosition().setSupervisor(supervisor);
        }
    } catch (Exception e) {
        throw new RuntimeException("Failed to load employees from CSV: " + filepath, e);
    }

        return employees;
    }


    // Helper to find Employee by full name "FirstName LastName"
    private static Employee findEmployeeByFullName(List<Employee> employees, String fullName) {
        String target = fullName.toLowerCase().trim();
        for (Employee e : employees) {
            String eFullName = (e.getFirstName() + " " + e.getLastName()).toLowerCase();
            if (eFullName.equals(target)) {
                return e;
            }
        }
        return null; // Not found
    }

    //Helper to find Employee by supervisor format: "LastName, FirstName".

    private static Employee findEmployeesSupervisor(List<Employee> employees, String supervisorField) {
        if (supervisorField == null || supervisorField.isBlank()) {
            return null;
        }

        supervisorField = supervisorField.trim().replaceAll("\\s+", " "); // normalize spaces

        String[] parts = supervisorField.split(",", 2);
        if (parts.length < 2) {
            return null;
        }

        String lastName = parts[0].trim().toLowerCase();
        String firstName = parts[1].trim().toLowerCase();

        for (Employee e : employees) {
            if (e.getLastName().trim().toLowerCase().equals(lastName) &&
                e.getFirstName().trim().toLowerCase().equals(firstName)) {
                return e;
            }
        }

        return null;
    }


    private static double parseDoubleSafe(String s) {
        try {
            // Remove quotes and commas
            String cleaned = s.replaceAll("[\",]", "").trim();
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }


    /**
     * Reads Attendance records from CSV and associates them with Employees by employee number.
     *
     * @param filepath Path to attendance CSV file.
     * @param employees List of existing employees to match against.
     * @return List of Attendance records.
     * @throws Exception on file or parsing errors.
     */
    public static List<Attendance> readAttendanceFromCSV(String filepath, List<Employee> employees) throws Exception {
        List<Attendance> records = new ArrayList<>();

    try (
        FileReader fr = new FileReader(filepath);
        com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(fr)
    ) {
        csvReader.readNext(); // skip header

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            int employeeNumber = Integer.parseInt(line[0]);

            LocalDate date = LocalDate.parse(line[3], dateFormatter);
            LocalTime logIn = LocalTime.parse(line[4], timeFormatter);
            LocalTime logOut = LocalTime.parse(line[5], timeFormatter);

            // Find employee by employee number from the provided list
            Employee employee = null;
            for (Employee emp : employees) {
                if (emp.getEmployeeNumber() == employeeNumber) {
                    employee = emp;
                    break;
                }
            }

            if (employee == null) {
                System.err.printf("Warning: No employee found with number %d, skipping attendance record.%n", employeeNumber);
                continue;
            }

            Attendance attendance = new Attendance(employee, date, logIn, logOut);
            records.add(attendance);
        }
    } catch (Exception e) {
        throw new RuntimeException("Failed to load attendance from CSV: " + filepath, e);
    }

        return records;
    }

}
