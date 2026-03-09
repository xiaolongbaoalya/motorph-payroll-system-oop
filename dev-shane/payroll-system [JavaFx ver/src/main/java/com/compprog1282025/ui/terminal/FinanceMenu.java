package com.compprog1282025.ui.terminal;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.DateTimeUtil;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;

public class FinanceMenu extends BaseMenu {
	private FinanceService financeService;
	private EmployeeService employeeService;
	
	public FinanceMenu(FinanceService financeService, EmployeeService employeeService) {
		this.financeService = financeService;
		this.employeeService = employeeService;
	}
	
	public void displayFinanceMenu(Session session, Scanner scanner) {
		
		Role role = session.getUser().getRole();
		
		boolean canCalculate = role.hasPermission(Permission.CALCULATE_SALARY);
		boolean canGenerate = role.hasPermission(Permission.GENERATE_PAYSLIP);
		
		String choice = "default";
		while(!choice.equalsIgnoreCase("0")) {
			this.displayHeader("Finance Menu");
			if(canCalculate) { 
				System.out.println("1. Calculate employee salary");
			}
			if(canGenerate) { 
				System.out.println("1. Generate employee payslip");
			}
			System.out.println("0. Exit");
			System.out.print("Select an option: ");
			choice = scanner.nextLine().trim();
			switch (choice) {
			case "0":
				break;
			case "1":
				if(canCalculate) {
					findEmployeeSalary(scanner);
				}
				break;
			case "2":
				if(canGenerate) {
					generateEmployeePayslip(scanner);
				}
				break;
			default:
				System.out.println("Invalid input, please try again");
			}
		}
	}
	
	public void findEmployeeSalary(Scanner scanner) {
		displaySubHeader("Find employee salary");
		
		System.out.print("Enter employee number to view: ");
		try {
			int employeeNumber = Integer.valueOf(scanner.nextLine().trim());
			Employee employee = employeeService.getEmployee(employeeNumber);
			if(employee == null) {
				System.out.println("Employee number not found, please try again.");
				return;
			}
			System.out.print("Enter year: ");
			int year = Integer.valueOf(scanner.nextLine().trim());
			System.out.print("Enter month number: ");
			int month = Integer.valueOf(scanner.nextLine().trim());
			if (DateTimeUtil.isValidYear(year) && DateTimeUtil.isValidMonth(month)) {
				double grossSalary = financeService.calculateMonthSalary(employee, year, month);
				double netSalary = 0;
				if(grossSalary > 0) {
					netSalary = financeService.calculateNet(grossSalary);
				}
				System.out.println(String.format("Expected net salary: %.2f", financeService.calculateNet(employee.getSalary().getBasicSalary())));
				System.out.println(String.format("Actual net salary for %d-%d: %.2f", month, year, netSalary));
			} else {
				System.out.println("Invalid year/month input. Please try again.");
			}
		} catch (Exception e) {
			System.out.println("Error with salary calculation, please try again.");
		}
	}
	
	public void generateEmployeePayslip(Scanner scanner) {
		displaySubHeader("Generate payslip");
		System.out.print("Enter employee number to view: ");
		try {
			int employeeNumber = Integer.valueOf(scanner.nextLine().trim());
			Employee employee = employeeService.getEmployee(employeeNumber);
			if(employee == null) {
				System.out.println("Employee number not found, please try again.");
				return;
			}
			System.out.print("Enter year: ");
			int year = Integer.valueOf(scanner.nextLine().trim());
			System.out.print("Enter month number: ");
			int month = Integer.valueOf(scanner.nextLine().trim());
			if (DateTimeUtil.isValidYear(year) && DateTimeUtil.isValidMonth(month)) {
				double grossSalary = financeService.calculateMonthSalary(employee, year, month);
				double netSalary = 0;
				if(grossSalary > 0) {
					double sss = financeService.calculateSSS(grossSalary);
					double philHealth = financeService.calculatePhilhealth(grossSalary);
					double pagIbig = financeService.calculatePagibig(grossSalary);
					double taxableIncome = grossSalary - (sss + philHealth + pagIbig);
					double withholdingTax = financeService.calculateWithholdingTax(taxableIncome);
					netSalary = taxableIncome - withholdingTax;
					displaySubHeader(String.format("PAYSLIP FOR %s (%d-%d)", employee.getFullName(), month, year));
					System.out.println(String.format("Gross Salary: %.2f", grossSalary));
					System.out.println("----- Deductions -----");
					System.out.println(String.format("SSS: %.2f", sss));
					System.out.println(String.format("Philhealth: %.2f", philHealth));
					System.out.println(String.format("Pag-ibig: %.2f", pagIbig));
					System.out.println("----- Tax -----");
					System.out.println(String.format("Withholding tax: %.2f", withholdingTax));
					System.out.println("----- Final amount -----");
					System.out.println(String.format("Net salary: %.2f", netSalary));
				} else {
					System.out.println("No valid payslip to generate.");
				}
				
			} else {
				System.out.println("Invalid year/month input. Please try again.");
			}
		} catch (Exception e) {
			System.out.println("Error with salary calculation, please try again.");
		}
	}

}
