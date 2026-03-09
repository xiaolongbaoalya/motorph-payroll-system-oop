package com.compprog1282025.ui.terminal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

import com.compprog1282025.model.employee.*;
import com.compprog1282025.model.user.*;
import com.compprog1282025.service.*;

public class EmployeeMenu extends BaseMenu {
	private EmployeeService employeeService;
	private AttendanceService attendanceService;
	private FinanceService financeService;
	private LeaveService leaveService;
	
	public EmployeeMenu(EmployeeService employeeService, AttendanceService attendanceService, FinanceService financeService, LeaveService leaveService) {
		this.employeeService = employeeService;
		this.attendanceService = attendanceService;
		this.financeService = financeService;
		this.leaveService = leaveService;
	}
	
	public void displayPersonalMenu(Session session, Scanner scanner) {
//		Role role = session.getUser().getRole();
		String choice = "default";
		while(!choice.equalsIgnoreCase("0")) {
			this.displayHeader("Personal Functions");
			System.out.println("1. View Profile");
			System.out.println("2. Time-In Attendance");
			if(session.getAttendance() != null) {
				System.out.println("3. Time-Out Attendance");
			}
			System.out.println("4. Check Salary");
			System.out.println("5. Apply Leave");
			System.out.println("0. Exit");
			System.out.print("Select an option: ");
			choice = scanner.nextLine().trim();
			switch (choice) {
			case "0":
				break;
			case "1":
				displayProfile(session);
				break;
			case "2":
				attendanceService.timeInAttendance(session, employeeService.getEmployee(session.getUser().getEmployeeNumber()));
				System.out.println("Attendance has been timed-in.");
				break;
			case "3":
				if(session.getAttendance() != null) {
					attendanceService.timeOutAttendance(session, employeeService.getEmployee(session.getUser().getEmployeeNumber()));
					System.out.println("Attendance has been timed-out.");
				} else {
					System.out.println("Invalid input, please try again.");
				}
				break;
			case "4":
				// call salary calculation here
				displaySalary(session, scanner);
				break;
			case "5":
				displayLeaveApplication(session, scanner);
				break;
			default:
				System.out.println("Invalid input, please try again.");
			}
		}
	}
	
	
	public void displayProfile(Session session) {
		Employee current = employeeService.getEmployee(session.getUser().getEmployeeNumber());
		this.displaySubHeader("Employee Info");
		if (current == null) {
            System.out.println("Employee not found.");
            return;
        }
		
        System.out.println(current.toString());
        System.out.println("Supervisor: " + current.getSupervisorName());
        System.out.println("Birthdate: " + DateTimeUtil.convertDateToString(current.getBirthday()));

        if (current.getContact() != null) {
            System.out.println("--- Contact Information ---");
            System.out.println("Address: " + current.getContact().getAddress());
            System.out.println("Phone Number: " + current.getContact().getPhone());
        }

        if (current.getSalary() != null) {
            System.out.println("--- Compensation ---");
            System.out.printf("Basic Salary: %.2f\n", current.getSalary().getBasicSalary());
            System.out.printf("Hourly Rate: %.2f\n", current.getSalary().calculateHourlyRate());
            System.out.printf("Rice Subsidy: %.2f\n", current.getSalary().getRiceSubsidy());
            System.out.printf("Phone Allowance: %.2f\n", current.getSalary().getPhoneAllowance());
            System.out.printf("Clothing Allowance: %.2f\n", current.getSalary().getClothingAllowance());
        }
	}
	
	public void displaySalary(Session session, Scanner scanner) {
		displaySubHeader("View salary");
		try {
			Employee current = employeeService.getEmployee(session.getUser().getEmployeeNumber());
			System.out.print("Enter year: ");
			int year = Integer.valueOf(scanner.nextLine().trim());
			System.out.print("Enter month number: ");
			int month = Integer.valueOf(scanner.nextLine().trim());
			if (DateTimeUtil.isValidYear(year) && DateTimeUtil.isValidMonth(month)) {
				double grossSalary = financeService.calculateMonthSalary(current, year, month);
				double netSalary = 0;
				if(grossSalary > 0) {
					netSalary = financeService.calculateNet(grossSalary);
				}
				System.out.println(String.format("Expected net salary: %.2f", financeService.calculateNet(current.getSalary().getBasicSalary())));
				System.out.println(String.format("Actual net salary for %d-%d: %.2f", month, year, netSalary));
			} else {
				System.out.println("Invalid year/month input. Please try again.");
			}
		} catch (Exception e) {
			System.out.println("Error with salary calculation, please try again.");
		}
	}
	
	public void displayLeaveApplication(Session session, Scanner scanner) {
		displaySubHeader("Leave application");
		try {
			Employee current = employeeService.getEmployee(session.getUser().getEmployeeNumber());
			System.out.print("Enter date of leave (mm/dd/yyyy): ");
			LocalDate date = DateTimeUtil.convertStringToDate(scanner.nextLine());
			System.out.print("Enter reason for leave: ");
			String reason = scanner.nextLine().trim();
			String leaveId = Integer.toString(current.getEmployeeNumber()) + "_" + DateTimeUtil.convertDateToString(date);
			Leave leave = new Leave(leaveId, current.getEmployeeNumber(), date, reason);
			leaveService.applyLeave(leave, session);
		} catch (Exception e) {
			System.out.println("Error with leave application, please try again.");
			System.out.println(e.getMessage());
		}
	}

}
