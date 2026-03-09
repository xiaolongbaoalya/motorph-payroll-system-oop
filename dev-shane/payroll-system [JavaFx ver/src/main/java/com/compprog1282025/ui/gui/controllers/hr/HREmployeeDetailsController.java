package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.InvalidAccessException;

import java.util.List;

public class HREmployeeDetailsController {
    private final EmployeeService employeeService;

    public HREmployeeDetailsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public List<Employee> loadDirectory(Session session) {
        return employeeService.getEmployeeDirectory();
    }

    public Employee viewEmployee(int employeeNumber) {
        return employeeService.getEmployeeDetails(employeeNumber);
    }

    public void updateEmployee(Employee employee, Session session) throws InvalidAccessException {
        employeeService.updateEmployee(employee, session);
    }

    public void archiveEmployee(int employeeNumber, Session session) throws InvalidAccessException {
        employeeService.archiveEmployee(employeeNumber, session);
    }
}
