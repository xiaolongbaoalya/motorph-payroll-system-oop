package com.compprog1282025.util;

/**
 * Utility class for calculating government-mandated deductions.
 */
public class DeductionsUtil {

    private static final double PHILHEALTH_PREMIUM_RATE = 0.03; // 3%
    private static final double PHILHEALTH_MIN_PREMIUM = 300.0;
    private static final double PHILHEALTH_MAX_PREMIUM = 1800.0;

    /**
     * Calculates the employee's PhilHealth contribution share based on gross monthly salary.
     * 
     * @param grossSalary The employee's gross monthly salary (before deductions).
     * @return The PhilHealth deduction amount for the employee.
     * @throws IllegalArgumentException if grossSalary is negative.
     */
    public static double calculatePhilhealth(double grossSalary) {
        if (grossSalary < 0) {
            throw new IllegalArgumentException("Gross salary cannot be negative");
        }

        double premium;
        if (grossSalary <= 10000) {
            premium = PHILHEALTH_MIN_PREMIUM;
        } else if (grossSalary <= 59999.99) {
            premium = Math.min(Math.max(grossSalary * PHILHEALTH_PREMIUM_RATE, PHILHEALTH_MIN_PREMIUM), PHILHEALTH_MAX_PREMIUM);
        } else {
            premium = PHILHEALTH_MAX_PREMIUM;
        }

        return premium / 2; // Employee share (50%)
    }

    private static final double LOWER_RATE = 0.01;
    private static final double HIGHER_RATE = 0.02;
    private static final double MAX_CONTRIBUTION = 100.0;

    /**
     * Calculate the Pag-IBIG contribution for an employee based on their monthly salary.
     * The contribution rate is 1% for salaries between 1000 and 1500 (inclusive),
     * and 2% for salaries above 1500.
     * The maximum contribution is capped at 100.
     *
     * @param finalSalary the employee's gross monthly salary
     * @return the Pag-IBIG employee contribution amount
     */
    public static double calculatePagIbig(double finalSalary) {
        if (finalSalary < 1000) {
            // If salary is below 1000, you may define behavior here, e.g., minimum contribution or zero.
            return 0;
        }

        double pagIbigRate = (finalSalary <= 1500) ? LOWER_RATE : HIGHER_RATE;
        double contribution = finalSalary * pagIbigRate;

        return Math.min(contribution, MAX_CONTRIBUTION);
    }

    public static double calculateSSS(double finalSalary) {
    final double[][] sssBrackets = {
        {0, 3249.99, 135.00}, {3250, 3749.99, 157.50}, {3750, 4249.99, 180.00},
        {4250, 4749.99, 202.50}, {4750, 5249.99, 225.00}, {5250, 5749.99, 247.50},
        {5750, 6249.99, 270.00}, {6250, 6749.99, 292.50}, {6750, 7249.99, 315.00},
        {7250, 7749.99, 337.50}, {7750, 8249.99, 360.00}, {8250, 8749.99, 382.50},
        {8750, 9249.99, 405.00}, {9250, 9749.99, 427.50}, {9750, 10249.99, 450.00},
        {10250, 10749.99, 472.50}, {10750, 11249.99, 495.00}, {11250, 11749.99, 517.50},
        {11750, 12249.99, 540.00}, {12250, 12749.99, 562.50}, {12750, 13249.99, 585.00},
        {13250, 13749.99, 607.50}, {13750, 14249.99, 630.00}, {14250, 14749.99, 652.50},
        {14750, 15249.99, 675.00}, {15250, 15749.99, 697.50}, {15750, 16249.99, 720.00},
        {16250, 16749.99, 742.50}, {16750, 17249.99, 765.00}, {17250, 17749.99, 787.50},
        {17750, 18249.99, 810.00}, {18250, 18749.99, 832.50}, {18750, 19249.99, 855.00},
        {19250, 19749.99, 877.50}, {19750, 20249.99, 900.00}, {20250, 20749.99, 922.50},
        {20750, 21249.99, 945.00}, {21250, 21749.99, 967.50}, {21750, 22249.99, 990.00},
        {22250, 22749.99, 1012.50}, {22750, 23249.99, 1035.00}, {23250, 23749.99, 1057.50},
        {23750, 24249.99, 1080.00}, {24250, 24749.99, 1102.50}, {24750, Double.MAX_VALUE, 1125.00}
    };

    for (double[] bracket : sssBrackets) {
        if (finalSalary >= bracket[0] && finalSalary <= bracket[1]) {
            return bracket[2] / 2; // Return employee share only
        }
    }
    return 0;  
    }

    public static double calculateWithholdingTax(double taxableIncome) {
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

}
