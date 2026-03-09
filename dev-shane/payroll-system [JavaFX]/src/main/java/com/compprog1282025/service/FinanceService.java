package com.compprog1282025.service;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.model.interfaces.Calculable;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.dto.PayslipComputation;

import java.time.YearMonth;

public class FinanceService implements Calculable {
    private final AttendanceService attendanceService;

    public FinanceService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    private static final double LOWER_RATE = 0.01;
    private static final double HIGHER_RATE = 0.02;
    private static final double MAX_CONTRIBUTION = 100.0;

    private static final double PHILHEALTH_PREMIUM_RATE = 0.03;
    private static final double PHILHEALTH_MIN_PREMIUM = 300.0;
    private static final double PHILHEALTH_MAX_PREMIUM = 1800.0;

    public double calculatePagibig(double salary) {
        double contribution = 0;
        if (salary < 1000) {
            return contribution;
        }

        double pagIbigRate = (salary <= 1500) ? LOWER_RATE : HIGHER_RATE;
        contribution = salary * pagIbigRate;

        return Math.min(contribution, MAX_CONTRIBUTION);
    }

    public double calculatePhilhealth(double salary) {
        double premium;
        if (salary <= 10000) {
            premium = PHILHEALTH_MIN_PREMIUM;
        } else if (salary < 60000) {
            premium = salary * PHILHEALTH_PREMIUM_RATE;
        } else {
            premium = PHILHEALTH_MAX_PREMIUM;
        }
        return premium / 2;
    }

    public double calculateSSS(double finalSalary) {
        final double[][] sssBrackets = {
                {0, 3250, 135.00}, {3250, 3750, 157.50}, {3750, 4250, 180.00},
                {4250, 4750, 202.50}, {4750, 5250, 225.00}, {5250, 5750, 247.50},
                {5750, 6250, 270.00}, {6250, 6750, 292.50}, {6750, 7250, 315.00},
                {7250, 7750, 337.50}, {7750, 8250, 360.00}, {8250, 8750, 382.50},
                {8750, 9250, 405.00}, {9250, 9750, 427.50}, {9750, 10250, 450.00},
                {10250, 10750, 472.50}, {10750, 11250, 495.00}, {11250, 11750, 517.50},
                {11750, 12250, 540.00}, {12250, 12750, 562.50}, {12750, 13250, 585.00},
                {13250, 13750, 607.50}, {13750, 14250, 630.00}, {14250, 14750, 652.50},
                {14750, 15250, 675.00}, {15250, 15750, 697.50}, {15750, 16250, 720.00},
                {16250, 16750, 742.50}, {16750, 17250, 765.00}, {17250, 17750, 787.50},
                {17750, 18250, 810.00}, {18250, 18750, 832.50}, {18750, 19250, 855.00},
                {19250, 19750, 877.50}, {19750, 20250, 900.00}, {20250, 20750, 922.50},
                {20750, 21250, 945.00}, {21250, 21750, 967.50}, {21750, 22250, 990.00},
                {22250, 22750, 1012.50}, {22750, 23250, 1035.00}, {23250, 23750, 1057.50},
                {23750, 24250, 1080.00}, {24250, 24750, 1102.50}, {24750, Double.MAX_VALUE, 1125.00}
        };

        for (double[] bracket : sssBrackets) {
            if (finalSalary >= bracket[0] && finalSalary < bracket[1]) {
                return bracket[2] / 2;
            }
        }
        return 0;
    }

    public double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) {
            return 0.0;
        } else if (taxableIncome <= 33332) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66666) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166666) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666666) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    @Override
    public double calculateNet(double salary) {
        double sss = calculateSSS(salary);
        double philHealth = calculatePhilhealth(salary);
        double pagIbig = calculatePagibig(salary);
        double taxableIncome = salary - (sss + philHealth + pagIbig);
        return taxableIncome - calculateWithholdingTax(taxableIncome);
    }

    public double calculateMonthSalary(Session session, Employee employee, int year, int month) throws InvalidAccessException {
        validatePayrollAccess(session, employee, "Calculate monthly salary");
        return calculateMonthSalaryInternal(employee, year, month);
    }

    public PayslipComputation computePayslip(Session session, Employee employee, int year, int month) throws InvalidAccessException {
        validatePayrollAccess(session, employee, "Generate payslip");

        if (employee == null) {
            return null;
        }

        double basicSalary = calculateMonthSalaryInternal(employee, year, month);
        if (basicSalary <= 0) {
            return null;
        }

        double rice = employee.getSalary().getRiceSubsidy();
        double phone = employee.getSalary().getPhoneAllowance();
        double clothing = employee.getSalary().getClothingAllowance();

        double sss = calculateSSS(basicSalary);
        double philHealth = calculatePhilhealth(basicSalary);
        double pagIbig = calculatePagibig(basicSalary);
        double taxableIncome = basicSalary - (sss + philHealth + pagIbig);
        double withholding = calculateWithholdingTax(taxableIncome);

        double totalEarnings = basicSalary + rice + phone + clothing;
        double totalContributions = sss + philHealth + pagIbig;
        double totalDeductions = totalContributions + withholding;
        double netPay = totalEarnings - totalDeductions;

        Payslip payslip = new Payslip(
                employee.getEmployeeNumber(),
                YearMonth.of(year, month),
                totalEarnings,
                sss,
                philHealth,
                pagIbig,
                withholding,
                netPay
        );

        return new PayslipComputation(
                payslip,
                basicSalary,
                rice,
                phone,
                clothing,
                totalEarnings,
                totalContributions,
                totalDeductions
        );
    }

    private double calculateMonthSalaryInternal(Employee employee, int year, int month) {
        YearMonth salaryPeriod = YearMonth.of(year, month);
        if (attendanceService.isWithinAttendanceRange(salaryPeriod)) {
            double monthlyHours = attendanceService.calculateMonthlyHours(employee.getEmployeeNumber(), salaryPeriod);
            double hourlyRate = employee.getSalary().calculateHourlyRate();
            return monthlyHours * hourlyRate;
        }
        return 0;
    }

    private void validatePayrollAccess(Session session, Employee targetEmployee, String action) throws InvalidAccessException {
        if (session == null || session.getUser() == null) {
            AccessControlService.deny(action, "authenticated user");
        }

        if (targetEmployee == null) {
            return;
        }

        if (AccessControlService.isEmployeeSelf(session, targetEmployee.getEmployeeNumber())) {
            return;
        }

        AccessControlService.requirePayrollProcessing(session, action);
    }
}