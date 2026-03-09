package com.compprog1282025.service;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;

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

    public List<Employee> getEmployeeDirectory() {
        return new ArrayList<>(employeeDao.getAll());
    }

    public void addEmployee(Employee employee, Session session) throws InvalidAccessException {
        validateEmployeeData(employee);
        ensureUniqueEmployeeNumber(employee.getEmployeeNumber());

        Role role = session.getUser().getRole();
        if (role.hasPermission(Permission.CREATE_EMPLOYEE)) {
            employeeDao.insert(employee);
        } else {
            throw new InvalidAccessException("You do not have the permissions to perform this action.");
        }
    }

    public void createEmployee(Employee employee, Session session) throws InvalidAccessException {
        addEmployee(employee, session);
    }

    public void updateEmployee(Employee employee, Session session) throws InvalidAccessException {
        updateEmployee(employee.getEmployeeNumber(), employee, session);
    }

    public void updateEmployee(int originalEmployeeNumber, Employee employee, Session session) throws InvalidAccessException {
        validateEmployeeData(employee);
        ensureEmployeeExists(originalEmployeeNumber);
        ensureUniqueEmployeeNumberForUpdate(originalEmployeeNumber, employee.getEmployeeNumber());

        Role role = session.getUser().getRole();
        if (role.hasPermission(Permission.UPDATE_EMPLOYEE)) {
            employeeDao.updateByEmployeeNumber(originalEmployeeNumber, employee);
        } else {
            throw new InvalidAccessException("You do not have the permissions to perform this action.");
        }
    }

    public void archiveEmployee(int employeeNumber, Session session) throws InvalidAccessException {
        Role role = session.getUser().getRole();
        if (!role.hasPermission(Permission.UPDATE_EMPLOYEE)) {
            throw new InvalidAccessException("You do not have the permissions to perform this action.");
        }

        Employee employee = employeeDao.findById(employeeNumber);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found: " + employeeNumber);
        }

        employeeDao.archive(employeeNumber);
    }

    public void removeEmployee(int employeeNumber, Session session) throws InvalidAccessException {
        Role role = session.getUser().getRole();
        if (!role.hasPermission(Permission.DELETE_EMPLOYEE)) {
            throw new InvalidAccessException("You do not have the permissions to perform this action.");
        }

        ensureEmployeeExists(employeeNumber);

        employeeDao.delete(employeeNumber);
        attendanceDao.deleteByEmployeeNumber(employeeNumber);
        requestDao.deleteByEmployeeNumber(employeeNumber);
        userDao.delete(employeeNumber);
    }

    public List<Employee> getAllEmployees() {
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
            throw new IllegalArgumentException("Employee number must be greater than zero.");
        }
        if (isBlank(employee.getFirstName()) || isBlank(employee.getLastName())) {
            throw new IllegalArgumentException("First name and last name are required.");
        }
        if (employee.getBirthday() == null) {
            throw new IllegalArgumentException("Birthday is required.");
        }
        if (employee.getContact() == null || isBlank(employee.getContact().getAddress()) || isBlank(employee.getContact().getPhone())) {
            throw new IllegalArgumentException("Address and phone are required.");
        }
        if (employee.getGovernmentID() == null
                || isBlank(employee.getGovernmentID().getSss())
                || isBlank(employee.getGovernmentID().getPhilHealth())
                || isBlank(employee.getGovernmentID().getTin())
                || isBlank(employee.getGovernmentID().getPagIbig())) {
            throw new IllegalArgumentException("All government IDs are required.");
        }
        validateGovernmentIdFormat("SSS #", employee.getGovernmentID().getSss());
        validateGovernmentIdFormat("PhilHealth #", employee.getGovernmentID().getPhilHealth());
        validateGovernmentIdFormat("TIN #", employee.getGovernmentID().getTin());
        validateGovernmentIdFormat("Pag-IBIG #", employee.getGovernmentID().getPagIbig());

        if (employee.getPosition() == null || isBlank(employee.getPosition().getJobTitle()) || isBlank(employee.getPosition().getDepartment())) {
            throw new IllegalArgumentException("Position and department are required.");
        }
        if (employee.getSalary() == null) {
            throw new IllegalArgumentException("Salary information is required.");
        }
        if (employee.getSalary().getBasicSalary() < 0
                || employee.getSalary().getRiceSubsidy() < 0
                || employee.getSalary().getPhoneAllowance() < 0
                || employee.getSalary().getClothingAllowance() < 0) {
            throw new IllegalArgumentException("Compensation values cannot be negative.");
        }
        if (isBlank(employee.getStatus())) {
            throw new IllegalArgumentException("Employee status is required.");
        }
    }

    private void ensureUniqueEmployeeNumber(int employeeNumber) {
        if (employeeDao.findById(employeeNumber) != null) {
            throw new IllegalArgumentException("Employee Number already exists. Please enter a unique Employee Number.");
        }
    }

    private void ensureUniqueEmployeeNumberForUpdate(int originalEmployeeNumber, int updatedEmployeeNumber) {
        Employee existing = employeeDao.findById(updatedEmployeeNumber);
        if (existing != null && originalEmployeeNumber != updatedEmployeeNumber) {
            throw new IllegalArgumentException("Employee Number already exists. Please enter a unique Employee Number.");
        }
    }

    private void ensureEmployeeExists(int employeeNumber) {
        if (employeeDao.findById(employeeNumber) == null) {
            throw new IllegalArgumentException("Employee not found: " + employeeNumber);
        }
    }

    private void validateGovernmentIdFormat(String field, String value) {
        if (value == null || !value.matches("[0-9-]+")) {
            throw new IllegalArgumentException(field + " format is invalid.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
