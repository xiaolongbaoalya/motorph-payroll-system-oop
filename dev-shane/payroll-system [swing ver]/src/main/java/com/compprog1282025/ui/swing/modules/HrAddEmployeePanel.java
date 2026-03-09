package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public class HrAddEmployeePanel extends JPanel implements RefreshablePanel {
    private final EmployeeService employeeService;
    private final Session session;

    private final JTextField txtEmpNo = new JTextField();
    private final JTextField txtFirst = new JTextField();
    private final JTextField txtLast = new JTextField();
    private final DatePickerControl dpBirthday = new DatePickerControl(LocalDate.now().minusYears(25), 1900, LocalDate.now().getYear());
    private final JTextArea txtAddress = new JTextArea(3, 20);
    private final JTextField txtPhone = new JTextField();

    private final JTextField txtSss = new JTextField();
    private final JTextField txtPhil = new JTextField();
    private final JTextField txtTin = new JTextField();
    private final JTextField txtPag = new JTextField();

    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField txtPosition = new JTextField();
    private final JComboBox<String> cmbDepartment = new JComboBox<>();
    private final JComboBox<String> cmbSupervisor = new JComboBox<>();

    private final JTextField txtBasic = new JTextField();
    private final JTextField txtRice = new JTextField();
    private final JTextField txtPhoneAllowance = new JTextField();
    private final JTextField txtClothing = new JTextField();

    public HrAddEmployeePanel(EmployeeService employeeService, Session session) {
        this.employeeService = employeeService;
        this.session = session;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(section("Personal Information", new java.awt.Component[][]{
                {new JLabel("Employee #"), txtEmpNo},
                {new JLabel("First Name"), txtFirst},
                {new JLabel("Last Name"), txtLast},
                {new JLabel("Birthday"), dpBirthday},
                {new JLabel("Address"), new JScrollPane(txtAddress)},
                {new JLabel("Phone"), txtPhone}
        }));

        content.add(section("Government Information", new java.awt.Component[][]{
                {new JLabel("SSS #"), txtSss},
                {new JLabel("PhilHealth #"), txtPhil},
                {new JLabel("TIN #"), txtTin},
                {new JLabel("Pag-IBIG #"), txtPag}
        }));

        content.add(section("Work Details", new java.awt.Component[][]{
                {new JLabel("Status"), cmbStatus},
                {new JLabel("Position"), txtPosition},
                {new JLabel("Department"), cmbDepartment},
                {new JLabel("Supervisor"), cmbSupervisor}
        }));

        content.add(section("Compensation Information", new java.awt.Component[][]{
                {new JLabel("Basic Salary"), txtBasic},
                {new JLabel("Rice Subsidy"), txtRice},
                {new JLabel("Phone Allowance"), txtPhoneAllowance},
                {new JLabel("Clothing Allowance"), txtClothing}
        }));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton btnAdd = new JButton("Add Employee");
        btnAdd.addActionListener(e -> addEmployee());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnAdd);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel section(String title, java.awt.Component[][] rows) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 12, 0),
                BorderFactory.createTitledBorder(title)
        ));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            form.add(rows[i][0], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            form.add(rows[i][1], gbc);
        }

        wrapper.add(form, BorderLayout.CENTER);
        return wrapper;
    }

    private void addEmployee() {
        try {
            Integer empNo = SwingModuleUtil.parseInteger(txtEmpNo.getText());
            LocalDate birthday = dpBirthday.getDate();

            if (empNo == null) {
                SwingModuleUtil.warn(this, "Validation", "Employee Number is required.");
                return;
            }
            if (employeeService.getEmployee(empNo) != null) {
                SwingModuleUtil.warn(this, "Validation", "Employee Number already exists. Please enter a unique Employee Number.");
                return;
            }

            if (isBlank(txtFirst.getText()) || isBlank(txtLast.getText()) || isBlank(txtPosition.getText())) {
                SwingModuleUtil.warn(this, "Validation", "Employee #, First Name, Last Name, and Position are required.");
                return;
            }

            String department = (String) cmbDepartment.getSelectedItem();
            String supervisor = (String) cmbSupervisor.getSelectedItem();

            Employee employee = new Employee(
                    empNo,
                    txtFirst.getText().trim(),
                    txtLast.getText().trim(),
                    birthday,
                    new ContactInfo(txtAddress.getText().trim(), txtPhone.getText().trim()),
                    new GovernmentID(txtSss.getText().trim(), txtPhil.getText().trim(), txtTin.getText().trim(), txtPag.getText().trim()),
                    new Position(txtPosition.getText().trim(), department == null ? "" : department),
                    new Compensation(
                            Double.parseDouble(txtBasic.getText().trim()),
                            Double.parseDouble(txtRice.getText().trim()),
                            Double.parseDouble(txtPhoneAllowance.getText().trim()),
                            Double.parseDouble(txtClothing.getText().trim())
                    ),
                    String.valueOf(cmbStatus.getSelectedItem()),
                    supervisor == null || supervisor.isBlank() ? "None" : supervisor
            );

            employeeService.addEmployee(employee, session);
            SwingModuleUtil.info(this, "Success", "Employee added.");
            clearForm();
            refreshData();
        } catch (NumberFormatException ex) {
            SwingModuleUtil.warn(this, "Validation", "Basic Salary, Rice Subsidy, Phone Allowance, and Clothing Allowance must be valid numbers.");
        } catch (Exception ex) {
            SwingModuleUtil.error(this, "Error", ex.getMessage());
        }
    }

    private void clearForm() {
        txtEmpNo.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtSss.setText("");
        txtPhil.setText("");
        txtTin.setText("");
        txtPag.setText("");
        txtPosition.setText("");
        txtBasic.setText("");
        txtRice.setText("");
        txtPhoneAllowance.setText("");
        txtClothing.setText("");
        cmbStatus.setSelectedIndex(0);
        dpBirthday.setDate(LocalDate.now());
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    @Override
    public void refreshData() {
        Set<String> departments = new LinkedHashSet<>();
        Set<String> supervisors = new LinkedHashSet<>();
        supervisors.add("None");

        for (Employee e : employeeService.getEmployeeDirectory()) {
            if (e.getPosition() != null && e.getPosition().getDepartment() != null && !e.getPosition().getDepartment().isBlank()) {
                departments.add(e.getPosition().getDepartment());
            }
            String name = (e.getFirstName() + " " + e.getLastName()).trim();
            if (!name.isBlank()) {
                supervisors.add(name);
            }
        }

        cmbDepartment.removeAllItems();
        if (departments.isEmpty()) {
            cmbDepartment.addItem("General");
        } else {
            for (String d : departments) {
                cmbDepartment.addItem(d);
            }
        }

        cmbSupervisor.removeAllItems();
        for (String s : supervisors) {
            cmbSupervisor.addItem(s);
        }
    }
}
