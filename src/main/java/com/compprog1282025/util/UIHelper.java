package com.compprog1282025.util;

import javax.swing.*;
import java.awt.*;

public class UIHelper {

    // Theme settings
    public static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BG_COLOR = new Color(250, 252, 255);             // Soft page background
    private static final Color FG_COLOR = new Color(33, 37, 41);                // Primary text
    private static final Color BORDER_COLOR = new Color(220, 220, 220);         // Soft gray border
    private static final Color BUTTON_COLOR = new Color(0x1c3680);              // MotorPH Blue
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color INFO_BOX_BG = Color.WHITE;
    private static final Color COMBOBOX_BG = new Color(245, 245, 245);          // Soft gray for combo box
    private static final Color SECTION_HEADER_COLOR = new Color(20, 20, 20);

    // Apply modern theming to any Swing component
    public static void applyTheme(JComponent comp) {
        comp.setFont(DEFAULT_FONT);
        comp.setForeground(FG_COLOR);

        if (comp instanceof JButton btn) {
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(BUTTON_TEXT_COLOR);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        } else if (comp instanceof JTextField || comp instanceof JTextArea) {
            comp.setBackground(Color.WHITE);
            comp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        } else if (comp instanceof JComboBox<?> comboBox) {
            comboBox.setFont(DEFAULT_FONT);
            comboBox.setBackground(COMBOBOX_BG);
            comboBox.setForeground(FG_COLOR);
            comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        } else if (comp instanceof JLabel lbl) {
            lbl.setFont(DEFAULT_FONT);
            lbl.setForeground(FG_COLOR);
            lbl.setBackground(BG_COLOR);

        } else if (comp instanceof JPanel panel) {
            panel.setBackground(BG_COLOR);
        } else {
            comp.setBackground(BG_COLOR);
        }
    }

    // Apply theme to a container and all nested children
    public static void applyThemeRecursively(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComponent jComp) {
                applyTheme(jComp);
            }
            if (comp instanceof Container nested) {
                applyThemeRecursively(nested);
            }
        }
    }

    // Create a boxed info row panel for any display text
    public static JPanel createInfoRow(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(DEFAULT_FONT);
        label.setForeground(FG_COLOR);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(INFO_BOX_BG);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        row.add(label);
        return row;
    }

    // Create a bold section header label
    public static JLabel sectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(DEFAULT_FONT.deriveFont(Font.BOLD, 15f));
        label.setForeground(SECTION_HEADER_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
        return label;
    }

}
