package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.InvalidAccessException;
import com.compprog1282025.service.EmployeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HRTeamAttendanceController {
    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final Supplier<Session> sessionSupplier;
    private final BiConsumer<String, String> alertCallback;
    private static final String BASE_OPTIONS_KEY = "hrHistoryEmployeeBaseOptions";
    private static final String AUTOCOMPLETE_KEY = "hrHistoryEmployeeAutocompleteConfigured";
    private static final String UPDATING_KEY = "hrHistoryEmployeeUpdating";

    public HRTeamAttendanceController(AttendanceService attendanceService) {
        this(attendanceService, null, null, null);
    }

    public HRTeamAttendanceController(
            AttendanceService attendanceService,
            EmployeeService employeeService,
            Supplier<Session> sessionSupplier,
            BiConsumer<String, String> alertCallback
    ) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.sessionSupplier = sessionSupplier;
        this.alertCallback = alertCallback;
    }

    public List<Attendance> loadTeamAttendance(int month, int year, Session session) throws InvalidAccessException {
        return attendanceService.getTeamAttendance(month, year, session);
    }

    public List<Attendance> loadEmployeeAttendanceHistory(int employeeNumber, LocalDate fromDate, LocalDate toDate, Session session) throws InvalidAccessException {
        return attendanceService.getEmployeeAttendanceHistory(employeeNumber, fromDate, toDate, session);
    }

    public void loadDefault(Session session) throws InvalidAccessException {
        LocalDate now = LocalDate.now();
        attendanceService.getTeamAttendance(now.getMonthValue(), now.getYear(), session);
    }

    public void setupPane(
            ComboBox<String> teamMonthCombo,
            ComboBox<Integer> teamYearCombo,
            TableView<Attendance> teamAttendanceTable,
            ComboBox<String> historyEmployeeCombo,
            TableView<Attendance> employeeHistoryTable
    ) {
        if (teamMonthCombo == null || teamYearCombo == null) {
            return;
        }
        teamMonthCombo.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        teamMonthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        teamYearCombo.getItems().clear();
        int currentYear = LocalDate.now().getYear();
        for (int year = 2020; year <= currentYear; year++) {
            teamYearCombo.getItems().add(year);
        }
        teamYearCombo.setValue(currentYear);

        configureAttendanceTable(teamAttendanceTable, true);
        configureAttendanceTable(employeeHistoryTable, false);
        setupHistoryEmployeeSelector(historyEmployeeCombo);
    }

    public void refreshHistoryEmployeeSelector(ComboBox<String> historyEmployeeCombo) {
        setupHistoryEmployeeSelector(historyEmployeeCombo);
    }

    public void loadTeamAttendance(
            ComboBox<String> monthCombo,
            ComboBox<Integer> yearCombo,
            TableView<Attendance> teamAttendanceTable
    ) {
        if (monthCombo == null || yearCombo == null || teamAttendanceTable == null) {
            return;
        }
        int month = monthCombo.getSelectionModel().getSelectedIndex() + 1;
        Integer year = yearCombo.getValue();
        if (month <= 0 || year == null) {
            return;
        }
        List<Attendance> rows = loadTeamAttendance(month, year, requireSession());
        teamAttendanceTable.setItems(FXCollections.observableArrayList(rows));
    }

    public void loadEmployeeHistory(
            ComboBox<String> employeeCombo,
            DatePicker fromPicker,
            DatePicker toPicker,
            TableView<Attendance> historyTable
    ) {
        if (employeeCombo == null || historyTable == null) {
            return;
        }
        Integer employeeNumber = resolveSelectedHistoryEmployeeNumber(employeeCombo);
        if (employeeNumber == null) {
            showAlert("Validation", "Please select a valid employee.");
            return;
        }

        try {
            List<Attendance> rows = loadEmployeeAttendanceHistory(
                    employeeNumber,
                    fromPicker != null ? fromPicker.getValue() : null,
                    toPicker != null ? toPicker.getValue() : null,
                    requireSession()
            );
            historyTable.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            showAlert("Access Denied", e.getMessage());
        }
    }

    private void setupHistoryEmployeeSelector(ComboBox<String> combo) {
        if (combo == null || employeeService == null) {
            return;
        }

        List<String> options = employeeService.getEmployeeDirectory(requireSession()).stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .map(emp -> emp.getEmployeeNumber() + " - " + emp.getFirstName() + " " + emp.getLastName())
                .toList();

        combo.setItems(FXCollections.observableArrayList(options));
        combo.setEditable(true);
        combo.setPrefWidth(320);
        combo.setMaxWidth(420);
        combo.getProperties().put(BASE_OPTIONS_KEY, List.copyOf(options));
        configureHistoryEmployeeAutocomplete(combo);
    }

    @SuppressWarnings("unchecked")
    private List<String> getHistoryEmployeeBaseOptions(ComboBox<String> combo) {
        Object raw = combo.getProperties().get(BASE_OPTIONS_KEY);
        if (raw instanceof List<?>) {
            return (List<String>) raw;
        }
        return List.of();
    }

    private void configureHistoryEmployeeAutocomplete(ComboBox<String> combo) {
        if (combo == null || Boolean.TRUE.equals(combo.getProperties().get(AUTOCOMPLETE_KEY))) {
            return;
        }
        combo.getProperties().put(AUTOCOMPLETE_KEY, true);

        combo.setOnShowing(event -> {
            List<String> baseOptions = getHistoryEmployeeBaseOptions(combo);
            combo.getProperties().put(UPDATING_KEY, true);
            combo.getItems().setAll(baseOptions);
            combo.getProperties().put(UPDATING_KEY, false);
        });

        combo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(combo.getProperties().get(UPDATING_KEY))) {
                return;
            }
            applyHistoryEmployeeFilter(combo, newValue);
        });
    }

    private void applyHistoryEmployeeFilter(ComboBox<String> combo, String query) {
        List<String> baseOptions = getHistoryEmployeeBaseOptions(combo);
        if (baseOptions.isEmpty()) {
            return;
        }

        String keyword = query == null ? "" : query.trim().toLowerCase();
        List<String> filtered = baseOptions.stream()
                .filter(option -> keyword.isEmpty()
                        || option.toLowerCase().contains(keyword)
                        || option.startsWith(query == null ? "" : query.trim()))
                .toList();

        combo.getProperties().put(UPDATING_KEY, true);
        combo.getItems().setAll(filtered.isEmpty() ? baseOptions : filtered);
        combo.getEditor().setText(query == null ? "" : query);
        combo.getEditor().positionCaret((query == null ? "" : query).length());
        combo.getProperties().put(UPDATING_KEY, false);
    }

    private Integer resolveSelectedHistoryEmployeeNumber(ComboBox<String> combo) {
        if (combo == null) {
            return null;
        }

        String editorText = combo.getEditor() != null ? combo.getEditor().getText() : "";
        String selectedValue = combo.getValue();
        String raw = (editorText != null && !editorText.trim().isEmpty()) ? editorText.trim() : (selectedValue == null ? "" : selectedValue.trim());
        if (raw.isEmpty()) {
            return null;
        }

        List<String> baseOptions = getHistoryEmployeeBaseOptions(combo);
        String matched = baseOptions.stream()
                .filter(option -> option.equalsIgnoreCase(raw))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            return null;
        }

        int separatorIndex = matched.indexOf(" - ");
        if (separatorIndex <= 0) {
            return null;
        }

        try {
            return Integer.parseInt(matched.substring(0, separatorIndex).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void configureAttendanceTable(TableView<Attendance> table, boolean includeEmployeeNumber) {
        if (table == null) {
            return;
        }
        table.getColumns().clear();

        if (includeEmployeeNumber) {
            TableColumn<Attendance, Integer> empCol = new TableColumn<>("Employee #");
            empCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
            table.getColumns().add(empCol);
        }

        TableColumn<Attendance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Attendance, String> inCol = new TableColumn<>("Time In");
        inCol.setCellValueFactory(new PropertyValueFactory<>("timeInFormatted"));
        TableColumn<Attendance, String> outCol = new TableColumn<>("Time Out");
        outCol.setCellValueFactory(new PropertyValueFactory<>("timeOutFormatted"));
        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(dateCol, inCol, outCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private Session requireSession() {
        if (sessionSupplier == null || sessionSupplier.get() == null) {
            throw new IllegalStateException("No active session available.");
        }
        return sessionSupplier.get();
    }

    private void showAlert(String title, String message) {
        if (alertCallback != null) {
            alertCallback.accept(title, message);
        }
    }
}
