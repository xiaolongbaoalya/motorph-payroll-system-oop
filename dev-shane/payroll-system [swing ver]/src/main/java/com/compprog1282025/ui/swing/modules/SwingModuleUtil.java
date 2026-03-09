package com.compprog1282025.ui.swing.modules;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.Font;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public final class SwingModuleUtil {
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final int TABLE_ROW_HEIGHT = 28;

    private SwingModuleUtil() {}

    public static String money(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        return format.format(value);
    }

    public static Integer parseInteger(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    public static void setTableData(JTable table, String[] columns, Object[][] rows) {
        setTableData(table, columns, rows, true);
    }

    public static void setTableData(JTable table, String[] columns, Object[][] rows, boolean applyDefaultStyle) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        if (applyDefaultStyle) {
            applyStandardTableStyle(table);
        }
    }

    public static void applyStandardTableStyle(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setIntercellSpacing(new java.awt.Dimension(6, 4));

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(TABLE_HEADER_FONT);
            header.setReorderingAllowed(false);
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 6, 2, 6));

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            Class<?> columnClass = table.getColumnClass(i);
            if (Boolean.class.equals(columnClass) || boolean.class.equals(columnClass)) {
                continue;
            }
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static void info(java.awt.Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(java.awt.Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(java.awt.Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
