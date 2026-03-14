package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.InvalidAccessException;

public class HRAddEmployeeController {
    private final EmployeeService employeeService;

    public HRAddEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void addEmployee(Employee employee, Session session) throws InvalidAccessException {
        employeeService.createEmployee(employee, session);
    }
}
