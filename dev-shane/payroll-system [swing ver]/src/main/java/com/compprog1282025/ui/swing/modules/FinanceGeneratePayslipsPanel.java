package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.PayslipPdfService;
import com.compprog1282025.service.dto.PayslipComputation;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FinanceGeneratePayslipsPanel extends JPanel implements RefreshablePanel {
    private final EmployeeService employeeService;
    private final FinanceService financeService;
    private final PayslipPdfService payslipPdfService;

    private final JComboBox<String> cmbMonth = new JComboBox<>();
    private final JComboBox<Integer> cmbYear = new JComboBox<>();
    private final JCheckBox chkAll = new JCheckBox("All Employees", true);
    private final JCheckBox chkSelected = new JCheckBox("Selected Employees");
    private final JTable table = new JTable();
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel lblStatus = new JLabel("Ready");
    private final JButton btnClear = new JButton("Clear Selection");

    public FinanceGeneratePayslipsPanel(EmployeeService employeeService, FinanceService financeService, PayslipPdfService payslipPdfService) {
        this.employeeService = employeeService;
        this.financeService = financeService;
        this.payslipPdfService = payslipPdfService;

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
        JButton btnGenerate = new JButton("Generate Payslips");
        btnGenerate.addActionListener(e -> generate());
        btnClear.addActionListener(e -> clearSelection());

        chkAll.addActionListener(e -> {
            if (chkAll.isSelected()) {
                chkSelected.setSelected(false);
            } else if (!chkSelected.isSelected()) {
                chkSelected.setSelected(true);
            }
            updateSelectionMode();
        });

        chkSelected.addActionListener(e -> {
            if (chkSelected.isSelected()) {
                chkAll.setSelected(false);
            } else if (!chkAll.isSelected()) {
                chkAll.setSelected(true);
            }
            updateSelectionMode();
        });

        top.add(new JLabel("Month"));
        top.add(cmbMonth);
        top.add(new JLabel("Year"));
        top.add(cmbYear);
        top.add(chkAll);
        top.add(chkSelected);
        top.add(btnGenerate);
        top.add(btnClear);

        progressBar.setStringPainted(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(progressBar, BorderLayout.CENTER);
        bottom.add(lblStatus, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        updateSelectionMode();
    }

    @Override
    public void refreshData() {
        List<Employee> rows = employeeService.getEmployeeDirectory().stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .toList();

        DefaultTableModel model = new DefaultTableModel(new String[]{"Select", "Employee #", "Name", "Department"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 && chkSelected.isSelected();
            }
        };

        for (Employee e : rows) {
            model.addRow(new Object[]{false, String.valueOf(e.getEmployeeNumber()), e.getFirstName() + " " + e.getLastName(), e.getPosition() != null ? e.getPosition().getDepartment() : ""});
        }
        table.setModel(model);
        SwingModuleUtil.applyStandardTableStyle(table);
        table.getColumnModel().getColumn(0).setMinWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        updateSelectionMode();
    }

    private void clearSelection() {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(false, i, 0);
        }
    }

    private void updateSelectionMode() {
        boolean selectedMode = chkSelected.isSelected();
        table.setEnabled(selectedMode);
        btnClear.setEnabled(selectedMode);
        if (!selectedMode) {
            clearSelection();
        }
        table.repaint();
    }

    private void generate() {
        int month = cmbMonth.getSelectedIndex() + 1;
        Integer year = (Integer) cmbYear.getSelectedItem();
        if (year == null) {
            SwingModuleUtil.warn(this, "Validation", "Select valid month/year.");
            return;
        }

        List<Employee> all = employeeService.getEmployeeDirectory().stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .toList();

        List<Employee> target = new ArrayList<>();
        // Safety precedence: if Selected Employees is checked, always use explicit row picks.
        if (chkSelected.isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                Object selected = table.getValueAt(i, 0);
                if (selected instanceof Boolean && (Boolean) selected) {
                    Integer empNo = SwingModuleUtil.parseInteger(String.valueOf(table.getValueAt(i, 1)));
                    if (empNo != null) {
                        Employee emp = employeeService.getEmployee(empNo);
                        if (emp != null) {
                            target.add(emp);
                        }
                    }
                }
            }
        } else if (chkAll.isSelected()) {
            target.addAll(all);
        }

        if (target.isEmpty()) {
            SwingModuleUtil.warn(this, "Validation", "No employees selected for generation.");
            return;
        }

        progressBar.setMaximum(target.size());
        progressBar.setValue(0);

        SwingWorker<String, Integer> worker = new SwingWorker<>() {
            int generated = 0;
            int skipped = 0;
            final List<String> reasons = new ArrayList<>();
            Path outputFolder;

            @Override
            protected String doInBackground() {
                for (int i = 0; i < target.size(); i++) {
                    Employee e = target.get(i);
                    lblStatus.setText("Generating " + (i + 1) + "/" + target.size() + " for " + e.getEmployeeNumber());
                    try {
                        PayslipComputation c = financeService.computePayslip(e, year, month);
                        if (c == null) {
                            skipped++;
                            reasons.add(e.getEmployeeNumber() + ": no payroll data");
                        } else {
                            Path file = payslipPdfService.generatePayslipPdf(e, c);
                            outputFolder = file.getParent();
                            generated++;
                        }
                    } catch (Exception ex) {
                        skipped++;
                        reasons.add(e.getEmployeeNumber() + ": " + ex.getMessage());
                    }
                    publish(i + 1);
                }
                return "Processed: " + target.size() + "\nGenerated: " + generated + "\nSkipped: " + skipped;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int v = chunks.get(chunks.size() - 1);
                progressBar.setValue(v);
                progressBar.setString(v + "/" + target.size());
            }

            @Override
            protected void done() {
                try {
                    String summary = get();
                    if (!reasons.isEmpty()) {
                        summary += "\n\nSkipped details:\n" + String.join("\n", reasons);
                    }

                    showBulkSummaryDialog(summary, outputFolder);
                    lblStatus.setText("Completed");
                } catch (Exception ex) {
                    SwingModuleUtil.error(FinanceGeneratePayslipsPanel.this, "Error", ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showBulkSummaryDialog(String summary, Path outputFolder) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JTextArea txtSummary = new JTextArea(summary);
        txtSummary.setEditable(false);
        txtSummary.setLineWrap(true);
        txtSummary.setWrapStyleWord(true);
        txtSummary.setRows(10);
        txtSummary.setColumns(58);

        panel.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        if (outputFolder != null) {
            JPanel folderPanel = new JPanel(new BorderLayout(6, 0));
            folderPanel.add(new JLabel("Folder:"), BorderLayout.WEST);

            JTextField txtFolder = new JTextField(outputFolder.toAbsolutePath().toString());
            txtFolder.setEditable(false);
            txtFolder.setCaretPosition(0);
            folderPanel.add(txtFolder, BorderLayout.CENTER);

            panel.add(folderPanel, BorderLayout.SOUTH);

            String[] options = {"Open Folder", "Close"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    panel,
                    "Bulk Generation Summary",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (choice == 0) {
                try {
                    Desktop.getDesktop().open(outputFolder.toFile());
                } catch (Exception ex) {
                    SwingModuleUtil.error(this, "Open Folder Error", ex.getMessage());
                }
            }
            return;
        }

        JOptionPane.showMessageDialog(this, panel, "Bulk Generation Summary", JOptionPane.INFORMATION_MESSAGE);
    }
}

