package com.compprog1282025.service.dto;

import com.compprog1282025.model.employee.Payslip;

public class PayslipComputation {
    private final Payslip payslip;
    private final double basicSalary;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;
    private final double totalEarnings;
    private final double totalContributions;
    private final double totalDeductions;

    public PayslipComputation(
            Payslip payslip,
            double basicSalary,
            double riceSubsidy,
            double phoneAllowance,
            double clothingAllowance,
            double totalEarnings,
            double totalContributions,
            double totalDeductions
    ) {
        this.payslip = payslip;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.totalEarnings = totalEarnings;
        this.totalContributions = totalContributions;
        this.totalDeductions = totalDeductions;
    }

    public Payslip getPayslip() {
        return payslip;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public double getTotalContributions() {
        return totalContributions;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }
}
