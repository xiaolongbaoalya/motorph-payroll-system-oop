package com.compprog1282025.dao;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.service.DateTimeUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO implements DAO<Employee, Integer> {
    public static final String EMPLOYEE_CSV_PATH = "data/employees.csv";
    public static final String ARCHIVED_EMPLOYEE_CSV_PATH = "data/archived_employees.csv";

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
                        Double.parseDouble(line[14]),
                        Double.parseDouble(line[15]),
                        Double.parseDouble(line[16]),
                        Double.parseDouble(line[17])
                );
                this.employeeList.add(new Employee(empNum, firstName, lastName, birthday, contact, govtId, position, comp, status, supName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(EMPLOYEE_CSV_PATH))) {
            writer.writeNext(EMPLOYEE_HEADERS);
            for (Employee emp : employeeList) {
                writer.writeNext(toCsvRow(emp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        try (CSVWriter writer = new CSVWriter(new FileWriter(ARCHIVED_EMPLOYEE_CSV_PATH, true))) {
            if (writeHeader) {
                writer.writeNext(EMPLOYEE_HEADERS);
            }
            writer.writeNext(toCsvRow(employee));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Double.toString(employee.getSalary().getBasicSalary()),
                Double.toString(employee.getSalary().getRiceSubsidy()),
                Double.toString(employee.getSalary().getPhoneAllowance()),
                Double.toString(employee.getSalary().getClothingAllowance())
        };
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
}
