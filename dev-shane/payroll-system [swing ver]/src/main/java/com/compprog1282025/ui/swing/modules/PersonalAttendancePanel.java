package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PersonalAttendancePanel extends JPanel implements RefreshablePanel {
    private final AttendanceService attendanceService;
    private final Session session;

    private final JTable table;
    private final JLabel lblTotalHours;
    private final JComboBox<String> cmbMonth;
    private final JComboBox<Integer> cmbYear;

    public PersonalAttendancePanel(AttendanceService attendanceService, Session session) {
        this.attendanceService = attendanceService;
        this.session = session;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(242, 244, 247));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);

        JLabel viewLabel = new JLabel("View Records for:");
        viewLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

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
        cmbYear.setPreferredSize(new Dimension(80, 30));

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(e -> refreshData());

        top.add(viewLabel);
        top.add(cmbMonth);
        top.add(cmbYear);
        top.add(btnLoad);

        JPanel summaryCard = new JPanel(new BorderLayout());
        summaryCard.setBackground(new Color(248, 249, 251));
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(14, 0, 14, 18)
        ));

        JPanel leftAccent = new JPanel();
        leftAccent.setBackground(new Color(65, 131, 255));
        leftAccent.setPreferredSize(new Dimension(4, 0));
        summaryCard.add(leftAccent, BorderLayout.WEST);

        JPanel summaryTextWrap = new JPanel(new BorderLayout());
        summaryTextWrap.setOpaque(false);
        summaryTextWrap.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JPanel summaryText = new JPanel(new BorderLayout());
        summaryText.setOpaque(false);

        JLabel label = new JLabel("Total Hours Worked");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(90, 104, 123));

        lblTotalHours = new JLabel("0.00", SwingConstants.LEFT);
        lblTotalHours.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotalHours.setForeground(new Color(35, 46, 62));

        summaryText.add(label, BorderLayout.NORTH);
        summaryText.add(lblTotalHours, BorderLayout.CENTER);
        summaryTextWrap.add(summaryText, BorderLayout.CENTER);
        summaryCard.add(summaryTextWrap, BorderLayout.CENTER);

        table = new JTable();
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(summaryCard, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        int employeeNo = session.getUser().getEmployeeNumber();

        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select a valid year.");
            return;
        }

        List<Attendance> rows = attendanceService.getMonthlyRecords(employeeNo, month, year);
        Object[][] data = new Object[rows.size()][3];
        double total = 0;
        for (int i = 0; i < rows.size(); i++) {
            Attendance a = rows.get(i);
            data[i][0] = a.getDate();
            data[i][1] = a.getTimeInFormatted();
            data[i][2] = a.getTimeOutFormatted();
            total += a.getHoursWorked();
        }

        SwingModuleUtil.setTableData(table, new String[]{"Date", "Time In", "Time Out"}, data);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalHours.setText(String.format("%.2f", total));
    }
}