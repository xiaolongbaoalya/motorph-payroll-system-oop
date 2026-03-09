package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.EmployeeService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

public class HrEmployeeDetailsPanel extends JPanel implements RefreshablePanel {
    private final EmployeeService employeeService;
    private final AuthService authService;
    private final Session session;

    private final JTable table;

    private final JTextField txtBasicEmpNo = basicField();
    private final JTextField txtBasicFirst = basicField();
    private final JTextField txtBasicLast = basicField();
    private final JTextField txtBasicBirthday = basicField();
    private final JTextField txtBasicDepartment = basicField();
    private final JTextField txtBasicPosition = basicField();
    private final JTextField txtBasicStatus = basicField();

    public HrEmployeeDetailsPanel(EmployeeService employeeService, AuthService authService, Session session) {
        this.employeeService = employeeService;
        this.authService = authService;
        this.session = session;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateBasicDataPanel();
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        JPanel basicPanel = buildBasicDataPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, basicPanel);
        splitPane.setResizeWeight(0.76);
        splitPane.setDividerLocation(760);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnView = new JButton("View Employee");
        JButton btnUpdate = new JButton("Update Employee");
        JButton btnArchive = new JButton("Archive Employee");
        JButton btnDelete = new JButton("Delete Employee");

        btnView.addActionListener(e -> viewSelected());
        btnUpdate.addActionListener(e -> updateSelected());
        btnArchive.addActionListener(e -> archiveSelected());
        btnDelete.addActionListener(e -> deleteSelected());

        boolean isAdmin = RoleResolver.resolve(session) == EffectiveRole.ADMIN;
        btnDelete.setEnabled(isAdmin);
        btnDelete.setVisible(isAdmin);

        actions.add(btnView);
        actions.add(btnUpdate);
        actions.add(btnArchive);
        actions.add(btnDelete);

        add(splitPane, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private JPanel buildBasicDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Data"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        addBasicRow(panel, gbc, 0, "Employee #:", txtBasicEmpNo);
        addBasicRow(panel, gbc, 1, "First Name:", txtBasicFirst);
        addBasicRow(panel, gbc, 2, "Last Name:", txtBasicLast);
        addBasicRow(panel, gbc, 3, "Birthday:", txtBasicBirthday);
        addBasicRow(panel, gbc, 4, "Department:", txtBasicDepartment);
        addBasicRow(panel, gbc, 5, "Position:", txtBasicPosition);
        addBasicRow(panel, gbc, 6, "Status:", txtBasicStatus);

        gbc.gridy = 14;
        gbc.weighty = 1.0;
        panel.add(new JPanel(), gbc);

        panel.setPreferredSize(new Dimension(290, 0));
        return panel;
    }

    private void addBasicRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.weighty = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }

    private JTextField basicField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setPreferredSize(new Dimension(190, 28));
        return tf;
    }

    @Override
    public void refreshData() {
        List<Employee> rows = employeeService.getEmployeeDirectory();
        Object[][] data = new Object[rows.size()][8];
        for (int i = 0; i < rows.size(); i++) {
            Employee e = rows.get(i);
            data[i][0] = e.getEmployeeNumber();
            data[i][1] = e.getLastName();
            data[i][2] = e.getFirstName();
            data[i][3] = e.getStatus();
            data[i][4] = e.getGovernmentID() != null ? e.getGovernmentID().getSss() : "";
            data[i][5] = e.getGovernmentID() != null ? e.getGovernmentID().getPhilHealth() : "";
            data[i][6] = e.getGovernmentID() != null ? e.getGovernmentID().getTin() : "";
            data[i][7] = e.getGovernmentID() != null ? e.getGovernmentID().getPagIbig() : "";
        }
        SwingModuleUtil.setTableData(table, new String[]{"Employee #", "Last Name", "First Name", "Status", "SSS", "PhilHealth", "TIN", "Pag-IBIG"}, data, false);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        updateBasicDataPanel();
    }

    private Employee selectedEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        Integer empNo = SwingModuleUtil.parseInteger(String.valueOf(table.getValueAt(row, 0)));
        return empNo == null ? null : employeeService.getEmployee(empNo);
    }

    private void updateBasicDataPanel() {
        Employee e = selectedEmployee();
        if (e == null) {
            txtBasicEmpNo.setText("");
            txtBasicFirst.setText("");
            txtBasicLast.setText("");
            txtBasicBirthday.setText("");
            txtBasicDepartment.setText("");
            txtBasicPosition.setText("");
            txtBasicStatus.setText("");
            return;
        }

        txtBasicEmpNo.setText(String.valueOf(e.getEmployeeNumber()));
        txtBasicFirst.setText(nullSafe(e.getFirstName()));
        txtBasicLast.setText(nullSafe(e.getLastName()));
        txtBasicBirthday.setText(String.valueOf(e.getBirthday()));
        txtBasicDepartment.setText(e.getPosition() != null ? nullSafe(e.getPosition().getDepartment()) : "");
        txtBasicPosition.setText(e.getPosition() != null ? nullSafe(e.getPosition().getJobTitle()) : "");
        txtBasicStatus.setText(nullSafe(e.getStatus()));
    }

    private void viewSelected() {
        Employee e = selectedEmployee();
        if (e == null) {
            SwingModuleUtil.warn(this, "Selection", "Select an employee first.");
            return;
        }

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setText(
                "Employee #: " + e.getEmployeeNumber() + "\n"
                        + "First Name: " + nullSafe(e.getFirstName()) + "\n"
                        + "Last Name: " + nullSafe(e.getLastName()) + "\n"
                        + "Birthday: " + e.getBirthday() + "\n"
                        + "Status: " + nullSafe(e.getStatus()) + "\n"
                        + "Supervisor: " + nullSafe(e.getSupervisorName()) + "\n\n"
                        + "Contact Details\n"
                        + "Address: " + (e.getContact() != null ? nullSafe(e.getContact().getAddress()) : "") + "\n"
                        + "Phone: " + (e.getContact() != null ? nullSafe(e.getContact().getPhone()) : "") + "\n\n"
                        + "Government Details\n"
                        + "SSS: " + (e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getSss()) : "") + "\n"
                        + "PhilHealth: " + (e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getPhilHealth()) : "") + "\n"
                        + "TIN: " + (e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getTin()) : "") + "\n"
                        + "Pag-IBIG: " + (e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getPagIbig()) : "") + "\n\n"
                        + "Work Details\n"
                        + "Position: " + (e.getPosition() != null ? nullSafe(e.getPosition().getJobTitle()) : "") + "\n"
                        + "Department: " + (e.getPosition() != null ? nullSafe(e.getPosition().getDepartment()) : "") + "\n\n"
                        + "Compensation\n"
                        + "Basic Salary: " + (e.getSalary() != null ? e.getSalary().getBasicSalary() : 0) + "\n"
                        + "Rice Subsidy: " + (e.getSalary() != null ? e.getSalary().getRiceSubsidy() : 0) + "\n"
                        + "Phone Allowance: " + (e.getSalary() != null ? e.getSalary().getPhoneAllowance() : 0) + "\n"
                        + "Clothing Allowance: " + (e.getSalary() != null ? e.getSalary().getClothingAllowance() : 0)
        );

        JDialog dialog = new JDialog((java.awt.Frame) null, "View Employee", true);
        dialog.setSize(560, 620);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JScrollPane(area));
        dialog.setVisible(true);
    }

    private void updateSelected() {
        Employee e = selectedEmployee();
        if (e == null) {
            SwingModuleUtil.warn(this, "Selection", "Select an employee first.");
            return;
        }

        JDialog dialog = new JDialog((java.awt.Frame) null, "Update Employee", true);
        dialog.setSize(1040, 560);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField txtEmpNo = new JTextField(String.valueOf(e.getEmployeeNumber()));
        JTextField txtFirst = new JTextField(nullSafe(e.getFirstName()));
        JTextField txtLast = new JTextField(nullSafe(e.getLastName()));
        DatePickerControl dpBirthday = new DatePickerControl(e.getBirthday(), 1900, LocalDate.now().getYear());
        JTextField txtAddress = new JTextField(e.getContact() != null ? nullSafe(e.getContact().getAddress()) : "");
        JTextField txtPhone = new JTextField(e.getContact() != null ? nullSafe(e.getContact().getPhone()) : "");
        JTextField txtSss = new JTextField(e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getSss()) : "");
        JTextField txtPhil = new JTextField(e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getPhilHealth()) : "");
        JTextField txtTin = new JTextField(e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getTin()) : "");
        JTextField txtPag = new JTextField(e.getGovernmentID() != null ? nullSafe(e.getGovernmentID().getPagIbig()) : "");
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Regular", "Probationary"});
        cmbStatus.setSelectedItem(e.getStatus());
        JTextField txtPosition = new JTextField(e.getPosition() != null ? nullSafe(e.getPosition().getJobTitle()) : "");
        JTextField txtDepartment = new JTextField(e.getPosition() != null ? nullSafe(e.getPosition().getDepartment()) : "");
        JTextField txtSupervisor = new JTextField(nullSafe(e.getSupervisorName()));
        JTextField txtBasic = new JTextField(e.getSalary() != null ? String.valueOf(e.getSalary().getBasicSalary()) : "0");
        JTextField txtRice = new JTextField(e.getSalary() != null ? String.valueOf(e.getSalary().getRiceSubsidy()) : "0");
        JTextField txtPhoneAllowance = new JTextField(e.getSalary() != null ? String.valueOf(e.getSalary().getPhoneAllowance()) : "0");
        JTextField txtClothing = new JTextField(e.getSalary() != null ? String.valueOf(e.getSalary().getClothingAllowance()) : "0");

        int row = 0;
        row = addField(form, gbc, row, 0, "Employee #", txtEmpNo);
        row = addField(form, gbc, row, 1, "First Name", txtFirst);
        row = addField(form, gbc, row, 2, "Last Name", txtLast);
        row = addField(form, gbc, row, 3, "Birthday", dpBirthday);
        row = addField(form, gbc, row, 4, "Address", txtAddress);
        row = addField(form, gbc, row, 5, "Phone", txtPhone);
        row = addField(form, gbc, row, 6, "SSS #", txtSss);
        row = addField(form, gbc, row, 7, "PhilHealth #", txtPhil);
        row = addField(form, gbc, row, 8, "TIN #", txtTin);
        row = addField(form, gbc, row, 9, "Pag-IBIG #", txtPag);
        row = addField(form, gbc, row, 10, "Status", cmbStatus);
        row = addField(form, gbc, row, 11, "Position", txtPosition);
        row = addField(form, gbc, row, 12, "Department", txtDepartment);
        row = addField(form, gbc, row, 13, "Supervisor", txtSupervisor);
        row = addField(form, gbc, row, 14, "Basic Salary", txtBasic);
        row = addField(form, gbc, row, 15, "Rice Subsidy", txtRice);
        row = addField(form, gbc, row, 16, "Phone Allowance", txtPhoneAllowance);
        addField(form, gbc, row, 17, "Clothing Allowance", txtClothing);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to update this employee?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (confirm != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                Integer empNo = SwingModuleUtil.parseInteger(txtEmpNo.getText());
                LocalDate birthday = dpBirthday.getDate();
                if (empNo == null) {
                    SwingModuleUtil.warn(dialog, "Validation", "Invalid employee number.");
                    return;
                }

                Employee updated = new Employee(
                        empNo,
                        txtFirst.getText().trim(),
                        txtLast.getText().trim(),
                        birthday,
                        new ContactInfo(txtAddress.getText().trim(), txtPhone.getText().trim()),
                        new GovernmentID(txtSss.getText().trim(), txtPhil.getText().trim(), txtTin.getText().trim(), txtPag.getText().trim()),
                        new Position(txtPosition.getText().trim(), txtDepartment.getText().trim()),
                        new Compensation(
                                Double.parseDouble(txtBasic.getText().trim()),
                                Double.parseDouble(txtRice.getText().trim()),
                                Double.parseDouble(txtPhoneAllowance.getText().trim()),
                                Double.parseDouble(txtClothing.getText().trim())
                        ),
                        String.valueOf(cmbStatus.getSelectedItem()),
                        txtSupervisor.getText().trim().isEmpty() ? "None" : txtSupervisor.getText().trim()
                );

                employeeService.updateEmployee(e.getEmployeeNumber(), updated, session);
                dialog.dispose();
                refreshData();
                SwingModuleUtil.info(this, "Success", "Employee updated.");
            } catch (Exception ex) {
                SwingModuleUtil.error(dialog, "Error", ex.getMessage());
            }
        });

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSave);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private int addField(JPanel form, GridBagConstraints gbc, int row, int index, String label, java.awt.Component field) {
        int y = index / 2;
        int columnPair = index % 2;

        gbc.gridx = columnPair * 2;
        gbc.gridy = y;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = columnPair * 2 + 1;
        gbc.weightx = 1;
        form.add(field, gbc);

        return row;
    }

    private void archiveSelected() {
        Employee e = selectedEmployee();
        if (e == null) {
            SwingModuleUtil.warn(this, "Selection", "Select an employee first.");
            return;
        }
        try {
            employeeService.archiveEmployee(e.getEmployeeNumber(), session);
            refreshData();
            SwingModuleUtil.info(this, "Success", "Employee archived.");
        } catch (Exception ex) {
            SwingModuleUtil.error(this, "Error", ex.getMessage());
        }
    }

    private void deleteSelected() {
        if (RoleResolver.resolve(session) != EffectiveRole.ADMIN) {
            SwingModuleUtil.warn(this, "Access Denied", "Only Admin users can delete employees.");
            return;
        }

        Employee e = selectedEmployee();
        if (e == null) {
            SwingModuleUtil.warn(this, "Selection", "Please select an employee to delete.");
            return;
        }

        JPasswordField passwordField = new JPasswordField();
        int passResult = JOptionPane.showConfirmDialog(this, passwordField, "Enter admin password", JOptionPane.OK_CANCEL_OPTION);
        if (passResult != JOptionPane.OK_OPTION) {
            SwingModuleUtil.info(this, "Cancelled", "Employee deletion was canceled.");
            return;
        }

        String password = new String(passwordField.getPassword());
        if (password.isBlank() || authService.login(session.getUser().getUsername(), password) == null) {
            SwingModuleUtil.warn(this, "Validation", "Password verification failed.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you really want to DELETE this Employee?",
                "Confirm Deletion",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) {
            SwingModuleUtil.info(this, "Cancelled", "Employee deletion was canceled.");
            return;
        }

        try {
            employeeService.removeEmployee(e.getEmployeeNumber(), session);
            refreshData();
            SwingModuleUtil.info(this, "Success", "Employee deleted successfully.");
        } catch (Exception ex) {
            SwingModuleUtil.error(this, "Error", ex.getMessage());
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}


