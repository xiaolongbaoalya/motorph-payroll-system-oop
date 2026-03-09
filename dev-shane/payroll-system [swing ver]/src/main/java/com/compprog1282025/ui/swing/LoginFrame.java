package com.compprog1282025.ui.swing;

import com.compprog1282025.model.user.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;

public class LoginFrame extends JFrame {
    private static final Color PAGE_BG = new Color(237, 238, 241);
    private static final Color CARD_BG = new Color(249, 249, 250);
    private static final Color TEXT_DARK = new Color(28, 38, 56);
    private static final Color INPUT_BORDER = new Color(205, 211, 218);
    private static final Color BUTTON_BG = new Color(12, 24, 48);
    private static final Color LINK_BLUE = new Color(70, 169, 233);

    private static final int FORM_WIDTH = 312;
    private static final int FIELD_HEIGHT = 40;

    private final AppContext appContext;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginFrame(AppContext appContext) {
        this.appContext = appContext;
        this.usernameField = new JTextField();
        this.passwordField = new JPasswordField();

        setTitle("MotorPH Payroll System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(PAGE_BG);
        page.add(buildLoginCard());
        setContentPane(page);
    }

    private JPanel buildLoginCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(380, 560));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 231, 236), 1, true),
                BorderFactory.createEmptyBorder(30, 34, 30, 34)
        ));

        styleInput(usernameField);
        styleInput(passwordField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        JLabel logoLabel = new JLabel(loadScaledLogo(220, 95));
        card.add(logoLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        JLabel titleLabel = new JLabel("Login to your account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        card.add(titleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 22, 0);
        card.add(buildAlignedFormPanel(), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel forgotLabel = new JLabel("Forgot password?");
        forgotLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        forgotLabel.setForeground(LINK_BLUE);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Please contact HR/Admin to reset your password.",
                        "Password Assistance",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        card.add(forgotLabel, gbc);

        return card;
    }

    private JPanel buildAlignedFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(FORM_WIDTH, 260));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Username"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(fieldLabel("Password"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 22, 0);
        form.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        styleLoginButton(loginButton);
        loginButton.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(loginButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(loginButton, gbc);

        return form;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private void styleInput(JTextField field) {
        field.setMinimumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        field.setPreferredSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        field.setMaximumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Border rounded = BorderFactory.createLineBorder(INPUT_BORDER, 1, true);
        Border padding = BorderFactory.createEmptyBorder(6, 10, 6, 10);
        field.setBorder(BorderFactory.createCompoundBorder(rounded, padding));
        field.setBackground(Color.WHITE);
    }

    private void styleLoginButton(JButton button) {
        button.setMinimumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        button.setPreferredSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        button.setMaximumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_BG);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private javax.swing.ImageIcon loadScaledLogo(int maxWidth, int maxHeight) {
        URL logoUrl = getClass().getResource("/com/compprog1282025/assets/motorph_logo.png");
        if (logoUrl == null) {
            return new javax.swing.ImageIcon();
        }
        Image source = new javax.swing.ImageIcon(logoUrl).getImage();
        int srcW = source.getWidth(null);
        int srcH = source.getHeight(null);
        if (srcW <= 0 || srcH <= 0) {
            return new javax.swing.ImageIcon(source);
        }

        double ratio = Math.min((double) maxWidth / srcW, (double) maxHeight / srcH);
        int targetW = Math.max(1, (int) Math.round(srcW * ratio));
        int targetH = Math.max(1, (int) Math.round(srcH * ratio));
        return new javax.swing.ImageIcon(source.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH));
    }

    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Session session = appContext.getAuthService().login(username, password);
        if (session == null || !session.isActive()) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        appContext.setCurrentSession(session);
        DashboardFrame dashboardFrame = new DashboardFrame(appContext, session);
        dashboardFrame.setVisible(true);
        dispose();
    }
}