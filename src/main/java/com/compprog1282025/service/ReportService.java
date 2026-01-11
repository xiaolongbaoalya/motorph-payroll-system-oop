package com.compprog1282025.service;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.compprog1282025.Main;
import com.compprog1282025.model.Compensation;
import com.compprog1282025.model.Employee;
import com.compprog1282025.util.DeductionsUtil;

public class ReportService {

    private final PayrollService payrollService;
    private final AttendanceService attendanceService;

    public ReportService(PayrollService payrollService, AttendanceService attendanceService) {
        this.payrollService = payrollService;
        this.attendanceService = attendanceService;
    }

    public void printPayrollReport(List<Employee> employees, YearMonth month) {
        System.out.println("\nPayroll Report for " + month);
        for (Employee e : employees) {
            double gross = payrollService.calculateMonthlySalary(e, month);
            double net = payrollService.calculateNetSalary(e, month);
            System.out.printf("[%d] %s\n", e.getEmployeeNumber(), e.getFullName());
            System.out.printf("  Gross Salary: %.2f\n", gross);
            System.out.printf("  Net Salary:   %.2f\n", net);
            System.out.println();
        }
    }

    public void printPayrollForEmployee(Employee emp, Compensation compensation, YearMonth month) {
        System.out.printf("\n[%d] %s\n", emp.getEmployeeNumber(), emp.getFullName());
        System.out.println("Position: " + emp.getPosition());
        System.out.printf("Rate per Hour: %.2f\n", compensation.getHourlyRate());

        double gross = payrollService.calculateMonthlySalary(emp, month);
        double sss = DeductionsUtil.calculateSSS(gross);
        double philhealth = DeductionsUtil.calculatePhilhealth(gross);
        double pagibig = DeductionsUtil.calculatePagIbig(gross);
        double taxable = gross - (sss + philhealth + pagibig);
        double tax = DeductionsUtil.calculateWithholdingTax(taxable);
        double net = payrollService.calculateNetSalary(emp, month);

        System.out.printf("\n=== Payroll for %s ===\n", month);
        System.out.printf("Gross Salary: %.2f\n", gross);
        System.out.printf("SSS Deduction: %.2f\n", sss);
        System.out.printf("PhilHealth Deduction: %.2f\n", philhealth);
        System.out.printf("Pag-IBIG Deduction: %.2f\n", pagibig);
        System.out.printf("Withholding Tax: %.2f\n", tax);
        System.out.printf("Net Salary: %.2f\n", net);
        }
    
    public void printWeeklyPayrollForEmployee(Employee emp, Compensation compensation, LocalDate referenceDate) {
        System.out.printf("\n[%d] %s\n", emp.getEmployeeNumber(), emp.getFullName());
        System.out.println("Position: " + emp.getPosition());
        System.out.printf("Rate per Hour: %.2f\n", compensation.getHourlyRate());

        // Get weekly salary data
        double gross = payrollService.calculateWeeklySalary(emp, referenceDate);
        double sss = DeductionsUtil.calculateSSS(gross);
        double philhealth = DeductionsUtil.calculatePhilhealth(gross);
        double pagibig = DeductionsUtil.calculatePagIbig(gross);
        double taxable = gross - (sss + philhealth + pagibig);
        double tax = DeductionsUtil.calculateWithholdingTax(taxable);
        double net = payrollService.calculateNetWeeklySalary(emp, referenceDate);

        // Get the correct week range from AttendanceService
        LocalDate[] weekRange = attendanceService.calculateFixedWeekRange(referenceDate);
        LocalDate startOfWeek = weekRange[0];
        LocalDate endOfWeek = weekRange[1];

        // Print payroll summary
        System.out.printf("\n=== Payroll for Week: %s to %s ===\n", startOfWeek, endOfWeek);
        System.out.printf("Gross Salary: %.2f\n", gross);
        System.out.printf("SSS Deduction: %.2f\n", sss);
        System.out.printf("PhilHealth Deduction: %.2f\n", philhealth);
        System.out.printf("Pag-IBIG Deduction: %.2f\n", pagibig);
        System.out.printf("Withholding Tax: %.2f\n", tax);
        System.out.printf("Net Salary: %.2f\n", net);
    }


    public void exportPayrollReportToCSV(List<Employee> employees, YearMonth month, String filePath) {
    try {
        Path projectRoot = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                                .getParent().getParent();

        File file = new File(filePath);

        if (!file.isAbsolute()) {
            file = projectRoot.resolve(filePath).toFile();
        }

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            for (Employee emp : employees) {
                double gross = payrollService.calculateMonthlySalary(emp, month);
                double net = payrollService.calculateNetSalary(emp, month);
                writer.printf("%d,%s,%.2f,%.2f%n",
                        emp.getEmployeeNumber(),
                        emp.getFullName(),
                        gross,
                        net);
            }

            System.out.println("Payroll report exported successfully to: " + file.getAbsolutePath());
        }
    } catch (Exception e) {
        System.out.println("Failed to export payroll report: " + e.getMessage());
    }
    }
}
