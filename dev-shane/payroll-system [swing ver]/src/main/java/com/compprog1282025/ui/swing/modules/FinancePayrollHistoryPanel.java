package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.dto.PayslipComputation;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FinancePayrollHistoryPanel extends JPanel implements RefreshablePanel {
    private final EmployeeService employeeService;
    private final FinanceService financeService;

    private final JComboBox<String> cmbMonth = new JComboBox<>();
    private final JComboBox<Integer> cmbYear = new JComboBox<>();
    private final JTable table = new JTable();

    private List<Payslip> cached = new ArrayList<>();

    public FinancePayrollHistoryPanel(EmployeeService employeeService, FinanceService financeService) {
        this.employeeService = employeeService;
        this.financeService = financeService;

        for (Month month : Month.values()) {
            cmbMonth.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        for (int year = 2022; year <= currentYear; year++) {
            cmbYear.addItem(year);
        }
        cmbYear.setSelectedItem(currentYear);

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnLoad = new JButton("Load Payroll History");
        JButton btnDetails = new JButton("View Details");
        btnLoad.addActionListener(e -> refreshData());
        btnDetails.addActionListener(e -> viewDetails());

        top.add(new JLabel("Month")); top.add(cmbMonth);
        top.add(new JLabel("Year")); top.add(cmbYear);
        top.add(btnLoad); top.add(btnDetails);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select valid month/year.");
            return;
        }

        List<Employee> employees = employeeService.getEmployeeDirectory().stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .toList();

        List<Payslip> rows = new ArrayList<>();
        for (Employee e : employees) {
            PayslipComputation c = financeService.computePayslip(e, year, month);
            if (c != null) {
                rows.add(c.getPayslip());
            }
        }
        cached = rows;

        Object[][] data = new Object[rows.size()][7];
        for (int i = 0; i < rows.size(); i++) {
            Payslip p = rows.get(i);
            Employee e = employeeService.getEmployee(p.getEmployeeNumber());
            data[i][0] = p.getEmployeeNumber();
            data[i][1] = e == null ? "Unknown" : e.getFirstName() + " " + e.getLastName();
            data[i][2] = p.getYearMonth();
            data[i][3] = SwingModuleUtil.money(p.getGrossSalary());
            double ded = p.getSss() + p.getPhilhealth() + p.getPagibig() + p.getWithholding();
            data[i][4] = SwingModuleUtil.money(ded);
            data[i][5] = SwingModuleUtil.money(p.getNetSalary());
            data[i][6] = "Computed";
        }

        SwingModuleUtil.setTableData(table, new String[]{"Employee #", "Employee Name", "Period", "Gross", "Deductions", "Net", "Status"}, data);
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= cached.size()) {
            SwingModuleUtil.warn(this, "Selection", "Select a payroll record first.");
            return;
        }

        Payslip p = cached.get(row);
        Employee e = employeeService.getEmployee(p.getEmployeeNumber());
        String msg = "Employee #: " + p.getEmployeeNumber() + "\n"
                + "Employee Name: " + (e == null ? "Unknown" : e.getFirstName() + " " + e.getLastName()) + "\n"
                + "Payroll Period: " + p.getYearMonth() + "\n"
                + "Gross Pay: " + SwingModuleUtil.money(p.getGrossSalary()) + "\n"
                + "SSS: " + SwingModuleUtil.money(p.getSss()) + "\n"
                + "PhilHealth: " + SwingModuleUtil.money(p.getPhilhealth()) + "\n"
                + "Pag-IBIG: " + SwingModuleUtil.money(p.getPagibig()) + "\n"
                + "Withholding Tax: " + SwingModuleUtil.money(p.getWithholding()) + "\n"
                + "Net Pay: " + SwingModuleUtil.money(p.getNetSalary());
        SwingModuleUtil.info(this, "Payroll Details", msg);
    }
}