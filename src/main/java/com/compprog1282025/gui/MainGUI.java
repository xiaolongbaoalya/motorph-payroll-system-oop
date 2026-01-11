package com.compprog1282025.gui;

import com.compprog1282025.model.Employee;
import com.compprog1282025.model.Attendance;
import com.compprog1282025.model.Compensation;
import com.compprog1282025.model.ContactInfo;
import com.compprog1282025.model.GovernmentID;
import com.compprog1282025.model.Position;
import com.compprog1282025.service.*;
import com.compprog1282025.service.PayrollService;
import com.compprog1282025.util.CSVWriter;
import com.compprog1282025.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.time.YearMonth;
import java.util.List;
import java.awt.Dialog.ModalityType; 
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;


public class MainGUI extends JFrame {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;
    private final List<Employee> employees;
    
    // Normally variables such as private JTextField tfEmpNum are declared here
    // However, the way this program is built, text fields are created dynamically in the methods
    // So we will define them here for the sake of completeness, but they are not initialized
    private JTextField tfEmpNum;
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JTextField tfBirthday;
    private JTextField tfAddress;
    private JTextField tfPhone;
    private JTextField tfSSS;
    private JTextField tfPhilHealth;
    private JTextField tfTIN;
    private JTextField tfPagibig;
    private JTextField tfStatus;
    private JTextField tfPosition;
    private JTextField tfSupervisor;
    private JTextField tfBasicSalary;
    private JTextField tfRice;
    private JTextField tfPhoneAllow;
    private JTextField tfClothing;
    private JTextField tfSemiGross;
    private JTextField tfHourly;
    private List<Attendance> attendanceRecords;
    
    private static javax.swing.table.DefaultTableModel updateDeleteTableModel = null;

    public MainGUI() {
        // Load data
        DataLoaderService loader = new DataLoaderService();
        loader.loadAllData();

        employees = loader.getEmployees();
        attendanceRecords = loader.getAttendanceRecords();

        attendanceService = new AttendanceService(attendanceRecords);
        payrollService = new PayrollService(attendanceService);
        employeeService = new EmployeeService(employees);

        initUI();
    }
    
    private void initUI() {
        setTitle("Payroll System");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIManager.getColor("Panel.background"));
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(Box.createVerticalGlue(), gbc);

      
        JLabel label = new JLabel("Login as:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(new Color(33, 37, 41));
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(label, gbc);

        // Buttons
        JButton employeeBtn = new JButton("Employee");
        JButton adminBtn = new JButton("Admin");
        styleButton(employeeBtn);
        styleButton(adminBtn);
        employeeBtn.setPreferredSize(new Dimension(140, 40));
        adminBtn.setPreferredSize(new Dimension(140, 40));

        employeeBtn.addActionListener(e -> employeeLogin());
        adminBtn.addActionListener(e -> adminLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(employeeBtn);
        buttonPanel.add(adminBtn);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);

        // Add vertical glue after the buttons
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        add(panel);
        //UIHelper.applyThemeRecursively(panel);
    }
    
//Change color of the button to 0x1c3680 to match motorPH's logo. We can also add the logo if needed
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(0x1c3680));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1c3680), 1, true),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleButtonSmall(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(new Color(0x1c3680));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1c3680), 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
   
   private void employeeLogin() { // This is the code for employee login
        JDialog loginDialog = new JDialog(this, "Employee Login", true);
        loginDialog.setSize(400, 250);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        styleButtonSmall(loginBtn);
        styleButtonSmall(cancelBtn);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(loginBtn);

        loginDialog.add(formPanel, BorderLayout.CENTER);
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // add padding: top, left, bottom, right
        paddedPanel.add(buttonPanel, BorderLayout.CENTER);
        loginDialog.add(paddedPanel, BorderLayout.SOUTH);

        UIHelper.applyThemeRecursively(formPanel);
        UIHelper.applyThemeRecursively(buttonPanel);
        UIHelper.applyThemeRecursively(paddedPanel);
        UIHelper.applyThemeRecursively(loginDialog.getContentPane());

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()); // this is to NOT allow to trim password

            AuthService authService = new AuthService();

            try {
                authService.loadAccounts();
            } catch (IOException ex) {
                StyledErrorDialog(loginDialog, "Error", "Failed to load account data: " + ex.getMessage());
                return;
            }

            if (!authService.authenticate(username, password)) {
                StyledErrorDialog(this, "Login Failed", "Invalid username or password.");
                return;
            }

            String role = authService.getRole(username);
            if (role == null || !role.equalsIgnoreCase("employee")) {
                StyledErrorDialog(this, "Access Denied", "You do not have employee access.");
                return;
            }

            loginDialog.dispose();

            String input = StyledInputDialog(this, "Verify Identity", "Enter your Employee Number:");
            if (input == null) return;

            try {
                int empNum = Integer.parseInt(input.trim());
                Employee employee = employeeService.findEmployeeByNumber(empNum);

                if (employee != null) {
                    showEmployeeDashboard(employee);
                } else {
                    StyledErrorDialog(this, "Error", "Employee number not found.");
                }
            } catch (NumberFormatException ex) {
                StyledErrorDialog(this, "Error", "Invalid employee number.");
            }
        });

        cancelBtn.addActionListener(e -> loginDialog.dispose());

        loginDialog.setVisible(true);
    }


   private void adminLogin() { //this is the code for admin login

    JDialog loginDialog = new JDialog(this, "Admin Login", true);
    loginDialog.setSize(400, 250);
    loginDialog.setLocationRelativeTo(this);
    loginDialog.setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel usernameLabel = new JLabel("Username:");
    JTextField usernameField = new JTextField();
    usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

    JLabel passwordLabel = new JLabel("Password:");
    JPasswordField passwordField = new JPasswordField();
    passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

    formPanel.add(usernameLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(usernameField);
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(passwordLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(passwordField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton loginBtn = new JButton("Login");
    JButton cancelBtn = new JButton("Cancel");
    styleButtonSmall(loginBtn);
    styleButtonSmall(cancelBtn);

    buttonPanel.add(cancelBtn);
    buttonPanel.add(loginBtn);

    loginDialog.add(formPanel, BorderLayout.CENTER);
    JPanel paddedPanel = new JPanel(new BorderLayout());
    paddedPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // add padding: top, left, bottom, right
    paddedPanel.add(buttonPanel, BorderLayout.CENTER);
    loginDialog.add(paddedPanel, BorderLayout.SOUTH);

    UIHelper.applyThemeRecursively(formPanel);
    UIHelper.applyThemeRecursively(buttonPanel);
    UIHelper.applyThemeRecursively(paddedPanel);
    UIHelper.applyThemeRecursively(loginDialog.getContentPane());

    cancelBtn.addActionListener(e -> loginDialog.dispose());

    loginBtn.addActionListener(e -> {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()); // do NOT trim password

        AuthService authService = new AuthService();

        try {
            authService.loadAccounts();
        } catch (IOException ex) {
            StyledErrorDialog(loginDialog, "Error", "Failed to load account data: " + ex.getMessage());
            return;
        }
        
        if (!authService.authenticate(username, password)) {
            StyledErrorDialog(this, "Login Failed", "Invalid username or password.");
            return;
        }

        if (!authService.authenticate(username, password)) {
            StyledErrorDialog(loginDialog, "Error", "Invalid login credentials. Access denied.");
            return;
        }

        String role = authService.getRole(username);

        if (role == null || !role.trim().equalsIgnoreCase("admin")) {
            StyledErrorDialog(loginDialog, "Access Denied", "You do not have admin privileges.");
            return;
        }

        loginDialog.dispose();
        showAdminMenu();
    });

    loginDialog.setVisible(true);
}

    private void showEmployeeDashboard(Employee employee) {
        JDialog dialog = new JDialog(this, "Employee Dashboard - " + employee.getFullName(), true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("Employee Dashboard");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(16, 0, 8, 0));
        dialog.add(header, BorderLayout.NORTH);

        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(245, 248, 250));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(18, 24, 18, 24)
        ));
        detailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel basicInfoHeader = new JLabel("Basic Information");
        basicInfoHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        basicInfoHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        detailsPanel.add(basicInfoHeader);

        JLabel nameLabel = new JLabel("Name: " + employee.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel positionLabel = new JLabel("Position: " + employee.getPosition());
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel rateLabel = new JLabel(String.format("Hourly Rate: %.2f", employee.getHourlyRate()));
        rateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(positionLabel);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(rateLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        JLabel compHeader = new JLabel("Compensations:");
        compHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        compHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JLabel riceLabel = new JLabel(String.format("  Rice Subsidy: %.2f", employee.getCompensation().getRiceSubsidy()));
        riceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel phoneLabel = new JLabel(String.format("  Phone Allowance: %.2f", employee.getCompensation().getPhoneAllowance()));
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel clothingLabel = new JLabel(String.format("  Clothing Allowance: %.2f", employee.getCompensation().getClothingAllowance()));
        clothingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        detailsPanel.add(compHeader);
        detailsPanel.add(Box.createVerticalStrut(2));
        detailsPanel.add(riceLabel);
        detailsPanel.add(Box.createVerticalStrut(2));
        detailsPanel.add(phoneLabel);
        detailsPanel.add(Box.createVerticalStrut(2));
        detailsPanel.add(clothingLabel);
        detailsPanel.add(Box.createVerticalStrut(2));

        dialog.add(detailsPanel, BorderLayout.CENTER);

        JButton viewSalaryBtn = new JButton("View Monthly Salary");
        JButton closeBtn = new JButton("Close");
        styleButtonSmall(viewSalaryBtn);
        viewSalaryBtn.setPreferredSize(new Dimension(180, 36));
        viewSalaryBtn.setMaximumSize(new Dimension(180, 36));
        styleButtonSmall(closeBtn);
        closeBtn.setPreferredSize(new Dimension(100, 36));
        closeBtn.setMaximumSize(new Dimension(100, 36));

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnPanel.setBackground(dialog.getBackground());
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(viewSalaryBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        btnPanel.add(closeBtn);
        btnPanel.add(Box.createHorizontalGlue());

        viewSalaryBtn.addActionListener(e -> {
        JDialog inputDialog = new JDialog(dialog, "Select Month and Year", true);
        inputDialog.setLayout(new BorderLayout(5, 5));
        inputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Inner input panel with grid layout
        JPanel inputGrid = new JPanel(new GridLayout(2, 2, 5, 5));

        JLabel monthLabel = new JLabel("Select Month:");
        String[] months = new DateFormatSymbols().getMonths();
        JComboBox<String> monthComboBox = new JComboBox<>();
        for (int i = 0; i < 12; i++) {
            monthComboBox.addItem(String.format("%02d - %s", i + 1, months[i]));
        }

        JLabel yearLabel = new JLabel("Select Year:");
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 10; i <= currentYear + 5; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(currentYear);

        inputGrid.add(monthLabel);
        inputGrid.add(monthComboBox);
        inputGrid.add(yearLabel);
        inputGrid.add(yearComboBox);

        // Wrap grid in padded panel to avoid sticking
        JPanel paddedInputPanel = new JPanel(new BorderLayout());
        paddedInputPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10)); // padding: top, left, bottom, right
        paddedInputPanel.add(inputGrid, BorderLayout.CENTER);

        inputDialog.add(paddedInputPanel, BorderLayout.CENTER);

        // Custom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        styleButtonSmall(okButton);
        styleButtonSmall(cancelButton);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        inputDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set wider size before showing
        inputDialog.pack();
        inputDialog.setSize(inputDialog.getWidth() + 120, inputDialog.getHeight());
        inputDialog.setLocationRelativeTo(dialog);
        inputDialog.setResizable(false);

        final int[] result = {JOptionPane.CANCEL_OPTION};

        okButton.addActionListener(e2 -> {
            result[0] = JOptionPane.OK_OPTION;
            inputDialog.dispose();
        });

        cancelButton.addActionListener(e2 -> {
            result[0] = JOptionPane.CANCEL_OPTION;
            inputDialog.dispose();
        });

        inputDialog.setVisible(true);

        // Handle result after dialog closes
        if (result[0] == JOptionPane.OK_OPTION) {
            try {
                int month = monthComboBox.getSelectedIndex() + 1;
                int year = (Integer) yearComboBox.getSelectedItem();
                YearMonth ym = YearMonth.of(year, month);

                double gross = payrollService.calculateMonthlySalary(employee, ym);
                double net = payrollService.calculateNetSalary(employee, ym);

                String message = String.format("Payroll Summary for %s\nGross Salary: %.2f\nNet Salary: %.2f", ym, gross, net);
                StyledInfoDialog(dialog, "Payroll Summary", message);
            } catch (Exception ex) {
                StyledErrorDialog(dialog, "Error", "Invalid date selection.");
            }
        }
    });


    closeBtn.addActionListener(e -> dialog.dispose());
    dialog.add(btnPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);

    }

    private void showAdminMenu() {// THIS IS DONE
        JDialog dialog = new JDialog(this, "Admin Menu", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] menuOptions = {
                "View/Add Employees",
                "Update/Delete Employees",
                "Monthly Payroll Reports",
                "Weekly Payroll Reports",
                "Export Payroll Report to CSV",
                "Exit"

        };

      
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

      
        menuPanel.add(Box.createVerticalGlue());

       
        for (String option : menuOptions) {
            JButton button = new JButton(option);
            styleButton(button);
            button.setPreferredSize(new Dimension(300, 40));
            button.setMaximumSize(new Dimension(300, 40));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            menuPanel.add(Box.createVerticalStrut(10)); // space between buttons
            menuPanel.add(button);

            button.addActionListener(e -> {
                switch (option) {
                    case "View/Add Employees" -> showViewAddEmployeeList(dialog);
                    case "Update/Delete Employees" -> showUpdateDeleteEmployeeList(dialog);
                    case "View Employee Record" -> {
                        Employee emp = promptEmployeeNumber(dialog);
                        if (emp != null) showDetailedEmployeeDialog(dialog, emp);
                    }
                    case "Monthly Payroll Reports" -> salaryCalculationMenu(dialog); // added from MS1 og view
                    case "Weekly Payroll Reports" -> showWeeklySalaryMenu(dialog); // added from MS1 og view
                    case "Export Payroll Report to CSV" -> exportPayrollReport(dialog);
                    case "Exit" -> dialog.dispose();
        }
    });
}
        // sdd more 'glue' after buttons to keep everything vertically centered
        menuPanel.add(Box.createVerticalGlue());
        
        dialog.add(menuPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }
    
    private void showWeeklySalaryMenu(Component parent) {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Weekly Payroll Reports", true);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(parent);
    dialog.setLayout(new BorderLayout());

    String[] options = {
        "Gross Salary for specific employee",
        "Net Salary for specific employee",
        "Close"
    };

    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    menuPanel.setBackground(Color.WHITE);
    menuPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

    menuPanel.add(Box.createVerticalGlue());

    for (String option : options) {
        JButton button = new JButton(option);
        styleButton(button);
        button.setPreferredSize(new Dimension(300, 40));
        button.setMaximumSize(new Dimension(300, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(button);

        button.addActionListener(e -> {
            switch (option) {
                case "Gross Salary for specific employee" -> {
                    Employee emp = promptEmployeeNumber(dialog);
                    if (emp == null) return;
                    String input = StyledInputDialog(dialog, "Enter Reference Date", "Enter a reference date (yyyy-MM-dd):");
                    if (input == null) return;
                    try {
                        LocalDate date = LocalDate.parse(input.trim());
                        double gross = payrollService.calculateWeeklySalary(emp, date);
                        StyledInfoDialog(dialog, "Gross Weekly Salary",
                                String.format("Gross Salary for %s on week of %s: %.2f", emp.getFullName(), date, gross));
                    } catch (Exception ex) {
                        StyledErrorDialog(dialog, "Error", "Invalid date format. Use yyyy-MM-dd.");
                    }
                }
                case "Net Salary for specific employee" -> {
                    Employee emp = promptEmployeeNumber(dialog);
                    if (emp == null) return;
                    String input = StyledInputDialog(dialog, "Enter Reference Date", "Enter a reference date (yyyy-MM-dd):");
                    if (input == null) return;
                    try {
                        LocalDate date = LocalDate.parse(input.trim());
                        double net = payrollService.calculateNetWeeklySalary(emp, date);
                        StyledInfoDialog(dialog, "Net Weekly Salary",
                                String.format("Net Salary for %s on week of %s: %.2f", emp.getFullName(), date, net));
                    } catch (Exception ex) {
                        StyledErrorDialog(dialog, "Error", "Invalid date format. Use yyyy-MM-dd.");
                    }
                }
                case "Close" -> dialog.dispose();
            }
        });
    }

    menuPanel.add(Box.createVerticalGlue());
    dialog.add(menuPanel, BorderLayout.CENTER);
    dialog.setVisible(true);
}
    
    private void showDetailedEmployeeDialog(Component parent, Employee emp) {
        // Create dialog
        JDialog detailDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(parent), "Employee Details", Dialog.ModalityType.APPLICATION_MODAL);
        detailDialog.setLayout(new BorderLayout());

        // === Local helper: section header
        Function<String, JLabel> sectionHeader = title -> {
            JLabel label = new JLabel(title, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 17));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            return label;
        };

        // === Local helper: slightly bold info row
        Function<String, JPanel> infoRow = text -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            row.add(label);
            return row;
        };

        // === Info Panel ===
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        infoPanel.setBackground(new Color(250, 252, 255));

        infoPanel.add(sectionHeader.apply("Personal Information"));
        infoPanel.add(infoRow.apply("Full Name: " + emp.getFullName()));
        infoPanel.add(infoRow.apply("Employee No.: " + emp.getEmployeeNumber()));
        infoPanel.add(infoRow.apply("Birthday: " + emp.getBirthday()));
        infoPanel.add(infoRow.apply("Status: " + emp.getStatus()));

        infoPanel.add(sectionHeader.apply("Position"));
        infoPanel.add(infoRow.apply("Position: " + emp.getPosition().getPosition()));
        infoPanel.add(infoRow.apply("Supervisor: "
                + (emp.getPosition().getSupervisor() != null ? emp.getPosition().getSupervisor().getFullName() : "None")));

        infoPanel.add(sectionHeader.apply("Contact Information"));
        infoPanel.add(infoRow.apply("Address: " + emp.getContact().getAddress()));
        infoPanel.add(infoRow.apply("Phone: " + emp.getContact().getPhoneNumber()));

        infoPanel.add(sectionHeader.apply("Government IDs"));
        infoPanel.add(infoRow.apply("SSS #: " + emp.getGovernmentID().getSss()));
        infoPanel.add(infoRow.apply("PhilHealth #: " + emp.getGovernmentID().getPhilhealth()));
        infoPanel.add(infoRow.apply("TIN #: " + emp.getGovernmentID().getTin()));
        infoPanel.add(infoRow.apply("Pag-IBIG #: " + emp.getGovernmentID().getPagibig()));

        infoPanel.add(sectionHeader.apply("Compensation"));
        infoPanel.add(infoRow.apply("Basic Salary: ₱" + emp.getCompensation().getBasicSalary()));
        infoPanel.add(infoRow.apply("Hourly Rate: ₱" + emp.getHourlyRate()));
        infoPanel.add(infoRow.apply("Rice Subsidy: ₱" + emp.getCompensation().getRiceSubsidy()));
        infoPanel.add(infoRow.apply("Phone Allowance: ₱" + emp.getCompensation().getPhoneAllowance()));
        infoPanel.add(infoRow.apply("Clothing Allowance: ₱" + emp.getCompensation().getClothingAllowance()));
        infoPanel.add(infoRow.apply("Semi-monthly Gross: ₱" + emp.getCompensation().getSemiGross()));

        // === Payroll Period ===
        infoPanel.add(sectionHeader.apply("Select Payroll Period"));

        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        String[] months = Arrays.copyOf(new DateFormatSymbols().getMonths(), 12);
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setPrototypeDisplayValue("September");

        int currentYear = Year.now().getValue();
        Integer[] years = IntStream.rangeClosed(currentYear - 5, currentYear + 1).boxed().toArray(Integer[]::new);
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setPrototypeDisplayValue(9999);

        dropdownPanel.add(new JLabel("Month:"));
        dropdownPanel.add(monthCombo);
        dropdownPanel.add(new JLabel("Year:"));
        dropdownPanel.add(yearCombo);
        infoPanel.add(dropdownPanel);

        infoPanel.add(Box.createVerticalStrut(10));
        JButton calcBtn = new JButton("Calculate Payroll");

        // Wrap in a panel with centered layout
        JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerButtonPanel.setOpaque(false); // make sure it blends in with the background
        centerButtonPanel.add(calcBtn);

        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(centerButtonPanel);


        // === Result Labels
        JLabel payrollForLabel = new JLabel();
        JLabel grossLabel = new JLabel();
        JLabel netLabel = new JLabel();

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(infoRow.apply("Payroll for: "));
        infoPanel.add(infoRow.apply("Gross Salary: "));
        infoPanel.add(infoRow.apply("Net Salary: "));


        // === Theme
        UIHelper.applyThemeRecursively(infoPanel);

        // === Payroll calculation
        calcBtn.addActionListener(e -> {
            try {
                int month = monthCombo.getSelectedIndex() + 1;
                int year = (Integer) yearCombo.getSelectedItem();
                YearMonth ym = YearMonth.of(year, month);

                double gross = payrollService.calculateMonthlySalary(emp, ym);
                double net = payrollService.calculateNetSalary(emp, ym);

                infoPanel.remove(infoPanel.getComponentCount() - 3);
                infoPanel.remove(infoPanel.getComponentCount() - 2);
                infoPanel.remove(infoPanel.getComponentCount() - 1);

                infoPanel.add(infoRow.apply("Payroll for: " + ym));
                infoPanel.add(infoRow.apply(String.format("Gross Salary: ₱%.2f", gross)));
                infoPanel.add(infoRow.apply(String.format("Net Salary: ₱%.2f", net)));

                infoPanel.revalidate();
                infoPanel.repaint();
            } catch (Exception ex) {
                StyledErrorDialog(detailDialog, "Error", "Invalid date selection.");
            }
        });

        // === Scrollable Layout
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setPreferredSize(new Dimension(520, 650));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(infoPanel.getBackground());

        detailDialog.add(scrollPane, BorderLayout.CENTER);
        detailDialog.pack();
        detailDialog.setResizable(true);
        detailDialog.setLocationRelativeTo(parent);
        detailDialog.setVisible(true);
    }

    private void showUpdateDeleteEmployeeList(Component parent) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Update/Delete Employee", true);
        dialog.setSize(1300, 700);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Table setup (no Status column)
        String[] columns = {
            "Employee #", "Last Name", "First Name",
            "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable employeeTable = new JTable(tableModel);
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                emp.getEmployeeNumber(),
                emp.getLastName(),
                emp.getFirstName(),
                emp.getGovernmentID().getSss(),
                emp.getGovernmentID().getPhilhealth(),
                emp.getGovernmentID().getTin(),
                emp.getGovernmentID().getPagibig()
            });
        }
        // Modern table design (copied from View/Add Employee)
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        employeeTable.setRowHeight(32);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setSelectionBackground(new Color(96, 96, 96));
        employeeTable.setSelectionForeground(Color.WHITE);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        employeeTable.getTableHeader().setBackground(new Color(0x1c3680));
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Employee Data Pane (right, scrollable)
        JPanel formPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Employee Data");
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.setBorder(border);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 15);
        String[] labels = {"Employee Number", "First Name", "Last Name", "Birthday", "Position", "Position Type", "Status", "Supervisor", "Address", "Phone", "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #", "Basic Salary", "Hourly Rate", "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Semi-monthly Gross"};
        JComponent[] valueFields = new JComponent[labels.length];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel l = new JLabel(labels[i] + ":");
            l.setFont(labelFont);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            formPanel.add(l, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            if ("Address".equals(labels[i])) {
                JTextArea addressArea = new JTextArea();
                addressArea.setFont(valueFont);
                addressArea.setLineWrap(true);
                addressArea.setWrapStyleWord(true);
                addressArea.setRows(2);
                addressArea.setEditable(false);
                addressArea.setOpaque(true); // so it has white background
                addressArea.setBackground(Color.WHITE);
                addressArea.setBorder(BorderFactory.createLineBorder(new java.awt.Color(176, 176, 176), 1));
                addressArea.setFocusable(false);
                formPanel.add(addressArea, gbc);
                valueFields[i] = addressArea;
            } else if ("Birthday".equals(labels[i])) {
                String[] months = java.util.Arrays.stream(java.time.Month.values())
                    .map(m -> m.toString().substring(0,1) + m.toString().substring(1).toLowerCase().substring(0,2)).toArray(String[]::new);
                JComboBox<String> cbMonth = new JComboBox<>(months);
                Integer[] days = java.util.stream.IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new);
                JComboBox<Integer> cbDay = new JComboBox<>(days);
                int currentYear = java.time.LocalDate.now().getYear();
                Integer[] years = java.util.stream.IntStream.rangeClosed(currentYear-70, currentYear-16).boxed().sorted((a,b)->b-a).toArray(Integer[]::new);
                JComboBox<Integer> cbYear = new JComboBox<>(years);
                JPanel birthdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                birthdayPanel.setOpaque(false);
                birthdayPanel.add(cbMonth);
                birthdayPanel.add(cbDay);
                birthdayPanel.add(cbYear);
                formPanel.add(birthdayPanel, gbc);
                valueFields[i] = birthdayPanel;
            } else {
                JTextField v = new JTextField();
                v.setFont(valueFont);
                v.setEditable(false);
                v.setBorder(BorderFactory.createLineBorder(new java.awt.Color(176, 176, 176), 1));
                v.setBackground(Color.WHITE);
                v.setHorizontalAlignment(SwingConstants.LEFT);
                formPanel.add(v, gbc);
                valueFields[i] = v;
            }
        }
        // Increase text field size
        Dimension fieldSize = new Dimension(0, 25);
        for (JComponent comp : valueFields) {
            if (comp instanceof JTextField field) {
                field.setPreferredSize(fieldSize);
            }
        }

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setPreferredSize(new Dimension(520, 500));
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        // Save button for editing
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new java.awt.Color(0x1c3680));
        saveBtn.setForeground(java.awt.Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
        saveBtn.setVisible(true);   // Always visible
        saveBtn.setEnabled(false);  // Disabled until Update is clicked

        JPanel saveBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        saveBtnPanel.setOpaque(false);
        saveBtnPanel.add(saveBtn);
        
        // Add below the scroll pane
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(formScroll, BorderLayout.CENTER);
        rightPanel.add(saveBtnPanel, BorderLayout.SOUTH);
        dialog.add(rightPanel, BorderLayout.EAST);

        // Update fields on row selection
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = employeeTable.getSelectedRow();
                if (row != -1) {
                    Employee emp = employees.stream()
                        .filter(em -> em.getEmployeeNumber() == (int) tableModel.getValueAt(row, 0))
                        .findFirst().orElse(null);
                    if (emp != null) {
                        Object[] values = {
                            String.valueOf(emp.getEmployeeNumber()),
                            emp.getFirstName(),
                            emp.getLastName(),
                            emp.getBirthday(),
                            emp.getPosition().getPosition(),
                            emp.getPosition().getPosition(),
                            emp.getStatus(),
                            emp.getPosition().getSupervisor() != null ? emp.getPosition().getSupervisor().getFullName() : "None",
                            emp.getContact().getAddress(),
                            emp.getContact().getPhoneNumber(),
                            emp.getGovernmentID().getSss(),
                            emp.getGovernmentID().getPhilhealth(),
                            emp.getGovernmentID().getTin(),
                            emp.getGovernmentID().getPagibig(),
                            "₱" + emp.getCompensation().getBasicSalary(),
                            "₱" + emp.getHourlyRate(),
                            "₱" + emp.getCompensation().getRiceSubsidy(),
                            "₱" + emp.getCompensation().getPhoneAllowance(),
                            "₱" + emp.getCompensation().getClothingAllowance(),
                            "₱" + emp.getCompensation().getSemiGross()
                        };
                        for (int i = 0; i < valueFields.length; i++) {
                            if (valueFields[i] instanceof JTextField v) {
                                v.setText(values[i].toString());
                            } else if (valueFields[i] instanceof JTextArea a) {
                                a.setText(values[i].toString());
                            } else if (valueFields[i] instanceof JPanel p && i == 3) { // Birthday dropdowns
                                LocalDate bday = (LocalDate) values[i];
                                @SuppressWarnings("unchecked")
                                JComboBox<String> cbMonth = (JComboBox<String>) p.getComponent(0);
                                @SuppressWarnings("unchecked")
                                JComboBox<Integer> cbDay = (JComboBox<Integer>) p.getComponent(1);
                                @SuppressWarnings("unchecked")
                                JComboBox<Integer> cbYear = (JComboBox<Integer>) p.getComponent(2);
                                cbMonth.setSelectedIndex(bday.getMonthValue() - 1);
                                cbDay.setSelectedItem(bday.getDayOfMonth());
                                cbYear.setSelectedItem(bday.getYear());
                            }
                        }
                        // Make all fields non-editable by default
                        for (int i = 0; i < valueFields.length; i++) {
                            if (valueFields[i] instanceof JTextField v) {
                                v.setEditable(false);
                            } else if (valueFields[i] instanceof JTextArea a) {
                                a.setEditable(false);
                            } else if (valueFields[i] instanceof JPanel p && i == 3) {
                                for (Component c : p.getComponents()) {
                                    c.setEnabled(false);
                                }
                            }
                        }
                        saveBtn.setVisible(true);
                        saveBtn.setEnabled(false); // Disable by default
                    }
                } else {
                    for (JComponent v : valueFields) {
                        if (v instanceof JTextField f) f.setText("");
                        else if (v instanceof JTextArea a) a.setText("");
                        else if (v instanceof JPanel p) {
                            @SuppressWarnings("unchecked")
                            JComboBox<String> cbMonth = (JComboBox<String>) p.getComponent(0);
                            @SuppressWarnings("unchecked")
                            JComboBox<Integer> cbDay = (JComboBox<Integer>) p.getComponent(1);
                            @SuppressWarnings("unchecked")
                            JComboBox<Integer> cbYear = (JComboBox<Integer>) p.getComponent(2);
                            cbMonth.setSelectedIndex(0);
                            cbDay.setSelectedIndex(0);
                            cbYear.setSelectedIndex(0);
                        }
                    }
                    saveBtn.setVisible(true);
                    saveBtn.setEnabled(false); // Disable by default

                }
            }
        });

        // Buttons panel (Update, Delete)
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        // Match style to View Employee/New Employee
        updateBtn.setPreferredSize(new Dimension(180, 38));
        deleteBtn.setPreferredSize(new Dimension(180, 38));
        updateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        updateBtn.setBackground(new java.awt.Color(0x1c3680));
        deleteBtn.setBackground(new java.awt.Color(0x1c3680));
        updateBtn.setForeground(java.awt.Color.WHITE);
        deleteBtn.setForeground(java.awt.Color.WHITE);
        updateBtn.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0x4f, 0x5e, 0xc0), 1, false));
        deleteBtn.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0x4f, 0x5e, 0xc0), 1, false));
        updateBtn.setFocusPainted(false);
        deleteBtn.setFocusPainted(false);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 20px horizontal gap
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0)); // Top, Left, Bottom, Right

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Enable buttons only when a row is selected
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            int row = employeeTable.getSelectedRow();
            updateBtn.setEnabled(row >= 0);
            deleteBtn.setEnabled(row >= 0);
        });

        // Update action
        updateBtn.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) return;
            // Make all fields editable including Employee Number
            for (int i = 0; i < valueFields.length; i++) {
                if (valueFields[i] instanceof JTextField v) {
                    v.setEditable(true);
                } else if (valueFields[i] instanceof JTextArea a) {
                    a.setEditable(true);
                    a.setFocusable(true);
                    a.setOpaque(true);
                    a.setBorder(BorderFactory.createLineBorder(new java.awt.Color(176, 176, 176), 1));
                } else if (valueFields[i] instanceof JPanel p && i == 3) {
                    for (Component c : p.getComponents()) {
                        c.setEnabled(true);
                    }
                }
            }
            saveBtn.setVisible(true);
            saveBtn.setEnabled(true); // Enable Save when Update is clicked

        });

        // Save action
        saveBtn.addActionListener(e -> {
            System.out.println("Save button clicked!");
            int row = employeeTable.getSelectedRow();
            if (row < 0) return;
            int empNum = (int) tableModel.getValueAt(row, 0);
            int index = -1;
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i).getEmployeeNumber() == empNum) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                System.out.println("Employee not found in list!");
                StyledErrorDialog(dialog, "Error", "Employee not found. Please refresh the list.");
                reloadEmployeesAndRefreshTable(tableModel);
                return;
            }
            Employee emp = employees.get(index);
            try {
                int newEmpNum = Integer.parseInt(((JTextField)valueFields[0]).getText().trim());
                emp.setEmployeeNumber(newEmpNum);
                emp.setFirstName(((JTextField)valueFields[1]).getText().trim());
                emp.setLastName(((JTextField)valueFields[2]).getText().trim());
                // Birthday
                JPanel bdayPanel = (JPanel) valueFields[3];
                @SuppressWarnings("unchecked")
                JComboBox<String> cbMonth = (JComboBox<String>) bdayPanel.getComponent(0);
                @SuppressWarnings("unchecked")
                JComboBox<Integer> cbDay = (JComboBox<Integer>) bdayPanel.getComponent(1);
                @SuppressWarnings("unchecked")
                JComboBox<Integer> cbYear = (JComboBox<Integer>) bdayPanel.getComponent(2);
                int month = cbMonth.getSelectedIndex() + 1;
                int day = (Integer) cbDay.getSelectedItem();
                int year = (Integer) cbYear.getSelectedItem();
                emp.setBirthday(LocalDate.of(year, month, day));
                emp.getContact().setAddress(((JTextArea)valueFields[8]).getText().trim());
                emp.getContact().setPhoneNumber(((JTextField)valueFields[9]).getText().trim());
                emp.getGovernmentID().setSss(((JTextField)valueFields[10]).getText().trim());
                emp.getGovernmentID().setPhilhealth(((JTextField)valueFields[11]).getText().trim());
                emp.getGovernmentID().setTin(((JTextField)valueFields[12]).getText().trim());
                emp.getGovernmentID().setPagibig(((JTextField)valueFields[13]).getText().trim());
                emp.setStatus(((JTextField)valueFields[6]).getText().trim());
                emp.getPosition().setPosition(((JTextField)valueFields[4]).getText().trim());
                // Compensation
                emp.getCompensation().setBasicSalary(Double.parseDouble(((JTextField)valueFields[14]).getText().replace("₱", "").replace(",", "").trim()));
                emp.setHourlyRate(Double.parseDouble(((JTextField)valueFields[15]).getText().replace("₱", "").replace(",", "").trim()));
                emp.getCompensation().setRiceSubsidy(Double.parseDouble(((JTextField)valueFields[16]).getText().replace("₱", "").replace(",", "").trim()));
                emp.getCompensation().setPhoneAllowance(Double.parseDouble(((JTextField)valueFields[17]).getText().replace("₱", "").replace(",", "").trim()));
                emp.getCompensation().setClothingAllowance(Double.parseDouble(((JTextField)valueFields[18]).getText().replace("₱", "").replace(",", "").trim()));
                emp.getCompensation().setSemiGross(Double.parseDouble(((JTextField)valueFields[19]).getText().replace("₱", "").replace(",", "").trim()));
                System.out.println("Updated: " + emp.getEmployeeNumber() + " " + emp.getFirstName() + " " + emp.getLastName());
                System.out.println("Writing to CSV...");
                com.compprog1282025.util.CSVWriter.writeAllEmployeesToCSV(employees);
                System.out.println("CSV write done.");
                reloadEmployeesAndRefreshTable(tableModel);
                if (tableModel.getRowCount() > 0) {
                    System.out.println("Table refreshed. First row: " + tableModel.getValueAt(0, 0));
                } else {
                    System.out.println("Table refreshed. No rows.");
                }
                // Make all fields non-editable again
                for (int i = 0; i < valueFields.length; i++) {
                    if (valueFields[i] instanceof JTextField v) {
                        v.setEditable(false);
                    } else if (valueFields[i] instanceof JTextArea a) {
                        a.setEditable(false);
                        a.setFocusable(false);
                        a.setOpaque(false);
                        a.setBorder(null);
                    } else if (valueFields[i] instanceof JPanel p && i == 3) {
                        for (Component c : p.getComponents()) {
                            c.setEnabled(false);
                        }
                    }
                }
                saveBtn.setVisible(false);
                // Show success dialog
                JDialog successDialog = new JDialog(dialog, "Success", true);
                successDialog.setLayout(new BorderLayout(10, 10));
                JLabel successLabel = new JLabel("Employee updated successfully!");
                successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                successLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
                JButton okBtn = new JButton("OK");
                styleButtonSmall(okBtn);
                okBtn.setBackground(new Color(0x1c3680));
                okBtn.setForeground(Color.WHITE);
                okBtn.addActionListener(e2 -> successDialog.dispose());
                JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
                btnPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                btnPanel2.add(okBtn);
                successDialog.add(successLabel, BorderLayout.CENTER);
                successDialog.add(btnPanel2, BorderLayout.SOUTH);
                successDialog.pack();
                successDialog.setLocationRelativeTo(dialog);
                successDialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                StyledErrorDialog(dialog, "Error", "Invalid or missing value: " + ex.getMessage());
            }
            saveBtn.setEnabled(false); // Disable again after saving

        });

        // Delete action
        deleteBtn.addActionListener(e -> {
            int row = employeeTable.getSelectedRow();
            if (row < 0) return;
            int empNum = (int) tableModel.getValueAt(row, 0);
            Employee emp = employeeService.findEmployeeByNumber(empNum);
            if (emp == null) {
                StyledErrorDialog(dialog, "Error", "Employee not found.");
                return;
            }

            // Styled confirm dialog
            JDialog confirmDialog = new JDialog(dialog, "Confirm Deletion", Dialog.ModalityType.APPLICATION_MODAL);
            confirmDialog.setLayout(new BorderLayout(10, 10));
            confirmDialog.setSize(340, 150);
            confirmDialog.setLocationRelativeTo(dialog);

            // Message area (centered)
            JLabel messageLabel = new JLabel(
                "<html><div style='text-align: center;'>Are you sure you want to delete<br>" + emp.getFullName() + "?</div></html>",
                SwingConstants.CENTER
            );
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
            confirmDialog.add(messageLabel, BorderLayout.CENTER);


            // Buttons
            JButton yesBtn = new JButton("Yes");
            JButton noBtn = new JButton("No");
            styleButtonSmall(yesBtn);
            styleButtonSmall(noBtn);

            yesBtn.addActionListener(e2 -> {
            try {
                CSVWriter.deleteEmployeeFromCSV(empNum);
                employees.remove(emp);
                tableModel.removeRow(row);

                // Custom success dialog (same as your "employee added" one)
                JDialog successDialog = new JDialog(dialog, "Success", true);
                successDialog.setLayout(new BorderLayout(10, 10));

                JLabel successLabel = new JLabel("Employee deleted successfully!");
                successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                successLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

                JButton okBtn = new JButton("OK");
                styleButtonSmall(okBtn);
                okBtn.setBackground(new Color(0x1c3680));
                okBtn.setForeground(Color.WHITE);
                okBtn.addActionListener(e3 -> successDialog.dispose());

                JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
                btnPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                btnPanel2.add(okBtn);

                successDialog.add(successLabel, BorderLayout.CENTER);
                successDialog.add(btnPanel2, BorderLayout.SOUTH);
                successDialog.pack();
                successDialog.setLocationRelativeTo(dialog);
                successDialog.setVisible(true);

            } catch (Exception ex) {
                StyledErrorDialog(dialog, "Error", "Failed to delete employee: " + ex.getMessage());
            }

            confirmDialog.dispose();
        });


            noBtn.addActionListener(e2 -> confirmDialog.dispose());

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10, 10));
            btnPanel.add(yesBtn);
            btnPanel.add(noBtn);

            confirmDialog.add(btnPanel, BorderLayout.SOUTH);
            confirmDialog.setVisible(true);
        });

        dialog.setVisible(true);

        // When dialog closes, clear static reference
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                updateDeleteTableModel = null;
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                updateDeleteTableModel = null;
            }
        });
    }

    // Helper to reload employees from CSV and refresh the table
    private void reloadEmployeesAndRefreshTable(DefaultTableModel tableModel) {
        try {
            employees.clear();
            employees.addAll(com.compprog1282025.util.CSVReader.readEmployeesFromCSV("data/employees.csv"));
            // Clear and repopulate the table
            tableModel.setRowCount(0);
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getGovernmentID().getSss(),
                    emp.getGovernmentID().getPhilhealth(),
                    emp.getGovernmentID().getTin(),
                    emp.getGovernmentID().getPagibig()
                });
            }
        } catch (Exception ex) {
            StyledErrorDialog(this, "Error", "Failed to reload employees: " + ex.getMessage());
        }
    }
    
    private void showViewAddEmployeeList(Component parent) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "View/Add Employee", true);
            dialog.setSize(1000, 600);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            // Table setup (no Status column)
            String[] columns = {
                "Employee #", "Last Name", "First Name",
                "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #"
            };
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable employeeTable = new JTable(tableModel);
            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getEmployeeNumber(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    emp.getGovernmentID().getSss(),
                    emp.getGovernmentID().getPhilhealth(),
                    emp.getGovernmentID().getTin(),
                    emp.getGovernmentID().getPagibig()
                });
            }
            // Modern table design (copied from View/Add Employee)
            employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            employeeTable.setRowHeight(32);
            employeeTable.setShowGrid(false);
            employeeTable.setIntercellSpacing(new Dimension(0, 0));
            employeeTable.setSelectionBackground(new Color(96, 96, 96)); // the black-grey color highlight for selection
            employeeTable.setSelectionForeground(Color.WHITE);
            employeeTable.setFillsViewportHeight(true);
            employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
            employeeTable.getTableHeader().setBackground(new Color(0x1c3680));
            employeeTable.getTableHeader().setForeground(Color.WHITE);
            employeeTable.getTableHeader().setReorderingAllowed(false);
            employeeTable.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JScrollPane scrollPane = new JScrollPane(employeeTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            dialog.add(scrollPane, BorderLayout.CENTER);

            // Button panel at the bottom
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));

            // View Employee button
            JButton viewBtn = new JButton("View Employee");
            styleButtonSmall(viewBtn);
            viewBtn.setPreferredSize(new Dimension(160, 38));
            viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            viewBtn.setEnabled(false);
            viewBtn.setBackground(new java.awt.Color(0x1c3680));
            viewBtn.setForeground(java.awt.Color.WHITE);
            btnPanel.add(viewBtn);

            // New Employee button
            JButton addBtn = new JButton("New Employee");
            styleButtonSmall(addBtn);
            addBtn.setPreferredSize(new Dimension(180, 38));
            addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            addBtn.setBackground(new java.awt.Color(0x1c3680));
            addBtn.setForeground(java.awt.Color.WHITE);
            btnPanel.add(addBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            // Enable view button only when a row is selected
            employeeTable.getSelectionModel().addListSelectionListener(e -> {
                int row = employeeTable.getSelectedRow();
                viewBtn.setEnabled(row >= 0);
            });

            // View Employee logic
            viewBtn.addActionListener(e -> {
                int row = employeeTable.getSelectedRow();
                if (row == -1) {
                    StyledErrorDialog(dialog, "Error", "Select an employee from the table.");
                    return;
                }
                int empNum = (int) tableModel.getValueAt(row, 0);
                Employee emp = employeeService.findEmployeeByNumber(empNum);
                if (emp == null) {
                    StyledErrorDialog(dialog, "Error", "Employee not found.");
                    return;
                }
                showDetailedEmployeeDialog(dialog, emp);
            });

        // New Employee logic: open a single form dialog
            addBtn.addActionListener(e -> {
                JDialog addDialog = new JDialog(dialog, "New Employee", true);
                addDialog.setSize(500, 650);
                addDialog.setLocationRelativeTo(dialog);
                addDialog.setLayout(new BorderLayout(10, 10));
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(0, 0, 8, 0); // vertical gap between rows
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.NONE;

                Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
                // Fields
                JLabel lblEmpNum = new JLabel("Employee Number:"); lblEmpNum.setFont(labelFont); lblEmpNum.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfEmpNum = new JTextField();
                JLabel lblFirstName = new JLabel("First Name:"); lblFirstName.setFont(labelFont); lblFirstName.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfFirstName = new JTextField();
                JLabel lblLastName = new JLabel("Last Name:"); lblLastName.setFont(labelFont); lblLastName.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfLastName = new JTextField();
                JLabel lblBirthday = new JLabel("Birthday:"); lblBirthday.setFont(labelFont); lblBirthday.setHorizontalAlignment(SwingConstants.LEFT);
                String[] months = java.util.Arrays.stream(java.time.Month.values())
                    .map(m -> m.toString().substring(0,1) + m.toString().substring(1).toLowerCase().substring(0,2)).toArray(String[]::new);
                JComboBox<String> cbMonth = new JComboBox<>(months);
                Integer[] days = java.util.stream.IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new);
                JComboBox<Integer> cbDay = new JComboBox<>(days);
                int currentYear = java.time.LocalDate.now().getYear();
                Integer[] years = java.util.stream.IntStream.rangeClosed(currentYear-70, currentYear-16).boxed().sorted((a,b)->b-a).toArray(Integer[]::new);
                JComboBox<Integer> cbYear = new JComboBox<>(years);
                JPanel birthdayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                birthdayPanel.add(cbMonth);
                birthdayPanel.add(cbDay);
                birthdayPanel.add(cbYear);
                JLabel lblAddress = new JLabel("Address:"); lblAddress.setFont(labelFont); lblAddress.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfAddress = new JTextField();
                JLabel lblPhone = new JLabel("Phone #:"); lblPhone.setFont(labelFont); lblPhone.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfPhone = new JTextField();
                JLabel lblSSS = new JLabel("SSS #:"); lblSSS.setFont(labelFont); lblSSS.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfSSS = new JTextField();
                JLabel lblPhil = new JLabel("PhilHealth #:"); lblPhil.setFont(labelFont); lblPhil.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfPhil = new JTextField();
                JLabel lblTIN = new JLabel("TIN #:"); lblTIN.setFont(labelFont); lblTIN.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfTIN = new JTextField();
                JLabel lblPagibig = new JLabel("Pag-ibig #:"); lblPagibig.setFont(labelFont); lblPagibig.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfPagibig = new JTextField();
                JLabel lblStatus = new JLabel("Status:"); lblStatus.setFont(labelFont); lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfStatus = new JTextField();
                JLabel lblPosition = new JLabel("Position:"); lblPosition.setFont(labelFont); lblPosition.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfPosition = new JTextField();
                JLabel lblSupervisor = new JLabel("Supervisor:"); lblSupervisor.setFont(labelFont); lblSupervisor.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfSupervisor = new JTextField();
                JLabel lblBasic = new JLabel("Basic Salary:"); lblBasic.setFont(labelFont); lblBasic.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfBasic = new JTextField();
                JLabel lblRice = new JLabel("Rice Subsidy:"); lblRice.setFont(labelFont); lblRice.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfRice = new JTextField();
                JLabel lblPhoneAllow = new JLabel("Phone Allowance:"); lblPhoneAllow.setFont(labelFont); lblPhoneAllow.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfPhoneAllow = new JTextField();
                JLabel lblClothing = new JLabel("Clothing Allowance:"); lblClothing.setFont(labelFont); lblClothing.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfClothing = new JTextField();
                JLabel lblSemi = new JLabel("Semi-Monthly:"); lblSemi.setFont(labelFont); lblSemi.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfSemi = new JTextField();
                JLabel lblHourly = new JLabel("Hourly Rate:"); lblHourly.setFont(labelFont); lblHourly.setHorizontalAlignment(SwingConstants.LEFT);
                JTextField tfHourly = new JTextField();

                // Add fields to form using GridBagLayout
                int row = 0;
                Object[][] labelFieldPairs = {
                    {lblEmpNum, tfEmpNum},
                    {lblFirstName, tfFirstName},
                    {lblLastName, tfLastName},
                    {lblBirthday, birthdayPanel},
                    {lblAddress, tfAddress},
                    {lblPhone, tfPhone},
                    {lblSSS, tfSSS},
                    {lblPhil, tfPhil},
                    {lblTIN, tfTIN},
                    {lblPagibig, tfPagibig},
                    {lblStatus, tfStatus},
                    {lblPosition, tfPosition},
                    {lblSupervisor, tfSupervisor},
                    {lblBasic, tfBasic},
                    {lblRice, tfRice},
                    {lblPhoneAllow, tfPhoneAllow},
                    {lblClothing, tfClothing},
                    {lblSemi, tfSemi},
                    {lblHourly, tfHourly}
                };
                for (Object[] pair : labelFieldPairs) {
                    gbc.gridx = 0;
                    gbc.gridy = row;
                    gbc.weightx = 0;
                    gbc.fill = GridBagConstraints.NONE;
                    formPanel.add((JLabel)pair[0], gbc);
                    gbc.gridx = 1;
                    gbc.weightx = 1;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    formPanel.add((Component)pair[1], gbc);
                    row++;
                }

                // Make textfield border thicker and font larger, not bold
                Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 16);
                javax.swing.border.Border thinGrayBorder = BorderFactory.createLineBorder(new java.awt.Color(176, 176, 176), 1);
                JTextField[] allFields = {tfEmpNum, tfFirstName, tfLastName, tfAddress, tfPhone, tfSSS, tfPhil, tfTIN, tfPagibig, tfStatus, tfPosition, tfSupervisor, tfBasic, tfRice, tfPhoneAllow, tfClothing, tfSemi, tfHourly};
                for (JTextField field : allFields) {
                    field.setFont(textFieldFont);
                    field.setBorder(thinGrayBorder);
                }
                // Fix text clipping by increasing text field size
                Dimension fieldSize = new Dimension(0, 25);
                for (JTextField field : allFields) {
                    field.setPreferredSize(fieldSize);
                }
                JScrollPane addEmpScrollPane = new JScrollPane(formPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                addEmpScrollPane.getVerticalScrollBar().setUnitIncrement(16);
                addEmpScrollPane.setBorder(null); // Remove default border for a cleaner look
                addDialog.add(addEmpScrollPane, BorderLayout.CENTER);
                // Save/Cancel buttons
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0,7, 0)); // Add extra bottom margin (15px more)
                JButton saveBtn = new JButton("Save");
                JButton cancelBtn = new JButton("Cancel");
                saveBtn.setBackground(new java.awt.Color(0x1c3680));
                saveBtn.setForeground(java.awt.Color.WHITE);
                cancelBtn.setBackground(new java.awt.Color(0x1c3680));
                cancelBtn.setForeground(java.awt.Color.WHITE);
                saveBtn.setFocusPainted(false);
                cancelBtn.setFocusPainted(false);
                saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                saveBtn.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
                cancelBtn.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
                buttonPanel.add(saveBtn);
                buttonPanel.add(cancelBtn);
                addDialog.add(buttonPanel, BorderLayout.SOUTH);
                // Save logic
                saveBtn.addActionListener(ev -> {
                    // Check all required fields before proceeding
                    if (tfEmpNum.getText().trim().isEmpty() ||
                        tfFirstName.getText().trim().isEmpty() ||
                        tfLastName.getText().trim().isEmpty() ||
                        tfAddress.getText().trim().isEmpty() ||
                        tfPhone.getText().trim().isEmpty() ||
                        tfSSS.getText().trim().isEmpty() ||
                        tfPhil.getText().trim().isEmpty() ||
                        tfTIN.getText().trim().isEmpty() ||
                        tfPagibig.getText().trim().isEmpty() ||
                        tfStatus.getText().trim().isEmpty() ||
                        tfSupervisor.getText().trim().isEmpty() ||
                        tfPosition.getText().trim().isEmpty() ||
                        tfBasic.getText().trim().isEmpty() ||
                        tfRice.getText().trim().isEmpty() ||
                        tfPhoneAllow.getText().trim().isEmpty() ||
                        tfClothing.getText().trim().isEmpty() ||
                        tfSemi.getText().trim().isEmpty() ||
                        tfHourly.getText().trim().isEmpty()) {
                        StyledErrorDialog(addDialog, "Error", "Please fill in all fields.");
                        return;
                    }
                    try {
                        int empNum = Integer.parseInt(tfEmpNum.getText().trim());
                        String firstName = tfFirstName.getText().trim();
                        String lastName = tfLastName.getText().trim();
                        int month = cbMonth.getSelectedIndex() + 1;
                        int day = (Integer) cbDay.getSelectedItem();
                        int year = (Integer) cbYear.getSelectedItem();
                        LocalDate birthday = LocalDate.of(year, month, day);
                        String address = tfAddress.getText().trim();
                        String phone = tfPhone.getText().trim();
                        String sss = tfSSS.getText().trim();
                        String philhealth = tfPhil.getText().trim();
                        String tin = tfTIN.getText().trim();
                        String pagibig = tfPagibig.getText().trim();
                        String status = tfStatus.getText().trim();
                        String posTitle = tfPosition.getText().trim();
                        String supervisorName = tfSupervisor.getText().trim();
                        Employee supervisor = null;
                        String fullName = null;
                            // Prevent assigning the employee as their own supervisor
                            if (!supervisorName.equalsIgnoreCase(fullName)) {
                                supervisor = employees.stream()
                                    .filter(e2 -> e2.getFullName().equalsIgnoreCase(supervisorName))
                                    .findFirst()
                                    .orElseGet(() -> {
                                        String[] nameParts = supervisorName.split(" ", 2);
                                        String supFirst = nameParts.length > 0 ? nameParts[0].trim() : "";
                                        String supLast = nameParts.length > 1 ? nameParts[1].trim() : "";
                                        return new Employee(supFirst, supLast);
                                    });
                            }


                        // FIXED: Correct field mapping
                        double basic = Double.parseDouble(tfBasic.getText().trim());
                        double rice = Double.parseDouble(tfRice.getText().trim());
                        double phoneAllow = Double.parseDouble(tfPhoneAllow.getText().trim());
                        double clothing = Double.parseDouble(tfClothing.getText().trim());
                        double semiGross = Double.parseDouble(tfSemi.getText().trim());
                        double hourly = Double.parseDouble(tfHourly.getText().trim());

                        Employee emp = new Employee(
                                empNum, firstName, lastName, birthday,
                                new ContactInfo(address, phone),
                                new GovernmentID(sss, philhealth, tin, pagibig),
                                new Position(posTitle, supervisor),
                                new Compensation(basic, rice, phoneAllow, clothing, semiGross, hourly),
                                status, "");

                        // FIXED: Add to list first, then write to CSV
                        employees.add(emp);
                        com.compprog1282025.util.CSVWriter.writeAllEmployeesToCSV(employees);

                        tableModel.addRow(new Object[]{
                            emp.getEmployeeNumber(),
                            emp.getLastName(),
                            emp.getFirstName(),
                            emp.getGovernmentID().getSss(),
                            emp.getGovernmentID().getPhilhealth(),
                            emp.getGovernmentID().getTin(),
                            emp.getGovernmentID().getPagibig()
                        });
                        // Also update Update/Delete Employee table if open
                        if (updateDeleteTableModel != null) {
                            updateDeleteTableModel.addRow(new Object[]{
                                emp.getEmployeeNumber(),
                                emp.getLastName(),
                                emp.getFirstName(),
                                emp.getGovernmentID().getSss(),
                                emp.getGovernmentID().getPhilhealth(),
                                emp.getGovernmentID().getTin(),
                                emp.getGovernmentID().getPagibig()
                            });
                        }
                        addDialog.dispose();
                        JDialog successDialog = new JDialog(dialog, "Success", true);
                        successDialog.setLayout(new BorderLayout(10, 10));
                        JLabel successLabel = new JLabel("New employee added successfully!");
                        successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        successLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
                        JButton okBtn = new JButton("OK");
                        styleButtonSmall(okBtn);
                        okBtn.setBackground(new Color(0x1c3680));
                        okBtn.setForeground(Color.WHITE);
                        okBtn.addActionListener(e2 -> successDialog.dispose());
                        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        btnPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                        btnPanel2.add(okBtn);
                        successDialog.add(successLabel, BorderLayout.CENTER);
                        successDialog.add(btnPanel2, BorderLayout.SOUTH);
                        successDialog.pack();
                        successDialog.setLocationRelativeTo(dialog);
                        successDialog.setVisible(true);
                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        if (msg == null || msg.trim().isEmpty()) {
                            msg = "An unexpected error occurred. Please check your input.";
                        }
                        StyledErrorDialog(addDialog, "Error", "Failed to add employee: " + msg);
                    }
                });
                cancelBtn.addActionListener(ev -> addDialog.dispose());
                addDialog.setVisible(true);
            });

            dialog.setVisible(true);
        }

        private void salaryCalculationMenu(Component parent) { // THIS IS DONE
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Salary Calculation", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        String[] options = {
            "Gross Salary for ALL employees",
            "Net Salary for ALL employees",
            "Gross Salary for specific employee",
            "Net Salary for specific employee",
            "Close"
        };

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        menuPanel.add(Box.createVerticalGlue());

        for (String option : options) {
            JButton button = new JButton(option);
            styleButton(button);
            button.setPreferredSize(new Dimension(300, 40));
            button.setMaximumSize(new Dimension(300, 40));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            menuPanel.add(Box.createVerticalStrut(10));
            menuPanel.add(button);

            button.addActionListener(e -> {
                switch (option) {
                    case "Gross Salary for ALL employees" -> {
                        YearMonth ym = promptYearMonth(dialog);
                        if (ym == null) return;

                        String[] columnNames = {"Employee Number", "Full Name", "Gross Salary"};
                        Object[][] data = new Object[employees.size()][3];
                        int i = 0;
                        for (Employee emp : employees) {
                            double gross = payrollService.calculateMonthlySalary(emp, ym);
                            data[i][0] = emp.getEmployeeNumber();
                            data[i][1] = emp.getFullName();
                            data[i][2] = String.format("%.2f", gross);
                            i++;
                        }

                        JTable table = new JTable(data, columnNames);
                        JScrollPane scrollPane = new JScrollPane(table);
                        table.setFillsViewportHeight(true);

                        JDialog tableDialog = new JDialog(dialog, "Gross Salaries for " + ym, true);
                        tableDialog.setSize(600, 400);
                        tableDialog.setLocationRelativeTo(dialog);
                        tableDialog.add(scrollPane);
                        tableDialog.setVisible(true);
                    }

                    case "Net Salary for ALL employees" -> {
                        YearMonth ym = promptYearMonth(dialog);
                        if (ym == null) return;

                        String[] columnNames = {"Employee Number", "Full Name", "Net Salary"};
                        Object[][] data = new Object[employees.size()][3];
                        int i = 0;
                        for (Employee emp : employees) {
                            double net = payrollService.calculateNetSalary(emp, ym);
                            data[i][0] = emp.getEmployeeNumber();
                            data[i][1] = emp.getFullName();
                            data[i][2] = String.format("%.2f", net);
                            i++;
                        }

                        JTable table = new JTable(data, columnNames);
                        JScrollPane scrollPane = new JScrollPane(table);
                        table.setFillsViewportHeight(true);

                        JDialog tableDialog = new JDialog(dialog, "Net Salaries for " + ym, true);
                        tableDialog.setSize(600, 400);
                        tableDialog.setLocationRelativeTo(dialog);
                        tableDialog.add(scrollPane);
                        tableDialog.setVisible(true);
                    }

                    case "Gross Salary for specific employee" -> {
                        Employee emp = promptEmployeeNumber(dialog);
                        if (emp == null) return;
                        YearMonth ym = promptYearMonth(dialog);
                        if (ym == null) return;
                        double gross = payrollService.calculateMonthlySalary(emp, ym);
                        StyledInfoDialog(dialog, "Gross Salary", String.format("Gross Salary for %s in %s: %.2f", emp.getFullName(), ym, gross));
                    }

                    case "Net Salary for specific employee" -> {
                        Employee emp = promptEmployeeNumber(dialog);
                        if (emp == null) return;
                        YearMonth ym = promptYearMonth(dialog);
                        if (ym == null) return;
                        double net = payrollService.calculateNetSalary(emp, ym);
                        StyledInfoDialog(dialog, "Net Salary", String.format("Net Salary for %s in %s: %.2f", emp.getFullName(), ym, net));
                    }

                    case "Close" -> dialog.dispose();
                }
            });
        }

        menuPanel.add(Box.createVerticalGlue());
        dialog.add(menuPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void exportPayrollReport(Component parent) { // Find way to edit view or design
        YearMonth ym = promptYearMonth(parent);
        if (ym == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File to Save Payroll Report");
        fileChooser.setSelectedFile(new File("payroll_report_" + ym + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                payrollService.exportPayrollReportToCSV(employees, ym, fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Payroll report exported to:\n" + fileToSave.getAbsolutePath());
            } catch (Exception e) {
                StyledErrorDialog(this, "Error", "Error exporting CSV: " + e.getMessage());
            }
        }
    }
    

    private YearMonth promptYearMonth(Component parent) {
        String input = StyledInputDialog(parent, "Enter Month and Year", "Enter month and year (yyyy-MM):");
        if (input == null) return null;
        try {
            return YearMonth.parse(input.trim());
        } catch (Exception e) {
            StyledErrorDialog(parent, "Error", "Invalid format. Use yyyy-MM.");
            return null;
        }
    }
    private Employee promptEmployeeNumber(Component parent) {
        String input = StyledInputDialog(parent, "Enter Employee Number", "Enter employee number:");
        if (input == null) return null; // User cancelled

        try {
            int empNum = Integer.parseInt(input.trim());
            Employee emp = employeeService.findEmployeeByNumber(empNum);
            if (emp == null) {
                StyledErrorDialog(parent, "Error", "Employee not found.");
            }
            return emp;
        } catch (NumberFormatException e) {
            StyledErrorDialog(parent, "Error", "Invalid employee number.");
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
    
    private String StyledInputDialog(Component parent, String title, String message) {
        JDialog inputDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(parent), title, ModalityType.APPLICATION_MODAL);
        inputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inputDialog.setSize(380, 170);
        inputDialog.setResizable(false);
        inputDialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        mainPanel.setBackground(new Color(250, 252, 255));

        JLabel promptLabel = new JLabel(message);
        promptLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(325, 28));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        styleButtonSmall(okButton);
        styleButtonSmall(cancelButton);
        okButton.setPreferredSize(new Dimension(100, 36));
        cancelButton.setPreferredSize(new Dimension(100, 36));
        okButton.setMaximumSize(new Dimension(100, 36));
        cancelButton.setMaximumSize(new Dimension(100, 36));

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalGlue());

        final String[] result = new String[1];
        result[0] = null;

        okButton.addActionListener(e -> {
            result[0] = inputField.getText().trim();
            inputDialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result[0] = null;
            inputDialog.dispose();
        });

        mainPanel.add(promptLabel);
        mainPanel.add(inputField);
        mainPanel.add(buttonPanel);

        inputDialog.setContentPane(mainPanel);
        inputDialog.getRootPane().setDefaultButton(okButton);
        inputDialog.setVisible(true);

        return result[0];
    }
    
    private void StyledErrorDialog(Component parent, String title, String message) {
        JDialog errorDialog = new JDialog((Window) SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        errorDialog.setLayout(new BorderLayout(10, 10));
        errorDialog.setSize(340, 150);
        errorDialog.setLocationRelativeTo(parent);
        errorDialog.getContentPane().setBackground(new Color(245, 248, 250));

        // Icon and message panel
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 18));
        contentPanel.setOpaque(false);
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        contentPanel.add(iconLabel);
        contentPanel.add(messageLabel);

        // OK button
        JButton okButton = new JButton("OK");
        styleButtonSmall(okButton);
        okButton.setPreferredSize(new Dimension(80, 32));
        okButton.setMaximumSize(new Dimension(80, 32));
        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); 
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        errorDialog.add(contentPanel, BorderLayout.CENTER);
        errorDialog.add(buttonPanel, BorderLayout.SOUTH);
        errorDialog.setVisible(true);
    }
    

    private void StyledInfoDialog(Component parent, String title, String message) {
        JDialog dialog = new JDialog((Window) SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(parent);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setBackground(dialog.getBackground());
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton okButton = new JButton("OK");
        styleButtonSmall(okButton);
        okButton.setPreferredSize(new Dimension(60, 30));

        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); 
        buttonPanel.add(okButton);

        dialog.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}