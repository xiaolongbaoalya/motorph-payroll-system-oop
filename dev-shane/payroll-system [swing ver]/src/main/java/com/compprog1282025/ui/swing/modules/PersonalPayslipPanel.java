package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.PayslipPdfService;
import com.compprog1282025.service.dto.PayslipComputation;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class PersonalPayslipPanel extends JPanel implements RefreshablePanel {
    private final FinanceService financeService;
    private final Session session;
    private final PayslipPdfService payslipPdfService;

    private final JComboBox<String> cmbMonth;
    private final JComboBox<Integer> cmbYear;

    private final JLabel lblNetPay;
    private final JLabel lblGrossIncome;

    private final JLabel lblBasic;
    private final JLabel lblRice;
    private final JLabel lblPhone;
    private final JLabel lblClothing;

    private final JLabel lblSss;
    private final JLabel lblPhil;
    private final JLabel lblPagIbig;
    private final JLabel lblTax;

    private final JLabel lblTotalEarnings;
    private final JLabel lblTotalDeductions;
    private final JLabel lblSummaryTax;
    private final JLabel lblFinalNetPay;

    public PersonalPayslipPanel(FinanceService financeService, Session session, PayslipPdfService payslipPdfService) {
        this.financeService = financeService;
        this.session = session;
        this.payslipPdfService = payslipPdfService;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(242, 244, 247));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        JLabel selectLabel = new JLabel("Select Pay Period:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

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
        btnLoad.addActionListener(e -> loadPayslip(true));

        JButton btnPdf = new JButton("Download PDF");
        btnPdf.addActionListener(e -> generatePdf());

        controls.add(selectLabel);
        controls.add(cmbMonth);
        controls.add(cmbYear);
        controls.add(btnLoad);
        controls.add(btnPdf);

        JPanel summaryCards = new JPanel(new GridLayout(1, 2, 12, 0));
        summaryCards.setOpaque(false);
        lblNetPay = new JLabel(SwingModuleUtil.money(0));
        lblGrossIncome = new JLabel(SwingModuleUtil.money(0));
        summaryCards.add(summaryCard("Net Pay", lblNetPay));
        summaryCards.add(summaryCard("Gross Income", lblGrossIncome));

        JPanel breakdown = new JPanel(new GridLayout(1, 2, 12, 0));
        breakdown.setOpaque(false);

        lblBasic = new JLabel();
        lblRice = new JLabel();
        lblPhone = new JLabel();
        lblClothing = new JLabel();
        breakdown.add(detailCard("Earnings", new String[]{
                "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance"
        }, new JLabel[]{lblBasic, lblRice, lblPhone, lblClothing}));

        lblSss = new JLabel();
        lblPhil = new JLabel();
        lblPagIbig = new JLabel();
        lblTax = new JLabel();
        breakdown.add(detailCard("Deductions", new String[]{
                "SSS", "PhilHealth", "Pag-IBIG", "Withholding Tax"
        }, new JLabel[]{lblSss, lblPhil, lblPagIbig, lblTax}));

        lblTotalEarnings = new JLabel();
        lblTotalDeductions = new JLabel();
        lblSummaryTax = new JLabel();
        lblFinalNetPay = new JLabel();

        JPanel summary = detailCard("Payroll Summary", new String[]{
                "Total Earnings", "Total Deductions", "Withholding Tax", "FINAL NET PAY"
        }, new JLabel[]{lblTotalEarnings, lblTotalDeductions, lblSummaryTax, lblFinalNetPay});

        content.add(controls);
        content.add(spacer(12));
        content.add(summaryCards);
        content.add(spacer(12));
        content.add(breakdown);
        content.add(spacer(12));
        content.add(summary);

        add(content, BorderLayout.NORTH);
        applyEmpty();
    }

    private JPanel summaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(248, 249, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(82, 95, 115));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(35, 46, 62));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel detailCard(String title, String[] labels, JLabel[] values) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(248, 249, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(82, 95, 115));

        JPanel rows = new JPanel(new GridLayout(labels.length, 2, 6, 8));
        rows.setOpaque(false);

        for (int i = 0; i < labels.length; i++) {
            JLabel key = new JLabel(labels[i]);
            key.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            values[i].setFont(new Font("Segoe UI", Font.BOLD, "FINAL NET PAY".equals(labels[i]) ? 18 : 13));
            rows.add(key);
            rows.add(values[i]);
        }

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel spacer(int height) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, height));
        return spacer;
    }

    @Override
    public void refreshData() {
        loadPayslip(false);
    }

    private void loadPayslip(boolean notifyIfMissing) {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select a valid year.");
            return;
        }

        Employee employee = session.getUser().getEmployee();
        PayslipComputation c = financeService.computePayslip(employee, year, month);
        if (c == null) {
            applyEmpty();
            if (notifyIfMissing) {
                SwingModuleUtil.info(this, "Payslip", "No payroll data for selected period.");
            }
            return;
        }

        Payslip p = c.getPayslip();
        lblNetPay.setText(SwingModuleUtil.money(p.getNetSalary()));
        lblGrossIncome.setText(SwingModuleUtil.money(c.getTotalEarnings()));

        lblBasic.setText(SwingModuleUtil.money(c.getBasicSalary()));
        lblRice.setText(SwingModuleUtil.money(c.getRiceSubsidy()));
        lblPhone.setText(SwingModuleUtil.money(c.getPhoneAllowance()));
        lblClothing.setText(SwingModuleUtil.money(c.getClothingAllowance()));

        lblSss.setText(SwingModuleUtil.money(p.getSss()));
        lblPhil.setText(SwingModuleUtil.money(p.getPhilhealth()));
        lblPagIbig.setText(SwingModuleUtil.money(p.getPagibig()));
        lblTax.setText(SwingModuleUtil.money(p.getWithholding()));

        lblTotalEarnings.setText(SwingModuleUtil.money(c.getTotalEarnings()));
        lblTotalDeductions.setText(SwingModuleUtil.money(c.getTotalDeductions()));
        lblSummaryTax.setText(SwingModuleUtil.money(p.getWithholding()));
        lblFinalNetPay.setText(SwingModuleUtil.money(p.getNetSalary()));
    }

    private void applyEmpty() {
        JLabel[] labels = {
                lblNetPay, lblGrossIncome, lblBasic, lblRice, lblPhone, lblClothing,
                lblSss, lblPhil, lblPagIbig, lblTax, lblTotalEarnings, lblTotalDeductions,
                lblSummaryTax, lblFinalNetPay
        };
        for (JLabel label : labels) {
            label.setText(SwingModuleUtil.money(0));
        }
    }

    private void generatePdf() {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select a valid year.");
            return;
        }

        try {
            Employee employee = session.getUser().getEmployee();
            PayslipComputation c = financeService.computePayslip(employee, year, month);
            if (c == null) {
                SwingModuleUtil.warn(this, "Payslip", "No payroll data for selected period.");
                return;
            }
            Path output = payslipPdfService.generatePayslipPdf(employee, c);
            showPdfPreview(output, employee, c);
        } catch (Exception ex) {
            SwingModuleUtil.error(this, "Error", ex.getMessage());
        }
    }

    private void showPdfPreview(Path output, Employee employee, PayslipComputation c) {
        Payslip p = c.getPayslip();
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setText("MotorPH Payslip Preview\n\n"
                + "Employee #: " + employee.getEmployeeNumber() + "\n"
                + "Employee Name: " + employee.getFirstName() + " " + employee.getLastName() + "\n"
                + "Period: " + p.getYearMonth() + "\n\n"
                + "Earnings\n"
                + "Basic Salary: " + SwingModuleUtil.money(c.getBasicSalary()) + "\n"
                + "Rice Subsidy: " + SwingModuleUtil.money(c.getRiceSubsidy()) + "\n"
                + "Phone Allowance: " + SwingModuleUtil.money(c.getPhoneAllowance()) + "\n"
                + "Clothing Allowance: " + SwingModuleUtil.money(c.getClothingAllowance()) + "\n"
                + "Total Earnings: " + SwingModuleUtil.money(c.getTotalEarnings()) + "\n\n"
                + "Deductions\n"
                + "SSS: " + SwingModuleUtil.money(p.getSss()) + "\n"
                + "PhilHealth: " + SwingModuleUtil.money(p.getPhilhealth()) + "\n"
                + "Pag-IBIG: " + SwingModuleUtil.money(p.getPagibig()) + "\n"
                + "Withholding Tax: " + SwingModuleUtil.money(p.getWithholding()) + "\n"
                + "Total Deductions: " + SwingModuleUtil.money(c.getTotalDeductions()) + "\n\n"
                + "FINAL NET PAY: " + SwingModuleUtil.money(p.getNetSalary()) + "\n\n"
                + "PDF File: " + output);

        JDialog dialog = new JDialog();
        dialog.setTitle("Payslip Preview");
        dialog.setModal(true);
        dialog.setSize(520, 560);
        dialog.setLocationRelativeTo(this);

        JButton openButton = new JButton("Open Generated PDF");
        openButton.addActionListener(e -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(output.toFile());
                }
            } catch (Exception ex) {
                SwingModuleUtil.error(this, "Error", ex.getMessage());
            }
        });

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(openButton);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }
}




