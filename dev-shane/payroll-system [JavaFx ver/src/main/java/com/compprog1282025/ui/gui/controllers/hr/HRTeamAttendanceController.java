package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

public class HRTeamAttendanceController {
    private final AttendanceService attendanceService;

    public HRTeamAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public List<Attendance> loadTeamAttendance(int month, int year) {
        return attendanceService.getTeamAttendance(month, year);
    }

    public List<Attendance> loadEmployeeAttendanceHistory(int employeeNumber, LocalDate fromDate, LocalDate toDate) {
        return attendanceService.getEmployeeAttendanceHistory(employeeNumber, fromDate, toDate);
    }

    public void loadDefault(Session session) {
        LocalDate now = LocalDate.now();
        attendanceService.getTeamAttendance(now.getMonthValue(), now.getYear());
    }
}
