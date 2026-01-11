package com.compprog1282025.service;

import com.compprog1282025.model.Employee;
import com.compprog1282025.util.DeductionsUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


public class PayrollService {

    private final AttendanceService attendanceService;

    public PayrollService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public double calculateMonthlySalary(Employee employee, YearMonth month) {
        double hoursWorked = attendanceService.calculateMonthlyHours(employee, month);
        double baseSalary = hoursWorked * employee.getHourlyRate();

        // Add fixed allowances
        double allowances = employee.getCompensation().getRiceSubsidy()
                           + employee.getCompensation().getPhoneAllowance()
                           + employee.getCompensation().getClothingAllowance();

        return baseSalary + allowances;
    }

    public double calculateWeeklySalary(Employee employee, LocalDate referenceDate) {
    double hoursWorked = attendanceService.calculateFixedWeekHours(employee, referenceDate);
    double baseSalary = hoursWorked * employee.getHourlyRate();

    // Prorating fixed monthly allowances for a 4-week month (1/4th of each)
    double weeklyAllowances = (employee.getCompensation().getRiceSubsidy()
                            + employee.getCompensation().getPhoneAllowance()
                            + employee.getCompensation().getClothingAllowance()) / 4.0;

    return baseSalary + weeklyAllowances;
    }


    public double calculateNetSalary(Employee employee, YearMonth month) {
        double grossSalary = calculateMonthlySalary(employee, month);

        // Deductions
        double philHealth = DeductionsUtil.calculatePhilhealth(grossSalary);
        double pagIbig = DeductionsUtil.calculatePagIbig(grossSalary);
        double sss = DeductionsUtil.calculateSSS(grossSalary);
        double taxableIncome = grossSalary - (philHealth + pagIbig + sss);
        double withholdingTax = DeductionsUtil.calculateWithholdingTax(taxableIncome);

        double totalDeductions = philHealth + pagIbig + sss + withholdingTax;

        return grossSalary - totalDeductions;
    }

    public double calculateNetWeeklySalary(Employee employee, LocalDate referenceDate) {
    double grossWeeklySalary = calculateWeeklySalary(employee, referenceDate);

    // Deductions (prorated to weekly amounts — 1/4th of monthly deductions)
    double philHealth = DeductionsUtil.calculatePhilhealth(grossWeeklySalary);
    double pagIbig = DeductionsUtil.calculatePagIbig(grossWeeklySalary);
    double sss = DeductionsUtil.calculateSSS(grossWeeklySalary);

    double taxableIncome = grossWeeklySalary - (philHealth + pagIbig + sss);
    double withholdingTax = DeductionsUtil.calculateWithholdingTax(taxableIncome);

    double totalDeductions = philHealth + pagIbig + sss + withholdingTax;

    return grossWeeklySalary - totalDeductions;
    }

    public void exportPayrollReportToCSV(List<Employee> employees, YearMonth ym, String absolutePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    

}
