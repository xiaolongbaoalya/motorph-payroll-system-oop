package com.compprog1282025.dao;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.service.DateTimeUtil;
import com.compprog1282025.service.MonetaryInputParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeDAO implements DAO<Employee, Integer> {
    public static final String EMPLOYEE_CSV_PATH = "data/employees.csv";
    public static final String ARCHIVED_EMPLOYEE_CSV_PATH = "data/archived_employees.csv";
    private static final int FILE_WRITE_RETRY_ATTEMPTS = 3;
    private static final long FILE_WRITE_RETRY_DELAY_MS = 100L;

    private static final String[] EMPLOYEE_HEADERS = {
            "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone",
            "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Department",
            "Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance"
    };

    private final List<Employee> employeeList;

    public EmployeeDAO() {
        this.employeeList = new ArrayList<>();
        loadData();
    }

    @Override
    public void loadData() {
        employeeList.clear();
        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_CSV_PATH))) {
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                int empNum = Integer.parseInt(line[0]);
                String lastName = line[1];
                String firstName = line[2];
                LocalDate birthday = DateTimeUtil.convertStringToDate(line[3]);
                ContactInfo contact = new ContactInfo(line[4], line[5]);
                GovernmentID govtId = new GovernmentID(line[6], line[7], line[8], line[9]);
                String status = line[10];
                Position position = new Position(line[11], line[12]);

                Employee supervisor = this.findByName(line[13]);
                String supName = supervisor != null ? supervisor.getFullName() : line[13];

                Compensation comp = new Compensation(
                        parseAmount(line[14], "Basic Salary"),
                        parseAmount(line[15], "Rice Subsidy"),
                        parseAmount(line[16], "Phone Allowance"),
                        parseAmount(line[17], "Clothing Allowance")
                );
                this.employeeList.add(new Employee(empNum, firstName, lastName, birthday, contact, govtId, position, comp, status, supName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveData() {
        writeCsvWithRetry(EMPLOYEE_CSV_PATH, false);
    }

    @Override
    public void insert(Employee employee) {
        employeeList.add(employee);
        saveData();
    }

    @Override
    public void update(Employee employee) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getEmployeeNumber() == employee.getEmployeeNumber()) {
                employeeList.set(i, employee);
                break;
            }
        }
        saveData();
    }

    public void updateByEmployeeNumber(int originalEmployeeNumber, Employee updatedEmployee) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getEmployeeNumber() == originalEmployeeNumber) {
                employeeList.set(i, updatedEmployee);
                saveData();
                return;
            }
        }
        throw new IllegalArgumentException("Employee not found: " + originalEmployeeNumber);
    }

    @Override
    public void delete(Integer employeeNumber) {
        employeeList.removeIf(emp -> emp.getEmployeeNumber() == employeeNumber.intValue());
        saveData();
    }

    public void archive(Integer employeeNumber) {
        Employee employee = findById(employeeNumber);
        if (employee == null) {
            return;
        }

        appendArchivedEmployee(employee);
        employeeList.removeIf(emp -> emp.getEmployeeNumber() == employeeNumber.intValue());
        saveData();
    }

    private void appendArchivedEmployee(Employee employee) {
        File archiveFile = new File(ARCHIVED_EMPLOYEE_CSV_PATH);
        boolean writeHeader = !archiveFile.exists() || archiveFile.length() == 0;

        writeArchivedEmployeeWithRetry(employee, writeHeader);
    }

    private String[] toCsvRow(Employee employee) {
        return new String[]{
                String.valueOf(employee.getEmployeeNumber()),
                employee.getLastName(),
                employee.getFirstName(),
                DateTimeUtil.convertDateToString(employee.getBirthday()),
                employee.getContact().getAddress(),
                employee.getContact().getPhone(),
                employee.getGovernmentID().getSss(),
                employee.getGovernmentID().getPhilHealth(),
                employee.getGovernmentID().getTin(),
                employee.getGovernmentID().getPagIbig(),
                employee.getStatus(),
                employee.getPosition().getJobTitle(),
                employee.getPosition().getDepartment(),
                employee.getSupervisorName(),
                formatMoney(employee.getSalary().getBasicSalary()),
                formatMoney(employee.getSalary().getRiceSubsidy()),
                formatMoney(employee.getSalary().getPhoneAllowance()),
                formatMoney(employee.getSalary().getClothingAllowance())
        };
    }


    private static String formatMoney(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static double parseAmount(String raw, String fieldName) {
        return MonetaryInputParser.parseAmount(raw, fieldName);
    }
    @Override
    public List<Employee> getAll() {
        return employeeList;
    }

    @Override
    public Employee findById(Integer employeeNumber) {
        for (Employee current : employeeList) {
            if (current.getEmployeeNumber() == employeeNumber.intValue()) {
                return current;
            }
        }
        return null;
    }

    public Employee findByName(String fullName) {
        for (Employee current : this.employeeList) {
            if (current.getFullName().trim().equalsIgnoreCase(fullName.trim())) {
                return current;
            }
        }
        return null;
    }

    private void writeCsvWithRetry(String path, boolean append) {
        IOException lastException = null;
        for (int attempt = 1; attempt <= FILE_WRITE_RETRY_ATTEMPTS; attempt++) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(path, append))) {
                writer.writeNext(EMPLOYEE_HEADERS);
                for (Employee emp : employeeList) {
                    writer.writeNext(toCsvRow(emp));
                }
                return;
            } catch (IOException e) {
                lastException = e;
                pauseBeforeRetry(attempt, e);
            }
        }
        if (lastException != null) {
            lastException.printStackTrace();
        }
    }

    private void writeArchivedEmployeeWithRetry(Employee employee, boolean writeHeader) {
        IOException lastException = null;
        for (int attempt = 1; attempt <= FILE_WRITE_RETRY_ATTEMPTS; attempt++) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(ARCHIVED_EMPLOYEE_CSV_PATH, true))) {
                if (writeHeader) {
                    writer.writeNext(EMPLOYEE_HEADERS);
                }
                writer.writeNext(toCsvRow(employee));
                return;
            } catch (IOException e) {
                lastException = e;
                pauseBeforeRetry(attempt, e);
            }
        }
        if (lastException != null) {
            lastException.printStackTrace();
        }
    }

    private void pauseBeforeRetry(int attempt, IOException e) {
        if (attempt >= FILE_WRITE_RETRY_ATTEMPTS) {
            return;
        }
        try {
            Thread.sleep(FILE_WRITE_RETRY_DELAY_MS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            e.addSuppressed(interruptedException);
        }
    }
}


