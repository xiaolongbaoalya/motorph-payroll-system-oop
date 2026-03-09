package com.compprog1282025.ui.swing;

import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.ui.swing.modules.FinanceGeneratePayslipsPanel;
import com.compprog1282025.ui.swing.modules.FinancePayrollHistoryPanel;
import com.compprog1282025.ui.swing.modules.HrAddEmployeePanel;
import com.compprog1282025.ui.swing.modules.HrEmployeeDetailsPanel;
import com.compprog1282025.ui.swing.modules.HrTeamAttendancePanel;
import com.compprog1282025.ui.swing.modules.HrTeamRequestsPanel;
import com.compprog1282025.ui.swing.modules.PersonalAttendancePanel;
import com.compprog1282025.ui.swing.modules.PersonalPayslipPanel;
import com.compprog1282025.ui.swing.modules.PersonalProfilePanel;
import com.compprog1282025.ui.swing.modules.PersonalRequestsPanel;
import com.compprog1282025.ui.swing.modules.RefreshablePanel;
import com.compprog1282025.ui.swing.modules.SwingModuleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardFrame extends JFrame {
    private static final Color SIDEBAR_BG = new Color(28, 43, 69);
    private static final Color CONTENT_BG = new Color(242, 244, 247);
    private static final Color SECTION_BG = new Color(224, 226, 230);
    private static final Color SECTION_BORDER = new Color(196, 200, 206);
    private static final Color MENU_TEXT = new Color(131, 159, 199);

    private static final int LEFT_WIDTH = 250;

    private final AppContext appContext;
    private final Session session;
    private final EffectiveRole role;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final Map<String, RefreshablePanel> panels = new LinkedHashMap<>();
    private final List<CollapsibleSection> sidebarSections = new ArrayList<>();

    public DashboardFrame(AppContext appContext, Session session) {
        this.appContext = appContext;
        this.session = session;
        this.role = RoleResolver.resolve(session);

        setTitle("MotorPH Payroll System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(980, 680));
        setLocationRelativeTo(null);

        cardPanel.setBackground(CONTENT_BG);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(cardPanel, BorderLayout.CENTER);

        buildCards();
        setContentPane(root);
        openPanel("my_attendance");
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(216, 220, 226)));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(SIDEBAR_BG);
        brand.setPreferredSize(new Dimension(LEFT_WIDTH, 88));
        JLabel logo = new JLabel(loadLogo());
        logo.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 10));
        brand.add(logo, BorderLayout.CENTER);

        String name = session.getUser() != null && session.getUser().getEmployee() != null
                ? session.getUser().getEmployee().getFirstName()
                : "User";

        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setBackground(CONTENT_BG);
        contentHeader.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome, " + name + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(new Color(34, 47, 66));

        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        date.setForeground(new Color(100, 112, 129));

        left.add(welcome);
        left.add(Box.createVerticalStrut(4));
        left.add(date);

        JPanel right = new JPanel();
        right.setOpaque(false);

        JButton timeIn = createActionButton("Time In", new Color(24, 183, 130), 88);
        timeIn.addActionListener(e -> {
            try {
                appContext.getAttendanceService().timeInAttendance(session, session.getUser().getEmployee());
                refreshVisiblePanel();
                SwingModuleUtil.info(this, "Success", "Time In Successfully");
            } catch (Exception ex) {
                SwingModuleUtil.error(this, "Error", ex.getMessage());
            }
        });

        JButton timeOut = createActionButton("Time Out", new Color(246, 72, 72), 100);
        timeOut.addActionListener(e -> {
            try {
                appContext.getAttendanceService().timeOutAttendance(session, session.getUser().getEmployee());
                refreshVisiblePanel();
                SwingModuleUtil.info(this, "Success", "Time Out Successfully");
            } catch (Exception ex) {
                SwingModuleUtil.error(this, "Error", ex.getMessage());
            }
        });

        right.add(timeIn);
        right.add(Box.createHorizontalStrut(10));
        right.add(timeOut);

        contentHeader.add(left, BorderLayout.WEST);
        contentHeader.add(right, BorderLayout.EAST);

        header.add(brand, BorderLayout.WEST);
        header.add(contentHeader, BorderLayout.CENTER);
        return header;
    }

    private JScrollPane buildSidebar() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(6, 14, 8, 14));
        menu.setBackground(SIDEBAR_BG);

        CollapsibleSection personal = createSection("Personal Dashboard");
        personal.addButton(menuButton("My Attendance", "my_attendance"));
        personal.addButton(menuButton("My Payslip", "my_payslip"));
        personal.addButton(menuButton("My Requests", "my_requests"));
        personal.addButton(menuButton("About Me", "about_me"));
        menu.add(personal);

        if (role == EffectiveRole.HR || role == EffectiveRole.ADMIN) {
            CollapsibleSection hr = createSection("HR Dashboard");
            hr.addButton(menuButton("Team Attendance", "hr_team_attendance"));
            hr.addButton(menuButton("Team Requests", "hr_team_requests"));
            hr.addButton(menuButton("Add Employee", "hr_add_employee"));
            hr.addButton(menuButton("Employee Details", "hr_employee_details"));
            menu.add(hr);
        }

        if (role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN) {
            CollapsibleSection finance = createSection("Finance Dashboard");
            finance.addButton(menuButton("Generate Payslips", "fin_generate_payslip"));
            finance.addButton(menuButton("Employee Payroll History", "fin_payroll_history"));
            menu.add(finance);
        }

        if (!sidebarSections.isEmpty()) {
            expandOnly(sidebarSections.get(0));
        }

        menu.add(Box.createVerticalGlue());
        JButton logout = createActionButton("Logout", new Color(244, 65, 65), 220);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> {
            appContext.clearCurrentSession();
            dispose();
            new LoginFrame(appContext).setVisible(true);
        });
        menu.add(Box.createVerticalStrut(8));
        menu.add(logout);

        JScrollPane scroll = new JScrollPane(menu);
        scroll.setPreferredSize(new Dimension(LEFT_WIDTH, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private JButton createActionButton(String text, Color bg, int width) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(width, 40));
        return button;
    }

    private ImageIcon loadLogo() {
        URL url = getClass().getResource("/com/compprog1282025/assets/motorph_logo_with_white_outline.png");
        if (url == null) {
            return new ImageIcon();
        }
        Image img = new ImageIcon(url).getImage();
        int srcW = img.getWidth(null);
        int srcH = img.getHeight(null);
        if (srcW <= 0 || srcH <= 0) {
            return new ImageIcon(img);
        }
        int maxW = 200;
        int maxH = 72;
        double ratio = Math.min((double) maxW / srcW, (double) maxH / srcH);
        int w = Math.max(1, (int) Math.round(srcW * ratio));
        int h = Math.max(1, (int) Math.round(srcH * ratio));
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private CollapsibleSection createSection(String title) {
        CollapsibleSection section = new CollapsibleSection(title);
        section.setOnExpand(() -> expandOnly(section));
        sidebarSections.add(section);
        return section;
    }

    private void expandOnly(CollapsibleSection target) {
        for (CollapsibleSection section : sidebarSections) {
            section.setExpanded(section == target);
        }
    }

    private void buildCards() {
        addCard("my_attendance", new PersonalAttendancePanel(appContext.getAttendanceService(), session));
        addCard("my_payslip", new PersonalPayslipPanel(appContext.getFinanceService(), session, new com.compprog1282025.service.PayslipPdfService()));
        addCard("my_requests", new PersonalRequestsPanel(appContext.getRequestService(), session));
        addCard("about_me", new PersonalProfilePanel(session));

        if (role == EffectiveRole.HR || role == EffectiveRole.ADMIN) {
            addCard("hr_team_attendance", new HrTeamAttendancePanel(appContext.getAttendanceService(), appContext.getEmployeeService()));
            addCard("hr_team_requests", new HrTeamRequestsPanel(appContext.getRequestService(), appContext.getEmployeeService(), session));
            addCard("hr_add_employee", new HrAddEmployeePanel(appContext.getEmployeeService(), session));
            addCard("hr_employee_details", new HrEmployeeDetailsPanel(appContext.getEmployeeService(), appContext.getAuthService(), session));
        }

        if (role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN) {
            addCard("fin_generate_payslip", new FinanceGeneratePayslipsPanel(appContext.getEmployeeService(), appContext.getFinanceService(), new com.compprog1282025.service.PayslipPdfService()));
            addCard("fin_payroll_history", new FinancePayrollHistoryPanel(appContext.getEmployeeService(), appContext.getFinanceService()));
        }
    }

    private void addCard(String key, RefreshablePanel panel) {
        panels.put(key, panel);
        JPanel p = (JPanel) panel;
        p.setBackground(CONTENT_BG);
        cardPanel.add(p, key);
    }

    private void openPanel(String key) {
        RefreshablePanel panel = panels.get(key);
        if (panel != null) {
            panel.refreshData();
        }
        cardLayout.show(cardPanel, key);
    }

    private void refreshVisiblePanel() {
        for (RefreshablePanel panel : panels.values()) {
            if (((JPanel) panel).isShowing()) {
                panel.refreshData();
                return;
            }
        }
    }

    private JButton menuButton(String text, String panelKey) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 8));
        button.setForeground(MENU_TEXT);
        button.setBackground(SIDEBAR_BG);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.addActionListener(e -> openPanel(panelKey));
        return button;
    }

    private static final class CollapsibleSection extends JPanel {
        private final String title;
        private final JButton headerButton;
        private final JPanel bodyPanel;
        private Runnable onExpand;
        private boolean expanded;

        private CollapsibleSection(String title) {
            this.title = title;
            setLayout(new BorderLayout());
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            setBackground(SIDEBAR_BG);

            headerButton = new JButton();
            headerButton.setHorizontalAlignment(SwingConstants.LEFT);
            headerButton.setFocusPainted(false);
            headerButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECTION_BORDER),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            headerButton.setBackground(SECTION_BG);
            headerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            headerButton.addActionListener(e -> toggle());

            bodyPanel = new JPanel();
            bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
            bodyPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 0));
            bodyPanel.setBackground(SIDEBAR_BG);

            add(headerButton, BorderLayout.NORTH);
            add(bodyPanel, BorderLayout.CENTER);
            setExpanded(false);
        }

        private void setOnExpand(Runnable onExpand) {
            this.onExpand = onExpand;
        }

        private void addButton(JButton button) {
            bodyPanel.add(button);
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
            bodyPanel.setVisible(expanded);
            headerButton.setText((expanded ? "v  " : ">  ") + title);
            revalidate();
            repaint();
        }

        private void toggle() {
            setExpanded(!expanded);
            if (expanded && onExpand != null) {
                onExpand.run();
            }
        }
    }
}


