package com.compprog1282025.model.employee;

import java.time.YearMonth;

public class Payslip {
	private int employeeNumber;
	private YearMonth yearMonth;
	private double grossSalary;
	private double sss;
    private double philhealth;
    private double pagibig;
    private double withholding;
    private double netSalary;
    
    public Payslip(int employeeNumber, YearMonth yearMonth, double grossSalary, double sss, double philhealth,
			double pagibig, double withholding, double netSalary) {
		super();
		this.employeeNumber = employeeNumber;
		this.yearMonth = yearMonth;
		this.grossSalary = grossSalary;
		this.sss = sss;
		this.philhealth = philhealth;
		this.pagibig = pagibig;
		this.withholding = withholding;
		this.netSalary = netSalary;
	}

	public int getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(int employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public YearMonth getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(YearMonth yearMonth) {
		this.yearMonth = yearMonth;
	}

	public double getGrossSalary() {
		return grossSalary;
	}
	public void setGrossSalary(double grossSalary) {
		this.grossSalary = grossSalary;
	}

	public double getSss() {
		return sss;
	}
	public void setSss(double sss) {
		this.sss = sss;
	}

	public double getPhilhealth() {
		return philhealth;
	}
	public void setPhilhealth(double philhealth) {
		this.philhealth = philhealth;
	}

	public double getPagibig() {
		return pagibig;
	}
	public void setPagibig(double pagibig) {
		this.pagibig = pagibig;
	}

	public double getWithholding() {
		return withholding;
	}
	public void setWithholding(double withholding) {
		this.withholding = withholding;
	}

	public double getNetSalary() {
		return netSalary;
	}
	public void setNetSalary(double netSalary) {
		this.netSalary = netSalary;
	}
    
}
