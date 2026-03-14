package com.compprog1282025.service;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDao;
    private final AttendanceDAO attendanceDao;
    private final RequestDAO requestDao;
    private final UserDAO userDao;

    public EmployeeService(EmployeeDAO employeeDao) {
        this(employeeDao, new AttendanceDAO(), new RequestDAO(), new UserDAO());
    }

    public EmployeeService(EmployeeDAO employeeDao, AttendanceDAO attendanceDao, RequestDAO requestDao, UserDAO userDao) {
        this.employeeDao = employeeDao;
        this.attendanceDao = attendanceDao;
        this.requestDao = requestDao;
        this.userDao = userDao;
    }

    public Employee getEmployee(int employeeNumber) {
        return employeeDao.findById(employeeNumber);
    }

    public Employee getEmployeeDetails(int employeeNumber) {
        return employeeDao.findById(employeeNumber);
    }

    public List<Employee> getEmployeeDirectory(Session session) throws InvalidAccessException {
        AccessControlService.requireEmployeeManagement(session, "Employee directory access");
        return new ArrayList<>(employeeDao.getAll());
    }

    public void addEmployee(Employee employee, Session session) throws InvalidAccessException {
        AccessControlService.requireEmployeeManagement(session, "Create employee");
        validateEmployeeData(employee);
        ensureUniqueEmployeeNumber(employee.getEmployeeNumber());
        employeeDao.insert(employee);
    }

    public void createEmployee(Employee employee, Session session) throws InvalidAccessException {
        addEmployee(employee, session);
    }

    public void updateEmployee(Employee employee, Session session) throws InvalidAccessException {
        updateEmployee(employee.getEmployeeNumber(), employee, session);
    }

    public void updateEmployee(int originalEmployeeNumber, Employee employee, Session session) throws InvalidAccessException {
        AccessControlService.requireEmployeeManagement(session, "Update employee");
        validateEmployeeData(employee);
        ensureEmployeeExists(originalEmployeeNumber);
        ensureUniqueEmployeeNumberForUpdate(originalEmployeeNumber, employee.getEmployeeNumber());
        employeeDao.updateByEmployeeNumber(originalEmployeeNumber, employee);
    }

    public void archiveEmployee(int employeeNumber, Session session) throws InvalidAccessException {
        AccessControlService.requireEmployeeManagement(session, "Archive employee");

        Employee employee = employeeDao.findById(employeeNumber);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found: " + employeeNumber);
        }

        employeeDao.archive(employeeNumber);
    }

    public void removeEmployee(int employeeNumber, Session session) throws InvalidAccessException {
        AccessControlService.requireEmployeeDelete(session, "Delete employee");
        ensureEmployeeExists(employeeNumber);

        employeeDao.delete(employeeNumber);
        attendanceDao.deleteByEmployeeNumber(employeeNumber);
        requestDao.deleteByEmployeeNumber(employeeNumber);
        userDao.delete(employeeNumber);
    }

    public List<Employee> getAllEmployees(Session session) throws InvalidAccessException {
        if (!AccessControlService.canViewEmployeeDirectory(session)) {
            AccessControlService.deny("Employee directory access", "HR, FINANCE, or ADMIN");
        }
        return employeeDao.getAll();
    }

    public int getNewId() {
        List<Employee> empList = employeeDao.getAll();
        if (empList != null && !empList.isEmpty()) {
            Employee lastEmp = empList.get(empList.size() - 1);
            return lastEmp.getEmployeeNumber() + 1;
        }
        return 10001;
    }

    public void validateEmployeeData(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee payload is required.");
        }
        if (employee.getEmployeeNumber() <= 0) {
            throw new IllegalArgumentException("Invalid input: Employee Number must be numeric and greater than zero.");
        }

        employee.setFirstName(InputValidationService.requirePersonName("First Name", employee.getFirstName()));
        employee.setLastName(InputValidationService.requirePersonName("Last Name", employee.getLastName()));

        if (employee.getBirthday() == null) {
            throw new IllegalArgumentException("Invalid input: Birthday is required.");
        }
        validateBirthday(employee.getBirthday());

        if (employee.getContact() == null) {
            throw new IllegalArgumentException("Invalid input: Contact information is required.");
        }
        employee.getContact().setAddress(InputValidationService.requireAddress(employee.getContact().getAddress()));
        employee.getContact().setPhone(InputValidationService.requirePhoneNumber(employee.getContact().getPhone()));

        if (employee.getGovernmentID() == null) {
            throw new IllegalArgumentException("Invalid input: Government IDs are required.");
        }
        employee.getGovernmentID().setSss(InputValidationService.requireSssNumber(employee.getGovernmentID().getSss()));
        employee.getGovernmentID().setPhilHealth(InputValidationService.requirePhilHealthNumber(employee.getGovernmentID().getPhilHealth()));
        employee.getGovernmentID().setTin(InputValidationService.requireTinNumber(employee.getGovernmentID().getTin()));
        employee.getGovernmentID().setPagIbig(InputValidationService.requirePagIbigNumber(employee.getGovernmentID().getPagIbig()));

        if (employee.getPosition() == null) {
            throw new IllegalArgumentException("Invalid input: Position and department are required.");
        }
        employee.getPosition().setJobTitle(InputValidationService.requirePositionTitle(employee.getPosition().getJobTitle()));
        employee.getPosition().setDepartment(InputValidationService.requireLabel("Department", employee.getPosition().getDepartment()));

        if (employee.getSalary() == null) {
            throw new IllegalArgumentException("Invalid input: Salary information is required.");
        }
        InputValidationService.requireNonNegative("Basic Salary", employee.getSalary().getBasicSalary());
        InputValidationService.requireNonNegative("Rice Subsidy", employee.getSalary().getRiceSubsidy());
        InputValidationService.requireNonNegative("Phone Allowance", employee.getSalary().getPhoneAllowance());
        InputValidationService.requireNonNegative("Clothing Allowance", employee.getSalary().getClothingAllowance());

        employee.setStatus(InputValidationService.requireLabel("Status", employee.getStatus()));
        employee.setSupervisorName(InputValidationService.requireLabel("Supervisor Name", employee.getSupervisorName()));
    }

    private void validateBirthday(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        if (birthday.isAfter(today)) {
            throw new IllegalArgumentException("Invalid input: Birthday cannot be a future date.");
        }

        int age = Period.between(birthday, today).getYears();
        int minimumAge = ValidationConfig.minEmploymentAge();
        if (age < minimumAge) {
            throw new IllegalArgumentException("Invalid input: Employee must be at least " + minimumAge + " years old.");
        }
    }

    private void ensureUniqueEmployeeNumber(int employeeNumber) {
        if (employeeDao.findById(employeeNumber) != null) {
            throw new IllegalArgumentException("Employee Number already exists!");
        }
    }

    private void ensureUniqueEmployeeNumberForUpdate(int originalEmployeeNumber, int updatedEmployeeNumber) {
        Employee existing = employeeDao.findById(updatedEmployeeNumber);
        if (existing != null && originalEmployeeNumber != updatedEmployeeNumber) {
            throw new IllegalArgumentException("Employee Number already exists!");
        }
    }

    private void ensureEmployeeExists(int employeeNumber) {
        if (employeeDao.findById(employeeNumber) == null) {
            throw new IllegalArgumentException("Employee not found: " + employeeNumber);
        }
    }
}
