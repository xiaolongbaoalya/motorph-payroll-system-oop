package com.compprog1282025;

import com.compprog1282025.model.Employee;
import com.compprog1282025.model.GovernmentID;
import com.compprog1282025.model.Position;
import com.compprog1282025.model.Attendance;
import com.compprog1282025.model.Compensation;
import com.compprog1282025.model.ContactInfo;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.DataLoaderService;
import com.compprog1282025.service.PayrollService;
import com.compprog1282025.service.ReportService;
import com.compprog1282025.util.CSVWriter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Load data
        DataLoaderService loader = new DataLoaderService();
        loader.loadAllData();

        List<Employee> employees = loader.getEmployees();
        List<Attendance> attendanceRecords = loader.getAttendanceRecords();

        // Initialize services
        AttendanceService attendanceService = new AttendanceService(attendanceRecords);
        PayrollService payrollService = new PayrollService(attendanceService);
        EmployeeService employeeService = new EmployeeService(employees);
        ReportService reportService = new ReportService(payrollService, attendanceService);

        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();

        try {
            authService.loadAccounts(); // Load from data/accounts.csv
            } catch (IOException e) {
                System.out.println("Failed to load account data: " + e.getMessage());
                return;
            }

            // Prompt for login
            System.out.println("Welcome to the Payroll System");
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            if (!authService.authenticate(username, password)) {
                System.out.println("Invalid login credentials. Access denied.");
                return;
            }

            String role = authService.getRole(username);

            if (role == null) {
                System.out.println("User role not found. Access denied.");
                return;
            }

    switch (role.toLowerCase()) {
        case "admin":
            while (true) {
                System.out.println("\n=== Admin Menu ===");
                System.out.println("1. View All Employees");
                System.out.println("2. View Employee Record");
                System.out.println("3. Monthly Payroll Reports");
                System.out.println("4. Weekly Payroll Reports");
                System.out.println("5. Export Payroll Report to CSV");
                System.out.println("6. Add New Employee to Record");
                System.out.println("7. Update Employee Record");
                System.out.println("8. Delete Employee Record");
                System.out.println("9. Exit");
                System.out.print("Select an option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.println("\n=== Employee List ===");
                        employeeService.printAllEmployeeData();
                        break;

                    case "2":
                        System.out.print("Enter Employee Number: ");
                        try {
                            int empNum = Integer.parseInt(scanner.nextLine().trim());
                            Employee emp = employeeService.findEmployeeByNumber(empNum);
                            if (emp != null) {
                                employeeService.printEmployeeData(emp);
                            } else {
                                System.out.println("Employee not found.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid employee number.");
                        }
                        break;

                    case "3":
                        String monthlyChoice;
                        do {
                            System.out.println("\n--- Monthly Payroll Reports ---");
                            System.out.println("a. Gross Salary for ALL employees");
                            System.out.println("b. Net Salary for ALL employees");
                            System.out.println("c. Gross Salary for a specific employee");
                            System.out.println("d. Net Salary for a specific employee");
                            System.out.println("e. Return to main menu");
                            System.out.print("Select an option: ");
                            monthlyChoice = scanner.nextLine().trim().toLowerCase();

                            switch (monthlyChoice) {
                            case "a":
                                try {
                                    System.out.print("Enter target month and year (yyyy-MM): ");
                                    String input = scanner.nextLine().trim();
                                    YearMonth grossAllMonth = YearMonth.parse(input);

                                    for (Employee emp : employees) {
                                        double gross = payrollService.calculateMonthlySalary(emp, grossAllMonth);
                                        System.out.printf("Employee %d (%s): Gross Salary = %.2f%n",
                                                emp.getEmployeeNumber(), emp.getFullName(), gross);
                                        }
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Use yyyy-MM.");

                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                break;

                                case "b":
                                    try {
                                        System.out.print("Enter target month and year (yyyy-MM): ");
                                        YearMonth netAllMonth = YearMonth.parse(scanner.nextLine().trim());
                                        for (Employee emp : employees) {
                                            double net = payrollService.calculateNetSalary(emp, netAllMonth);
                                            System.out.printf("Employee %d (%s): Net Salary = %.2f%n",
                                                    emp.getEmployeeNumber(), emp.getFullName(), net);
                                        }
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Use yyyy-MM.");

                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                break;

                                case "c":
                                    try {
                                        System.out.print("Enter employee number: ");
                                        int empNum = Integer.parseInt(scanner.nextLine().trim());

                                        Employee target = employeeService.findEmployeeByNumber(empNum);
                                        if (target == null) {
                                            System.out.println("Employee not found.");
                                            break;
                                        }

                                        System.out.print("Enter target month and year (yyyy-MM): ");
                                        YearMonth month = YearMonth.parse(scanner.nextLine().trim());

                                        double gross = payrollService.calculateMonthlySalary(target, month);
                                        System.out.printf("Gross Salary for %s in %s: %.2f%n", target.getFullName(), month, gross);
                                    } catch (NumberFormatException e) {
                                        System.out.println("Invalid employee number.");
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Use yyyy-MM.");

                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;
                                case "d":
                                    try {
                                        System.out.print("Enter employee number: ");
                                        int empNum = Integer.parseInt(scanner.nextLine().trim());
                                        Employee target = employeeService.findEmployeeByNumber(empNum);
                                        if (target == null) {
                                            System.out.println("Employee not found.");
                                            break;
                                        }

                                        System.out.print("Enter target month and year (yyyy-MM): ");
                                        YearMonth month = YearMonth.parse(scanner.nextLine().trim());

                                        if (monthlyChoice.equals("c")) {
                                            double gross = payrollService.calculateMonthlySalary(target, month);
                                            System.out.printf("Gross Salary for %s in %s: %.2f%n", target.getFullName(), month, gross);
                                        } else {
                                            double net = payrollService.calculateNetSalary(target, month);
                                            System.out.printf("Net Salary for %s in %s: %.2f%n", target.getFullName(), month, net);
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Invalid employee number.");
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Use yyyy-MM.");

                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;

                                case "e":
                                    System.out.println("Returning to main menu...");
                                    break;

                                default:
                                    System.out.println("Invalid choice. Try again.");
                            }
                        } while (!monthlyChoice.equals("e"));
                        break;

                    case "4":
                        String weeklyChoice;
                        do {
                            System.out.println("\n--- Weekly Payroll Reports ---");
                            System.out.println("a. Gross Salary for a specific employee");
                            System.out.println("b. Net Salary for a specific employee");
                            System.out.println("c. Return to main menu");
                            System.out.print("Select an option: ");
                            weeklyChoice = scanner.nextLine().trim().toLowerCase();

                            switch (weeklyChoice) {
                                case "a":
                                case "b":
                                    try {
                                        System.out.print("Enter employee number: ");
                                        int empNum = Integer.parseInt(scanner.nextLine().trim());
                                        Employee target = employeeService.findEmployeeByNumber(empNum);
                                        if (target == null) {
                                            System.out.println("Employee not found.");
                                            break;
                                        }

                                        System.out.print("Enter a reference date (yyyy-MM-dd): ");
                                        LocalDate date = LocalDate.parse(scanner.nextLine().trim());

                                        if (weeklyChoice.equals("a")) {
                                            double gross = payrollService.calculateWeeklySalary(target, date);
                                            System.out.printf("Gross Salary for %s: %.2f%n", target.getFullName(), gross);
                                        } else {
                                            double net = payrollService.calculateNetWeeklySalary(target, date);
                                            System.out.printf("Net Salary for %s: %.2f%n", target.getFullName(), net);
                                        }
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Invalid date format. Use yyyy-MM.");

                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Error: " + e.getMessage());
                                    }
                                    break;

                                case "c":
                                    System.out.println("Returning to main menu...");
                                    break;

                                default:
                                    System.out.println("Invalid choice. Try again.");
                            }
                        } while (!weeklyChoice.equals("c"));
                        break;

                    case "5":
                        try {
                            System.out.print("Enter target month and year (yyyy-MM): ");
                            YearMonth exportMonth = YearMonth.parse(scanner.nextLine().trim());

                            System.out.println("Enter the full path for the export CSV file.");
                            System.out.println("Example: ./exports/payroll_report_2024-06.csv");
                            System.out.print("File path: ");
                            String filePath = scanner.nextLine().trim();

                            reportService.exportPayrollReportToCSV(employees, exportMonth, filePath);
                        } catch (Exception e) {
                            System.out.println("Invalid input or error exporting CSV: " + e.getMessage());
                        }
                        break;

                    case "6":
                        try {
                            System.out.print("Employee Number: ");
                            int empNum = Integer.parseInt(scanner.nextLine().trim());

                            System.out.print("First Name: ");
                            String firstName = scanner.nextLine().trim();

                            System.out.print("Last Name: ");
                            String lastName = scanner.nextLine().trim();

                            System.out.print("Birthday (MM/dd/yyyy): ");
                            LocalDate birthday = LocalDate.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                            System.out.print("Address: ");
                            String address = scanner.nextLine().trim();

                            System.out.print("Phone Number: ");
                            String phone = scanner.nextLine().trim();

                            System.out.print("SSS #: ");
                            String sss = scanner.nextLine().trim();

                            System.out.print("Philhealth #: ");
                            String philhealth = scanner.nextLine().trim();

                            System.out.print("TIN #: ");
                            String tin = scanner.nextLine().trim();

                            System.out.print("Pag-ibig #: ");
                            String pagibig = scanner.nextLine().trim();

                            System.out.print("Status (e.g., Regular, Contractual): ");
                            String status = scanner.nextLine().trim();

                            System.out.print("Position Title: ");
                            String pos = scanner.nextLine().trim();

                            System.out.print("Immediate Supervisor (full name or leave blank): ");
                            String supervisorName = scanner.nextLine().trim();
                            Employee supervisor = employees.stream()
                                .filter(e -> e.getFullName().equalsIgnoreCase(supervisorName))
                                .findFirst()
                                .orElse(null);

                            System.out.print("Basic Salary: ");
                            double basic = Double.parseDouble(scanner.nextLine().trim());

                            System.out.print("Rice Subsidy: ");
                            double rice = Double.parseDouble(scanner.nextLine().trim());

                            System.out.print("Phone Allowance: ");
                            double phoneAllow = Double.parseDouble(scanner.nextLine().trim());

                            System.out.print("Clothing Allowance: ");
                            double clothing = Double.parseDouble(scanner.nextLine().trim());

                            System.out.print("Gross Semi-monthly Rate: ");
                            double semiGross = Double.parseDouble(scanner.nextLine().trim());

                            System.out.print("Hourly Rate: ");
                            double hourly = Double.parseDouble(scanner.nextLine().trim());

                            Employee emp = new Employee(
                                empNum,
                                firstName,
                                lastName,
                                birthday,
                                new ContactInfo(address, phone),
                                new GovernmentID(sss, philhealth, tin, pagibig),
                                new Position(pos, supervisor),
                                new Compensation(basic, rice, phoneAllow, clothing, semiGross, hourly),
                                status,
                                "" // placeholder
                            );

                            CSVWriter.appendEmployeeToCSV(emp);
                            employees.add(emp);

                            System.out.println("New employee added successfully!");

                        } catch (Exception e) {
                            System.out.println("Error creating employee: " + e.getMessage());
                        }
                        break;

                    case "7":
                        try {
                            System.out.print("Enter Employee Number to update: ");
                            int empNumberToUpdate = Integer.parseInt(scanner.nextLine().trim());

                            Employee existing = employees.stream()
                                .filter(emp -> emp.getEmployeeNumber() == empNumberToUpdate)
                                .findFirst()
                                .orElse(null);

                            if (existing == null) {
                                System.out.println("Employee not found.");
                                break;
                            }

                            System.out.println("Updating Employee: " + existing.getBasicInfo());
                            System.out.println("Leave any field blank to keep its current value.");

                            // Name
                            System.out.print("First Name [" + existing.getFirstName() + "]: ");
                            String firstName = scanner.nextLine().trim();
                            if (!firstName.isEmpty()) existing.setFirstName(firstName);

                            System.out.print("Last Name [" + existing.getLastName() + "]: ");
                            String lastName = scanner.nextLine().trim();
                            if (!lastName.isEmpty()) existing.setLastName(lastName);

                            // Birthday
                            System.out.print("Birthday (MM/dd/yyyy) [" + existing.getBirthday().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "]: ");
                            String birthdayInput = scanner.nextLine().trim();
                            if (!birthdayInput.isEmpty()) {
                                try {
                                    existing.setBirthday(LocalDate.parse(birthdayInput, DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                                } catch (Exception e) {
                                    System.out.println("Invalid date format. Birthday not updated.");
                                }
                            }

                            // Contact Info
                            System.out.print("Address [" + existing.getContact().getAddress() + "]: ");
                            String address = scanner.nextLine().trim();
                            if (!address.isEmpty()) existing.getContact().setAddress(address);

                            System.out.print("Phone Number [" + existing.getContact().getPhoneNumber() + "]: ");
                            String phone = scanner.nextLine().trim();
                            if (!phone.isEmpty()) existing.getContact().setPhoneNumber(phone);

                            // Government IDs
                            GovernmentID govID = existing.getGovernmentID();
                            System.out.print("SSS Number [" + govID.getSss() + "]: ");
                            String sss = scanner.nextLine().trim();
                            if (!sss.isEmpty()) govID.setSss(sss);

                            System.out.print("PhilHealth Number [" + govID.getPhilhealth() + "]: ");
                            String phil = scanner.nextLine().trim();
                            if (!phil.isEmpty()) govID.setPhilhealth(phil);

                            System.out.print("TIN Number [" + govID.getTin() + "]: ");
                            String tin = scanner.nextLine().trim();
                            if (!tin.isEmpty()) govID.setTin(tin);

                            System.out.print("Pag-ibig Number [" + govID.getPagibig() + "]: ");
                            String pagibig = scanner.nextLine().trim();
                            if (!pagibig.isEmpty()) govID.setPagibig(pagibig);

                            // Status
                            System.out.print("Employment Status [" + existing.getStatus() + "]: ");
                            String status = scanner.nextLine().trim();
                            if (!status.isEmpty()) existing.setStatus(status);

                            // Position
                            System.out.print("Position Title [" + existing.getPosition().getPosition() + "]: ");
                            String positionTitle = scanner.nextLine().trim();
                            if (!positionTitle.isEmpty()) existing.getPosition().setPosition(positionTitle);

                            // Compensation
                            Compensation comp = existing.getCompensation();

                            comp.setBasicSalary(promptDouble(scanner, "Basic Salary", comp.getBasicSalary()));
                            comp.setRiceSubsidy(promptDouble(scanner, "Rice Subsidy", comp.getRiceSubsidy()));
                            comp.setPhoneAllowance(promptDouble(scanner, "Phone Allowance", comp.getPhoneAllowance()));
                            comp.setClothingAllowance(promptDouble(scanner, "Clothing Allowance", comp.getClothingAllowance()));
                            comp.setSemiGross(promptDouble(scanner, "Gross Semi-monthly Rate", comp.getSemiGross()));
                            comp.setHourlyRate(promptDouble(scanner, "Hourly Rate", comp.getHourlyRate()));

                            CSVWriter.updateEmployeeInCSV(existing);

                            System.out.println("Employee record updated successfully.");

                        } catch (Exception e) {
                            System.out.println("Error during employee update: " + e.getMessage());
                        }
                        break;

                    case "8":
                        try {
                            System.out.print("Enter Employee Number to delete: ");
                            int empNumberToDelete = Integer.parseInt(scanner.nextLine().trim());

                            Employee toDelete = employees.stream()
                                .filter(emp -> emp.getEmployeeNumber() == empNumberToDelete)
                                .findFirst()
                                .orElse(null);

                            if (toDelete == null) {
                                System.out.println("Employee not found.");
                                break;
                            }

                            System.out.print("Are you sure you want to delete " + toDelete.getFullName() + "? (yes/no): ");
                            String confirm = scanner.nextLine().trim().toLowerCase();

                            if (confirm.equals("yes")) {
                                CSVWriter.deleteEmployeeFromCSV(empNumberToDelete); // Removed path
                                employees.remove(toDelete); // Updates in-memory list
                                System.out.println("Employee deleted.");
                            } else {
                                System.out.println("Deletion cancelled.");
                            }

                        } catch (Exception e) {
                            System.out.println("Error deleting employee: " + e.getMessage());
                        }
                        break;

                    case "9":
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        case "employee":
            System.out.print("Enter your employee number: ");
            int empNum = Integer.parseInt(scanner.nextLine());

            Employee employee = employees.stream()
                .filter(e -> e.getEmployeeNumber() == empNum)
                .findFirst()
                .orElse(null);

            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }

            System.out.println("\n=== Employee Self-Service ===");
            System.out.println("Name: " + employee.getFullName());
            System.out.println("Position: " + employee.getPosition());
            System.out.println("Hourly Rate: " + employee.getHourlyRate());
            System.out.println("Compensation:");
            System.out.printf("  Rice Subsidy: %.2f%n", employee.getCompensation().getRiceSubsidy());
            System.out.printf("  Phone Allowance: %.2f%n", employee.getCompensation().getPhoneAllowance());
            System.out.printf("  Clothing Allowance: %.2f%n", employee.getCompensation().getClothingAllowance());

        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. View Monthly Gross/Net Salary");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String empChoice = scanner.nextLine().trim();

            if (empChoice.equals("1")) {
                while (true) {
                    System.out.print("Enter target month and year (yyyy-MM): ");
                    String monthInput = scanner.nextLine().trim();
                    try {
                        YearMonth targetMonth = YearMonth.parse(monthInput);
                        double gross = payrollService.calculateMonthlySalary(employee, targetMonth);
                        double net = payrollService.calculateNetSalary(employee, targetMonth);

                        System.out.println("\n=== Payroll Summary for " + targetMonth + " ===");
                        System.out.printf("Gross Salary: %.2f%n", gross);
                        System.out.printf("Net Salary: %.2f%n", net);
                        break; // Exit the retry loop
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Use yyyy-MM.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                    System.out.print("Try again? (yes/no): ");
                    String tryAgain = scanner.nextLine().trim().toLowerCase();
                    if (!tryAgain.equals("yes")) {
                        break; // Exit the retry loop if user chooses not to continue
                    }
                }

                } else if (empChoice.equals("2")) {
                    System.out.println("Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }
        break;
        default:
            System.out.println("Unrecognized role. Access denied.");
            break;
            }
        }

    private static double promptDouble(Scanner scanner, String label, double currentValue) {
        System.out.print(label + " [" + currentValue + "]: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return currentValue;
        try {
            return Double.parseDouble(input.replace(",", ""));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Keeping existing value.");
            return currentValue;
        }
    }
}
