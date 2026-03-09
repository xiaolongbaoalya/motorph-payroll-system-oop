package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.EmployeeService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HrTeamAttendancePanel extends JPanel implements RefreshablePanel {
    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    private final JComboBox<String> cmbMonth;
    private final JComboBox<Integer> cmbYear;
    private final JTable tblTeam;

    private final JComboBox<String> cmbEmployee;
    private final DatePickerControl dpFrom;
    private final DatePickerControl dpTo;
    private final JTable tblHistory;

    public HrTeamAttendancePanel(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;

        setLayout(new GridLayout(2, 1, 10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createTitledBorder("Team Attendance"));
        JPanel teamFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cmbMonth = new JComboBox<>();
        for (Month month : Month.values()) {
            cmbMonth.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cmbMonth.setPreferredSize(new Dimension(120, 30));

        cmbYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = 2022; year <= currentYear; year++) {
            cmbYear.addItem(year);
        }
        cmbYear.setSelectedItem(currentYear);
        cmbYear.setPreferredSize(new Dimension(90, 30));

        JButton btnLoadTeam = new JButton("Load Team Attendance");
        btnLoadTeam.addActionListener(e -> loadTeam());

        teamFilter.add(new JLabel("Month:"));
        teamFilter.add(cmbMonth);
        teamFilter.add(new JLabel("Year:"));
        teamFilter.add(cmbYear);
        teamFilter.add(btnLoadTeam);

        tblTeam = new JTable();
        tblTeam.setRowHeight(28);
        tblTeam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTeam.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        top.add(teamFilter, BorderLayout.NORTH);
        top.add(new JScrollPane(tblTeam), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createTitledBorder("Specific Employee Attendance History"));
        JPanel historyFilter = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        cmbEmployee = new JComboBox<>();
        cmbEmployee.setEditable(true);
        cmbEmployee.setPrototypeDisplayValue("10001 - Manuel III Garcia");

        dpFrom = new DatePickerControl(LocalDate.now().minusMonths(1));
        dpTo = new DatePickerControl(LocalDate.now());

        JButton btnLoadHistory = new JButton("Load Employee History");
        btnLoadHistory.addActionListener(e -> loadHistory());

        gbc.gridx = 0; gbc.gridy = 0; historyFilter.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; historyFilter.add(cmbEmployee, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; historyFilter.add(new JLabel("From:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; historyFilter.add(dpFrom, gbc);

        gbc.gridx = 4; gbc.gridy = 0; historyFilter.add(new JLabel("To:"), gbc);
        gbc.gridx = 5; gbc.gridy = 0; historyFilter.add(dpTo, gbc);

        gbc.gridx = 6; gbc.gridy = 0; historyFilter.add(btnLoadHistory, gbc);

        tblHistory = new JTable();
        tblHistory.setRowHeight(28);
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblHistory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        bottom.add(historyFilter, BorderLayout.NORTH);
        bottom.add(new JScrollPane(tblHistory), BorderLayout.CENTER);

        add(top);
        add(bottom);
    }

    @Override
    public void refreshData() {
        List<String> items = employeeService.getEmployeeDirectory().stream()
                .sorted(Comparator.comparingInt(e -> e.getEmployeeNumber()))
                .map(e -> e.getEmployeeNumber() + " - " + e.getFirstName() + " " + e.getLastName())
                .toList();
        cmbEmployee.removeAllItems();
        for (String item : items) {
            cmbEmployee.addItem(item);
        }
        loadTeam();
    }

    private void loadTeam() {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select valid month/year.");
            return;
        }

        List<Attendance> rows = attendanceService.getTeamAttendance(month, year);
        Object[][] data = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            Attendance a = rows.get(i);
            data[i][0] = a.getEmployeeNumber();
            data[i][1] = a.getDate();
            data[i][2] = a.getTimeInFormatted();
            data[i][3] = a.getTimeOutFormatted();

        }
        SwingModuleUtil.setTableData(tblTeam, new String[]{"Employee #", "Date", "Time In", "Time Out"}, data);
        tblTeam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblTeam.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void loadHistory() {
        Object selected = cmbEmployee.getEditor().getItem();
        String raw = selected == null ? "" : selected.toString().trim();
        if (raw.isEmpty()) {
            SwingModuleUtil.warn(this, "Validation", "Please select a valid employee.");
            return;
        }

        Integer employeeNo = parseEmployeeNo(raw);
        if (employeeNo == null || employeeService.getEmployee(employeeNo) == null) {
            SwingModuleUtil.warn(this, "Validation", "Please select a valid employee.");
            return;
        }

        LocalDate from = dpFrom.getDate();
        LocalDate to = dpTo.getDate();
        if (to.isBefore(from)) {
            SwingModuleUtil.warn(this, "Validation", "To date cannot be earlier than From date.");
            return;
        }

        List<Attendance> rows = attendanceService.getEmployeeAttendanceHistory(employeeNo, from, to);

        Object[][] data = new Object[rows.size()][3];
        for (int i = 0; i < rows.size(); i++) {
            Attendance a = rows.get(i);
            data[i][0] = a.getDate();
            data[i][1] = a.getTimeInFormatted();
            data[i][2] = a.getTimeOutFormatted();

        }
        SwingModuleUtil.setTableData(tblHistory, new String[]{"Date", "Time In", "Time Out"}, data);
        tblHistory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblHistory.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private Integer parseEmployeeNo(String value) {
        int sep = value.indexOf(" - ");
        String numberPart = sep > 0 ? value.substring(0, sep).trim() : value;
        return SwingModuleUtil.parseInteger(numberPart);
    }
}




