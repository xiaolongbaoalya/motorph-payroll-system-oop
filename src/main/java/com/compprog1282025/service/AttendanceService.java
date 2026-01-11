package com.compprog1282025.service;

import com.compprog1282025.model.Attendance;
import com.compprog1282025.model.Employee;

import java.time.YearMonth;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AttendanceService {

    private final List<Attendance> attendanceRecords;
    private LocalDate minAttendanceDate;
    private LocalDate maxAttendanceDate;

    public AttendanceService(List<Attendance> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;

        Optional<LocalDate> min = attendanceRecords.stream()
            .map(Attendance::getDate)
            .min(LocalDate::compareTo);

        Optional<LocalDate> max = attendanceRecords.stream()
            .map(Attendance::getDate)
            .max(LocalDate::compareTo);

        this.minAttendanceDate = min.orElse(LocalDate.MIN);
        this.maxAttendanceDate = max.orElse(LocalDate.MAX);

    }

    // Checks if the given date is within the range of recorded attendance dates
    private boolean isWithinAttendanceRange(LocalDate date) {
        return !(date.isBefore(minAttendanceDate) || date.isAfter(maxAttendanceDate));
    }

    //YearMonth version of the above method
    private boolean isWithinAttendanceRange(YearMonth month) {
    LocalDate start = month.atDay(1);
    LocalDate end = month.atEndOfMonth();
    return !(end.isBefore(minAttendanceDate) || start.isAfter(maxAttendanceDate));
    }

    public double calculateMonthlyHours(Employee employee, YearMonth month) {
        if (!isWithinAttendanceRange(month)) {
            throw new IllegalArgumentException("No attendance records for the specified month: " + month);
        }
        return attendanceRecords.stream()
                .filter(a -> a.getEmployee().equals(employee))
                .filter(a -> YearMonth.from(a.getDate()).equals(month))
                .mapToDouble(a -> {
                    Duration duration = Duration.between(a.getTimeIn(), a.getTimeOut());
                    // Defensive: if timeOut is before timeIn (should not happen), return 0
                    return duration.isNegative() ? 0 : duration.toMinutes() / 60.0;
                })
                .sum();
    }

    public double calculateFixedWeekHours(Employee employee, LocalDate referenceDate) {
        if (!isWithinAttendanceRange(referenceDate)) {
            throw new IllegalArgumentException("No attendance records for the specified date: " + referenceDate);
        }
        LocalDate[] weekRange = calculateFixedWeekRange(referenceDate);
        LocalDate startDate = weekRange[0];
        LocalDate endDate = weekRange[1];

        return attendanceRecords.stream()
            .filter(a -> a.getEmployee().equals(employee))
            .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
            .mapToDouble(a -> {
                Duration duration = Duration.between(a.getTimeIn(), a.getTimeOut());
                return duration.isNegative() ? 0 : duration.toMinutes() / 60.0;
            })
        .sum();
    }


    public LocalDate[] calculateFixedWeekRange(LocalDate referenceDate) {
        // Anchor date: known start of first week in pattern (3-7 June 2024)
        LocalDate anchorStart = LocalDate.of(2024, 6, 3);

        // Calculate days between reference and anchor
        long daysSinceAnchor = Duration.between(anchorStart.atStartOfDay(), referenceDate.atStartOfDay()).toDays();

        // Determine which cycle (0-indexed) the reference date falls into
        long cycleIndex = daysSinceAnchor / 7;  // 5 work days + 2-day gap = 7-day cycle

        // Calculate start of the current cycle
        LocalDate weekStart = anchorStart.plusDays(cycleIndex * 7);
        LocalDate weekEnd = weekStart.plusDays(4);  // 5-day week

        return new LocalDate[]{weekStart, weekEnd};
    }
}
