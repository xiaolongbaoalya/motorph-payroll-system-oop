package com.compprog1282025.model.employee;

// This is a Mini Data Container for Employees' salary and allowances

// Money logic in here!

public class Compensation{
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;

    // main constructor to initialize all fields
    public Compensation(double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }


    // overload constructor
    public Compensation(double basicSalary) {
    	this.basicSalary = basicSalary;
    	this.riceSubsidy = 1500;
    	if (basicSalary >= 60000) {
    		this.phoneAllowance = 2000;
    		this.clothingAllowance = 1000;
    	} else if (basicSalary >= 50000) {
    		this.phoneAllowance = 1000;
    		this.clothingAllowance = 1000;
    	} else if (basicSalary >= 40000) {
    		this.phoneAllowance = 800;
    		this.clothingAllowance = 800;
    	} else {
    		this.phoneAllowance = 500;
    		this.clothingAllowance = 500;
    	}
    }
    
    // Getters and Setters - allow access and modification of the compensation fields for an employee
    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(double riceSubsidy) {
        this.riceSubsidy = riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        this.clothingAllowance = clothingAllowance;
    }
    
    public double calculateHourlyRate() {
    	return basicSalary/21/8;
    }
    
    public double calculateGrossSemiRate() {
    	return basicSalary/2;
    }

// Formatting to show currency style

    // @Override WILL COME BACK TO THIS LATER
    // public String toString() {

    //     // This is an action of this class, it calculates locally just for display
    //     double totalBenefits = riceSubsidy + phoneAllowance + clothingAllowance;

    //     return String.format("Basic: %.2f | Total Benefits: %.2f", basicSalary, getTotalBenefits());
    // }

}
