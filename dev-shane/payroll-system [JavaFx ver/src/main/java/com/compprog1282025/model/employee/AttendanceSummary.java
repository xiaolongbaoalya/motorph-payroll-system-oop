package com.compprog1282025.model.employee;

/**
 * Simplified POJO for Dashboard KPIs.
 */
public class AttendanceSummary {
    private final double totalHours;

    public AttendanceSummary(double totalHours) {
        this.totalHours = totalHours;
    }

    public double getTotalHours() {
        return totalHours;
    }
}