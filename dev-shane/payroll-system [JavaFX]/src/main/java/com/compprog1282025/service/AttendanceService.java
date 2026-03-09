package com.compprog1282025.service;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.employee.AttendanceSummary;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.Session;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceService {
    private final AttendanceDAO attendanceDao;
    private LocalDate minAttendanceDate;
    private LocalDate maxAttendanceDate;

    public AttendanceService(AttendanceDAO attendanceDao) {
        this.attendanceDao = attendanceDao;
        refreshDateRange();
    }

    private void refreshDateRange() {
        List<Attendance> attendanceList = attendanceDao.getAll();
        if (attendanceList != null && !attendanceList.isEmpty()) {
            this.minAttendanceDate = attendanceList.stream()
                    .map(Attendance::getDate)
                    .filter(java.util.Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            this.maxAttendanceDate = attendanceList.stream()
                    .map(Attendance::getDate)
                    .filter(java.util.Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now());
        } else {
            this.minAttendanceDate = LocalDate.MIN;
            this.maxAttendanceDate = LocalDate.MAX;
        }
    }

    public boolean isWithinAttendanceRange(LocalDate date) {
        return !(date.isBefore(minAttendanceDate) || date.isAfter(maxAttendanceDate));
    }

    public boolean isWithinAttendanceRange(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return !(end.isBefore(minAttendanceDate) || start.isAfter(maxAttendanceDate));
    }

    public double calculateMonthlyHours(int employeeNumber, YearMonth month) {
        List<Attendance> attendanceList = attendanceDao.getAll();
        return attendanceList.stream()
                .filter(a -> a.getEmployeeNumber() == employeeNumber)
                .filter(a -> YearMonth.from(a.getDate()).equals(month))
                .mapToDouble(this::computeHours)
                .sum();
    }

    private double computeHours(Attendance a) {
        if (a.getTimeIn() == null || a.getTimeOut() == null) return 0.0;
        Duration duration = Duration.between(a.getTimeIn(), a.getTimeOut());
        return duration.isNegative() ? 0.0 : duration.toMinutes() / 60.0;
    }

    public void timeInAttendance(Session session, Employee employee) {
        if (session.getAttendance() != null && session.getAttendance().getDate().equals(LocalDate.now())) {
            throw new IllegalStateException("User is already timed in for today.");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        String attendanceId = employee.getEmployeeNumber() + "_" + today;

        Attendance newRecord = new Attendance(
                attendanceId,
                employee.getEmployeeNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                today,
                now
        );

        LocalTime shiftStart = LocalTime.of(8, 0);
        newRecord.setStatus(now.isAfter(shiftStart) ? "Late" : "On Time");

        attendanceDao.insert(newRecord);
        attendanceDao.saveData();

        session.setAttendance(newRecord);
        refreshDateRange();
    }

    public void timeOutAttendance(Session session, Employee employee) {
        Attendance attendance = session.getAttendance();
        if (attendance == null) {
            throw new IllegalStateException("No active attendance record found to Time Out.");
        }

        attendance.setTimeOut(LocalTime.now());
        attendanceDao.update(attendance);
        attendanceDao.saveData();
        session.setAttendance(null);
    }

    public AttendanceSummary getMonthlySummary(int empId, int month, int year) {
        List<Attendance> allRecords = getAllRecordsForEmployee(empId);
        double total = 0;

        for (Attendance a : allRecords) {
            if (a.getDate().getMonthValue() == month && a.getDate().getYear() == year) {
                total += a.getHoursWorked();
            }
        }
        return new AttendanceSummary(total);
    }

    public List<Attendance> getMonthlyRecords(int employeeNum, int month, int year) {
        return attendanceDao.getAll().stream()
                .filter(a -> a.getEmployeeNumber() == employeeNum)
                .filter(a -> a.getDate().getMonthValue() == month)
                .filter(a -> a.getDate().getYear() == year)
                .collect(Collectors.toList());
    }

    public List<Attendance> getAllRecordsForEmployee(int employeeNumber) {
        return attendanceDao.getAll().stream()
                .filter(a -> a.getEmployeeNumber() == employeeNumber)
                .collect(Collectors.toList());
    }

    public List<Attendance> getTeamAttendance(int month, int year, Session session) throws InvalidAccessException {
        EffectiveRole role = AccessControlService.resolveRole(session);
        if (role != EffectiveRole.HR && role != EffectiveRole.ADMIN) {
            AccessControlService.deny("View team attendance", "HR or ADMIN");
        }

        return attendanceDao.getAll().stream()
                .filter(a -> a.getDate().getMonthValue() == month)
                .filter(a -> a.getDate().getYear() == year)
                .collect(Collectors.toList());
    }

    public List<Attendance> getEmployeeAttendanceHistory(int employeeNumber, LocalDate fromDate, LocalDate toDate, Session session) throws InvalidAccessException {
        EffectiveRole role = AccessControlService.resolveRole(session);
        if (role == EffectiveRole.EMPLOYEE && !AccessControlService.isEmployeeSelf(session, employeeNumber)) {
            AccessControlService.deny("View employee attendance history", "self-service user");
        }

        return attendanceDao.getAll().stream()
                .filter(a -> a.getEmployeeNumber() == employeeNumber)
                .filter(a -> fromDate == null || !a.getDate().isBefore(fromDate))
                .filter(a -> toDate == null || !a.getDate().isAfter(toDate))
                .collect(Collectors.toList());
    }
}