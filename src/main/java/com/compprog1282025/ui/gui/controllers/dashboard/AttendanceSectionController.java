package com.compprog1282025.ui.gui.controllers.dashboard;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.employee.AttendanceSummary;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AttendanceSectionController {
    private final AttendanceService attendanceService;
    private final TableView<Attendance> tblAttendance;
    private final ComboBox<String> cmbMonth;
    private final ComboBox<Integer> cmbYear;
    private final Label lblTotalHours;

    public AttendanceSectionController(
            AttendanceService attendanceService,
            TableView<Attendance> tblAttendance,
            ComboBox<String> cmbMonth,
            ComboBox<Integer> cmbYear,
            Label lblTotalHours
    ) {
        this.attendanceService = attendanceService;
        this.tblAttendance = tblAttendance;
        this.cmbMonth = cmbMonth;
        this.cmbYear = cmbYear;
        this.lblTotalHours = lblTotalHours;
    }

    public void setupAttendanceTable() {
        tblAttendance.getColumns().clear();
        tblAttendance.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Attendance, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Attendance, String> timeInCol = new TableColumn<>("Time In");
        timeInCol.setCellValueFactory(new PropertyValueFactory<>("timeInFormatted"));

        TableColumn<Attendance, String> timeOutCol = new TableColumn<>("Time Out");
        timeOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOutFormatted"));

        tblAttendance.getColumns().setAll(dateCol, timeInCol, timeOutCol);
    }

    public void refreshAttendanceData(Session session) {
        if (session == null || session.getUser() == null) {
            return;
        }

        int empId = session.getUser().getEmployee().getEmployeeNumber();
        int month = cmbMonth.getSelectionModel().getSelectedIndex() + 1;
        Integer year = cmbYear.getValue();
        if (year == null) {
            return;
        }

        List<Attendance> records = attendanceService.getAllRecordsForEmployee(empId).stream()
                .filter(a -> a.getDate().getMonthValue() == month)
                .filter(a -> a.getDate().getYear() == year)
                .collect(Collectors.toList());

        tblAttendance.setItems(FXCollections.observableArrayList(records));
        updateKPIs(session);
    }

    public void timeIn(Session session) {
        attendanceService.timeInAttendance(session, session.getUser().getEmployee());
        refreshAttendanceData(session);
    }

    public void timeOut(Session session) {
        attendanceService.timeOutAttendance(session, session.getUser().getEmployee());
        tblAttendance.refresh();
        refreshAttendanceData(session);
    }

    private void updateKPIs(Session session) {
        int empId = session.getUser().getEmployee().getEmployeeNumber();
        int month = cmbMonth.getSelectionModel().getSelectedIndex() + 1;
        int year = cmbYear.getValue();
        AttendanceSummary summary = attendanceService.getMonthlySummary(empId, month, year);
        if (lblTotalHours != null) {
            lblTotalHours.setText(String.format("%.2f", summary.getTotalHours()));
        }
    }
}
