package com.compprog1282025.ui.terminal;

import java.util.List;
import java.util.Scanner;

import com.compprog1282025.model.employee.*;
import com.compprog1282025.model.user.*;
import com.compprog1282025.service.*;

public class HRMenu extends BaseMenu {
	private EmployeeService employeeService;
	
	public HRMenu(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public void displayHRMenu(Session session, Scanner scanner) {
		
		Role role = session.getUser().getRole();
		
		boolean canView   = role.hasPermission(Permission.VIEW_EMPLOYEE);
	    boolean canCreate = role.hasPermission(Permission.CREATE_EMPLOYEE);
	    boolean canUpdate = role.hasPermission(Permission.UPDATE_EMPLOYEE);
	    boolean canDelete = role.hasPermission(Permission.DELETE_EMPLOYEE);
	    boolean canApprove = role.hasPermission(Permission.APPROVE_LEAVE);
		
		String choice = "default";
		while(!choice.equalsIgnoreCase("0")) {
			this.displayHeader("HR Menu");
			if(canView) {
				System.out.println("1. View all employees");
				System.out.println("2. Search for employee");
			}
			if(canCreate) {
				System.out.println("3. Add new employee");
			}
			if(canUpdate) {
				System.out.println("4. Update employee");
			}
			if(canDelete) {
				System.out.println("5. Delete employee");
			}
			if(canApprove) {
				System.out.println("6. Approve/Reject leave");
			}
			System.out.println("0. Exit");
			System.out.print("Select an option: ");
			choice = scanner.nextLine().trim();
			switch (choice) {
			case "0":
				break;
			case "1":
				if(canView) {
					displayAllEmployees(employeeService.getAllEmployees());
				}
				break;
			case "2":
				if(canView) {
					searchForEmployee(scanner);
				}
				break;
			case "3":
				if(canCreate) {
					// add create employee code
					addNewEmployee(scanner, session);
				}
				break;
			case "4":
				if(canUpdate) {
					// add update employee code
					updateEmployee(scanner, session);
				}
				break;
			case "5":
				if(canDelete) {
					// add delete employee code
					deleteEmployee(scanner, session);
				}
				break;
			default:
				System.out.println("Invalid input, please try again");
			}
		}
		
	}
	
	public void displayEmployeeInfo(Employee employee) {
		this.displaySubHeader("Employee Info");
		if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }
		
        System.out.println(employee.toString());
        System.out.println("Supervisor: " + employee.getSupervisorName());
        System.out.println("Birthdate: " + DateTimeUtil.convertDateToString(employee.getBirthday()));

        if (employee.getContact() != null) {
            System.out.println("--- Contact Information ---");
            System.out.println("Address: " + employee.getContact().getAddress());
            System.out.println("Phone Number: " + employee.getContact().getPhone());
        }

        if (employee.getSalary() != null) {
            System.out.println("--- Compensation ---");
            System.out.printf("Basic Salary: %.2f\n", employee.getSalary().getBasicSalary());
            System.out.printf("Hourly Rate: %.2f\n", employee.getSalary().calculateHourlyRate());
            System.out.printf("Rice Subsidy: %.2f\n", employee.getSalary().getRiceSubsidy());
            System.out.printf("Phone Allowance: %.2f\n", employee.getSalary().getPhoneAllowance());
            System.out.printf("Clothing Allowance: %.2f\n", employee.getSalary().getClothingAllowance());
        }
	}
	
	public void displayAllEmployees(List<Employee> employeeList) {
		displaySubHeader("View all employees");
		System.out.println(String.format(
				"%-15s | %-30s | %-15s | %-30s | %-30s | %-12s | %-80s | %-12s | %-12s | %-12s | %-12s | %-15s | %-18s |", 
				"Employee Number", "Full Name", "Status", "Position", "Supervisor Name", "Birthdate", "Address", "Phone Number",
				"Basic Salary", "Hourly Rate", "Rice Subsidy", "Phone Allowance", "Clothing Allowance"));
		for(Employee e: employeeList) {
			System.out.println(String.format(
					"%-15s | %-30s | %-15s | %-30s | %-30s | %-12s | %-80s | %-12s | %-12.2f | %-12.2f | %-12.2f | %-15.2f | %-18.2f |", 
					String.valueOf(e.getEmployeeNumber()), e.getFullName(), e.getStatus(), e.getPosition(), e.getSupervisorName(),
					e.getBirthday(), e.getContact().getAddress(), e.getContact().getPhone(),
					e.getSalary().getBasicSalary(), e.getSalary().calculateHourlyRate(), e.getSalary().getRiceSubsidy(), e.getSalary().getPhoneAllowance(), e.getSalary().getClothingAllowance()));
		}
	}
	
	public void searchForEmployee(Scanner scanner) {
		this.displaySubHeader("Search for employee");
		System.out.print("Enter Employee ID that you want to search for: ");
		try {
			int searchId = Integer.valueOf(scanner.nextLine());
			Employee emp = employeeService.getEmployee(searchId);
			if(emp == null) {
				System.out.println("Employee not found.");
				return;
			}
			displayEmployeeInfo(emp);
		} catch (Exception e) {
			System.out.println("[Search for employee] Error, please try again.");
			e.printStackTrace();
		}
		
	}
	
	public void addNewEmployee(Scanner scanner, Session session) {
		this.displaySubHeader("Add new employee");
		System.out.println("Please enter the employee's details accordingly.");
		try {
			System.out.print("Enter employee's first name: ");
			String fname = scanner.nextLine().trim();
			System.out.print("Enter employee's last name: ");
			String lname = scanner.nextLine().trim();
			System.out.print("Enter employee's birthday (mm/dd/yyyy): ");
			String bday = scanner.nextLine().trim();
			System.out.print("Enter employee's address: ");
			String addr = scanner.nextLine().trim();
			System.out.print("Enter employee's phone number: ");
			String phone = scanner.nextLine().trim();
			System.out.print("Enter employee's SSS: ");
			String sss = scanner.nextLine().trim();
			System.out.print("Enter employee's PhilHealth: ");
			String philhealth = scanner.nextLine().trim();
			System.out.print("Enter employee's TIN: ");
			String tin = scanner.nextLine().trim();
			System.out.print("Enter employee's PagIbig: ");
			String pagibig = scanner.nextLine().trim();
			System.out.print("Enter employee's position: ");
			String pos = scanner.nextLine().trim();
			System.out.print("Enter employee's department: ");
			String dept = scanner.nextLine().trim();
			System.out.print("Enter employee's basic salary: ");
			String salary = scanner.nextLine().trim();
			System.out.print("Enter employee's status (Regular/Probationary): ");
			String status = scanner.nextLine().trim();
			System.out.print("Enter employee's supervisor name: ");
			String sup = scanner.nextLine().trim();
			
			ContactInfo contact = new ContactInfo(addr, phone);
			GovernmentID govtId = new GovernmentID(sss, philhealth, tin, pagibig);
			Position position = new Position(pos, dept);
			Compensation comp = new Compensation(Double.valueOf(salary));
			
			Employee newEmp = new Employee(employeeService.getNewId(), fname, lname, DateTimeUtil.convertStringToDate(bday), contact, govtId, position, comp, status, sup);
			employeeService.addEmployee(newEmp, session);
			System.out.println("New employee added successfully");
		} catch (Exception e) {
			System.out.println("Error adding new employee. Please try again.");
			e.printStackTrace();
		}
	}
	
	public void updateEmployee(Scanner scanner, Session session) {
		this.displaySubHeader("Update employee details");
		System.out.println("Enter the employee number to update: ");
		try {
			boolean edits = false;
			int empId = Integer.valueOf(scanner.nextLine());
			Employee emp = employeeService.getEmployee(empId);
			if(emp == null) {
				System.out.println("Employee number not found, please try again.");
				return;
			}
			String[] fields = {"Name", "Birthday", "Contact", "Government Info", "Position", "Salary", "Status"};
			for (int i = 0; i < fields.length; i++) {
				System.out.println(i+1 + ": " + fields[i]);
			}
			System.out.println("0. Exit");
			System.out.print("Select a field to update: ");
			String choice = scanner.nextLine().trim();
			switch (choice) {
			case "0":
				break;
			case "1":
				System.out.print("Enter employee's first name: ");
				String fname = scanner.nextLine().trim();
				System.out.print("Enter employee's last name: ");
				String lname = scanner.nextLine().trim();
				emp.setFirstName(fname);
				emp.setLastName(lname);
				edits = true;
				break;
			case "2":
				System.out.print("Enter employee's birthday (mm/dd/yyyy): ");
				String bday = scanner.nextLine().trim();
				emp.setBirthday(DateTimeUtil.convertStringToDate(bday));
				edits = true;
				break;
			case "3":
				System.out.print("Enter employee's address: ");
				String addr = scanner.nextLine().trim();
				System.out.print("Enter employee's phone number: ");
				String phone = scanner.nextLine().trim();
				ContactInfo ctc = new ContactInfo(addr, phone);
				emp.setContact(ctc);
				edits = true;
				break;
			case "4":
				System.out.print("Enter employee's SSS: ");
				String sss = scanner.nextLine().trim();
				System.out.print("Enter employee's PhilHealth: ");
				String philhealth = scanner.nextLine().trim();
				System.out.print("Enter employee's TIN: ");
				String tin = scanner.nextLine().trim();
				System.out.print("Enter employee's PagIbig: ");
				String pagibig = scanner.nextLine().trim();
				GovernmentID govtId = new GovernmentID(sss, philhealth, tin, pagibig);
				emp.setGovernmentID(govtId);
				edits = true;
				break;
			case "5":
				System.out.print("Enter employee's position: ");
				String pos = scanner.nextLine().trim();
				System.out.print("Enter employee's department: ");
				String dept = scanner.nextLine().trim();
				System.out.print("Enter employee's supervisor name: ");
				String sup = scanner.nextLine().trim();
				Position post = new Position(pos, dept);
				emp.setPosition(post);
				emp.setSupervisorName(sup);
				edits = true;
				break;
			case "6":
				System.out.print("Enter employee's basic salary: ");
				String salary = scanner.nextLine().trim();
				Compensation comp = new Compensation(Double.valueOf(salary));
				emp.setSalary(comp);
				edits = true;
				break;
			case "7":
				System.out.print("Enter employee's status (Regular/Probationary): ");
				String status = scanner.nextLine().trim();
				emp.setStatus(status);
				edits = true;
				break;
			default:
				System.out.println("Invalid input, please try again.");
			}
			employeeService.updateEmployee(emp, session);
			if (edits) {
				System.out.println("Employee details updated.");
			}
		} catch (Exception e) {
			System.out.println("Error updating employee details. Please try again.");
			e.printStackTrace();
		}
	}
	
	public void deleteEmployee(Scanner scanner, Session session) {
		displaySubHeader("Delete employee");
		System.out.println("Enter employee number to delete: ");
		try {
			int empId = Integer.valueOf(scanner.nextLine());
			Employee emp = employeeService.getEmployee(empId);
			if(emp == null) {
				System.out.println("Employee number not found, please try again.");
				return;
			}
			System.out.println("Are you sure you want to delete the employee? This cannot be undone!");
			System.out.println("Confirm deletion (Y/N): ");
			String choice = scanner.nextLine();
			if(choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
				employeeService.removeEmployee(emp.getEmployeeNumber(), session);
			}
			System.out.println("Employee deleted.");
		} catch (Exception e) {
			System.out.println("Error deleting employee, please try again.");
			e.printStackTrace();
		}
	}
}
