package com.compprog1282025.service;

import com.compprog1282025.model.Compensation;
import com.compprog1282025.model.ContactInfo;
import com.compprog1282025.model.Employee;

import java.util.List;

public class EmployeeService {

    private final List<Employee> employeeList;

    public EmployeeService(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void printAllEmployeeData() {
        for (Employee e : employeeList) {
            System.out.println(e);  // This calls Employee.toString()
            System.out.println(" - ----------");
        }
    }

    public void printEmployeeData(Employee employee) {
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("=== Employee Record ===");
        System.out.println("Employee Number: " + employee.getEmployeeNumber());
        System.out.println("Full Name: " + employee.getFullName());
        System.out.println("Position: " + employee.getPosition());
        System.out.println("Birthdate: " + employee.getBirthday());

        ContactInfo contact = employee.getContact();
        if (contact != null) {
            System.out.println("--- Contact Information ---");
            System.out.println("Address: " + contact.getAddress());
            System.out.println("Phone Number: " + contact.getPhoneNumber());
        }

        Compensation comp = employee.getCompensation();
        if (comp != null) {
            System.out.println("--- Compensation ---");
            System.out.printf("Hourly Rate: %.2f\n", comp.getHourlyRate());
            System.out.printf("Rice Subsidy: %.2f\n", comp.getRiceSubsidy());
            System.out.printf("Phone Allowance: %.2f\n", comp.getPhoneAllowance());
            System.out.printf("Clothing Allowance: %.2f\n", comp.getClothingAllowance());
        }
    }

    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    public Employee findEmployeeByNumber(int employeeNumber) {
        return employeeList.stream()
                .filter(e -> e.getEmployeeNumber() == employeeNumber)
                .findFirst()
                .orElse(null);
    }

    public Employee findEmployeeByFullName(String fullName) {
    return employeeList.stream()
        .filter(e -> e.getFullName().equalsIgnoreCase(fullName))
        .findFirst()
        .orElse(null);
    }


}
