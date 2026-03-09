package com.compprog1282025.ui.swing.modules;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePickerControl extends JButton {
    private static final java.time.format.DateTimeFormatter FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate selectedDate;
    private final int startYear;
    private final int endYear;

    public DatePickerControl(LocalDate initialDate) {
        this(initialDate, 2022, LocalDate.now().getYear() + 10);
    }

    public DatePickerControl(LocalDate initialDate, int startYear, int endYear) {
        this.selectedDate = initialDate;
        this.startYear = Math.min(startYear, endYear);
        this.endYear = Math.max(startYear, endYear);
        setPreferredSize(new Dimension(140, 30));
        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setText(FORMATTER.format(initialDate));
        addActionListener(e -> openPicker());
    }

    public LocalDate getDate() {
        return selectedDate;
    }

    public void setDate(LocalDate date) {
        selectedDate = date;
        setText(FORMATTER.format(date));
    }

    private void openPicker() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CalendarPickerDialog dialog = new CalendarPickerDialog(owner, selectedDate, startYear, endYear);
        LocalDate picked = dialog.pickDate();
        if (picked != null) {
            setDate(picked);
        }
    }

    private static final class CalendarPickerDialog extends JDialog {
        private LocalDate selectedDate;
        private YearMonth visibleMonth;

        private final JComboBox<String> cmbMonth = new JComboBox<>();
        private final JComboBox<Integer> cmbYear = new JComboBox<>();
        private final JPanel daysPanel = new JPanel(new GridLayout(6, 7, 4, 4));
        private final int startYear;
        private final int endYear;

        private CalendarPickerDialog(Window owner, LocalDate initial, int startYear, int endYear) {
            super(owner, "Select Date", ModalityType.APPLICATION_MODAL);
            this.selectedDate = initial;
            this.visibleMonth = YearMonth.from(initial);
            this.startYear = startYear;
            this.endYear = endYear;

            for (Month month : Month.values()) {
                cmbMonth.addItem(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }

            for (int year = this.startYear; year <= this.endYear; year++) {
                cmbYear.addItem(year);
            }

            int year = visibleMonth.getYear();
            if (year < this.startYear) {
                year = this.startYear;
                visibleMonth = YearMonth.of(year, visibleMonth.getMonthValue());
            } else if (year > this.endYear) {
                year = this.endYear;
                visibleMonth = YearMonth.of(year, visibleMonth.getMonthValue());
            }

            cmbMonth.setSelectedIndex(visibleMonth.getMonthValue() - 1);
            cmbYear.setSelectedItem(year);

            cmbMonth.addActionListener(e -> updateVisibleMonthFromSelectors());
            cmbYear.addActionListener(e -> updateVisibleMonthFromSelectors());

            setLayout(new BorderLayout(8, 8));
            setSize(340, 320);
            setLocationRelativeTo(owner);

            JPanel header = new JPanel(new BorderLayout(6, 6));
            JButton prev = new JButton("<");
            JButton next = new JButton(">");

            prev.addActionListener(e -> {
                visibleMonth = visibleMonth.minusMonths(1);
                syncSelectors();
                refreshCalendar();
            });
            next.addActionListener(e -> {
                visibleMonth = visibleMonth.plusMonths(1);
                syncSelectors();
                refreshCalendar();
            });

            JPanel centerSelector = new JPanel(new GridLayout(1, 2, 6, 0));
            centerSelector.add(cmbMonth);
            centerSelector.add(cmbYear);

            header.add(prev, BorderLayout.WEST);
            header.add(centerSelector, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);

            JPanel weekDays = new JPanel(new GridLayout(1, 7, 4, 4));
            String[] names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String n : names) {
                JLabel l = new JLabel(n, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                weekDays.add(l);
            }

            JPanel center = new JPanel(new BorderLayout(4, 4));
            center.add(weekDays, BorderLayout.NORTH);
            center.add(daysPanel, BorderLayout.CENTER);

            JButton today = new JButton("Today");
            today.addActionListener(e -> {
                selectedDate = LocalDate.now();
                dispose();
            });

            add(header, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
            add(today, BorderLayout.SOUTH);

            refreshCalendar();
        }

        private void syncSelectors() {
            cmbMonth.setSelectedIndex(visibleMonth.getMonthValue() - 1);
            cmbYear.setSelectedItem(visibleMonth.getYear());
        }

        private void updateVisibleMonthFromSelectors() {
            Integer year = (Integer) cmbYear.getSelectedItem();
            int month = cmbMonth.getSelectedIndex() + 1;
            if (year != null && month >= 1 && month <= 12) {
                visibleMonth = YearMonth.of(year, month);
                refreshCalendar();
            }
        }

        private LocalDate pickDate() {
            setVisible(true);
            return selectedDate;
        }

        private void refreshCalendar() {
            daysPanel.removeAll();

            LocalDate firstDay = visibleMonth.atDay(1);
            int shift = firstDay.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            if (shift < 0) {
                shift += 7;
            }

            int length = visibleMonth.lengthOfMonth();
            int cellCount = 42;
            for (int i = 0; i < cellCount; i++) {
                if (i < shift || i >= shift + length) {
                    daysPanel.add(new JLabel(""));
                } else {
                    int day = i - shift + 1;
                    LocalDate date = visibleMonth.atDay(day);
                    JButton btn = new JButton(String.valueOf(day));
                    btn.setMargin(new Insets(2, 2, 2, 2));
                    if (date.equals(selectedDate)) {
                        btn.setBackground(new Color(65, 131, 255));
                        btn.setForeground(Color.WHITE);
                        btn.setOpaque(true);
                    }
                    btn.addActionListener(e -> {
                        selectedDate = date;
                        dispose();
                    });
                    daysPanel.add(btn);
                }
            }
            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }
}
