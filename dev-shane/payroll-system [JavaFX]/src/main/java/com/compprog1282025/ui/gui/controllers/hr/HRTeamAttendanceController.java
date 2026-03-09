package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.InvalidAccessException;

import java.time.LocalDate;
import java.util.List;

public class HRTeamAttendanceController {
    private final AttendanceService attendanceService;

    public HRTeamAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public List<Attendance> loadTeamAttendance(int month, int year, Session session) throws InvalidAccessException {
        return attendanceService.getTeamAttendance(month, year, session);
    }

    public List<Attendance> loadEmployeeAttendanceHistory(int employeeNumber, LocalDate fromDate, LocalDate toDate, Session session) throws InvalidAccessException {
        return attendanceService.getEmployeeAttendanceHistory(employeeNumber, fromDate, toDate, session);
    }

    public void loadDefault(Session session) throws InvalidAccessException {
        LocalDate now = LocalDate.now();
        attendanceService.getTeamAttendance(now.getMonthValue(), now.getYear(), session);
    }
}