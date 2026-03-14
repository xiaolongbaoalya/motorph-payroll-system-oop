package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HrEmployeeFormHelper {
    private static final String SUPERVISOR_NONE = "None";
    private static final String SUPERVISOR_BASE_OPTIONS_KEY = "supervisorBaseOptions";
    private static final String SUPERVISOR_AUTOCOMPLETE_KEY = "supervisorAutocompleteConfigured";
    private static final String SUPERVISOR_UPDATING_KEY = "supervisorUpdating";
    private static final String PHONE_PROMPT = "0917-123-4567";
    private static final String PHILHEALTH_PROMPT = "123456789012";
    private static final String PAG_IBIG_PROMPT = "123456789012";

    private final EmployeeService employeeService;
    private final Supplier<Session> sessionSupplier;
    private final BiConsumer<String, String> alertCallback;

    public HrEmployeeFormHelper(
            EmployeeService employeeService,
            Supplier<Session> sessionSupplier,
            BiConsumer<String, String> alertCallback
    ) {
        this.employeeService = employeeService;
        this.sessionSupplier = sessionSupplier;
        this.alertCallback = alertCallback;
    }

    public void setupAddForm(HrAddFormControls controls) {
        if (controls.statusCombo != null) {
            controls.statusCombo.setItems(FXCollections.observableArrayList("Regular", "Probationary"));
            controls.statusCombo.setValue("Regular");
        }
        populateDepartmentOptions(controls.departmentCombo);
        if (controls.birthdayPicker != null) {
            controls.birthdayPicker.setEditable(false);
        }
        populateSupervisorOptions(controls.supervisorCombo, null);
        if (controls.supervisorCombo != null) {
            controls.supervisorCombo.setEditable(true);
            controls.supervisorCombo.getSelectionModel().select(SUPERVISOR_NONE);
            controls.supervisorCombo.getEditor().setText(SUPERVISOR_NONE);
        }
        configureGovernmentAndPhoneInputs(controls.phoneField, controls.philHealthField, controls.pagIbigField);
        configureSssSegmentedFields(controls.sssPart1, controls.sssPart2, controls.sssPart3);
        configureTinSegmentedFields(controls.tinPart1, controls.tinPart2, controls.tinPart3, controls.tinPart4);
    }

    public Employee buildEmployeeFromAddForm(HrAddFormControls controls) {
        int employeeNumber = Integer.parseInt(readRequired(controls.employeeNumberField, "Employee number"));
        String lastName = readRequired(controls.lastNameField, "Last name");
        String firstName = readRequired(controls.firstNameField, "First name");
        LocalDate birthday = requireDate(controls.birthdayPicker, "Birthday");
        String address = readRequired(controls.addressField, "Address");
        String phone = readRequired(controls.phoneField, "Phone");
        String sss = buildSssValue(controls.sssPart1, controls.sssPart2, controls.sssPart3);
        String philHealth = readRequired(controls.philHealthField, "PhilHealth #");
        String tin = buildTinValue(controls.tinPart1, controls.tinPart2, controls.tinPart3, controls.tinPart4);
        String pagIbig = readRequired(controls.pagIbigField, "Pag-IBIG #");
        String status = controls.statusCombo.getValue() == null ? "Regular" : controls.statusCombo.getValue();
        String position = readRequired(controls.positionField, "Position");
        String department = readRequired(controls.departmentCombo, "Department");
        String supervisor = extractSupervisorName(controls.supervisorCombo, "Supervisor");

        double basicSalary = parseDouble(readRequired(controls.basicSalaryField, "Basic Salary"), "Basic Salary");
        double riceSubsidy = parseDouble(readRequired(controls.riceSubsidyField, "Rice Subsidy"), "Rice Subsidy");
        double phoneAllowance = parseDouble(readRequired(controls.phoneAllowanceField, "Phone Allowance"), "Phone Allowance");
        double clothingAllowance = parseDouble(readRequired(controls.clothingAllowanceField, "Clothing Allowance"), "Clothing Allowance");

        return new Employee(
                employeeNumber,
                firstName,
                lastName,
                birthday,
                new ContactInfo(address, phone),
                new GovernmentID(sss, philHealth, tin, pagIbig),
                new Position(position, department),
                new Compensation(basicSalary, riceSubsidy, phoneAllowance, clothingAllowance),
                status,
                supervisor
        );
    }

    public void clearAddForm(HrAddFormControls controls) {
        controls.employeeNumberField.clear();
        controls.lastNameField.clear();
        controls.firstNameField.clear();
        controls.birthdayPicker.setValue(null);
        controls.addressField.clear();
        controls.phoneField.clear();
        clearTextFields(controls.sssPart1, controls.sssPart2, controls.sssPart3);
        controls.philHealthField.clear();
        clearTextFields(controls.tinPart1, controls.tinPart2, controls.tinPart3, controls.tinPart4);
        controls.pagIbigField.clear();
        controls.positionField.clear();
        if (controls.departmentCombo != null) {
            if (!controls.departmentCombo.getItems().isEmpty()) {
                controls.departmentCombo.getSelectionModel().selectFirst();
            } else {
                controls.departmentCombo.getSelectionModel().clearSelection();
            }
        }
        if (controls.supervisorCombo != null) {
            controls.supervisorCombo.getSelectionModel().select(SUPERVISOR_NONE);
            controls.supervisorCombo.getEditor().setText(SUPERVISOR_NONE);
        }
        controls.basicSalaryField.clear();
        controls.riceSubsidyField.clear();
        controls.phoneAllowanceField.clear();
        controls.clothingAllowanceField.clear();
        controls.statusCombo.setValue("Regular");
    }

    public void openEmployeeViewWindow(Employee employee) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Employee Details - " + employee.getEmployeeNumber());

        VBox root = new VBox(8);
        root.setPadding(new Insets(14));
        root.getChildren().addAll(
                new Label("Employee #: " + employee.getEmployeeNumber()),
                new Label("Name: " + employee.getLastName() + ", " + employee.getFirstName()),
                new Label("Birthday: " + (employee.getBirthday() != null ? employee.getBirthday() : "N/A")),
                new Label("Address: " + employee.getContact().getAddress()),
                new Label("Phone: " + employee.getContact().getPhone()),
                new Label("SSS: " + employee.getGovernmentID().getSss()),
                new Label("PhilHealth: " + employee.getGovernmentID().getPhilHealth()),
                new Label("TIN: " + employee.getGovernmentID().getTin()),
                new Label("Pag-IBIG: " + employee.getGovernmentID().getPagIbig()),
                new Label("Status: " + employee.getStatus()),
                new Label("Position: " + employee.getPosition().getJobTitle()),
                new Label("Department: " + employee.getPosition().getDepartment()),
                new Label("Supervisor: " + employee.getSupervisorName()),
                new Label(String.format("Basic Salary: %.2f", employee.getSalary().getBasicSalary())),
                new Label(String.format("Rice Subsidy: %.2f", employee.getSalary().getRiceSubsidy())),
                new Label(String.format("Phone Allowance: %.2f", employee.getSalary().getPhoneAllowance())),
                new Label(String.format("Clothing Allowance: %.2f", employee.getSalary().getClothingAllowance()))
        );

        stage.setScene(new Scene(root, 430, 520));
        stage.showAndWait();
    }

    public void openEmployeeUpdateWindow(Employee employee, Runnable afterSave) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Update Employee - " + employee.getEmployeeNumber());

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(10);
        form.setPadding(new Insets(14));

        javafx.scene.layout.ColumnConstraints labelCol = new javafx.scene.layout.ColumnConstraints();
        labelCol.setMinWidth(170);
        javafx.scene.layout.ColumnConstraints valueCol = new javafx.scene.layout.ColumnConstraints();
        valueCol.setPrefWidth(360);
        valueCol.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().setAll(labelCol, valueCol);

        TextField txtEmpNo = new TextField(String.valueOf(employee.getEmployeeNumber()));
        TextField txtFirst = new TextField(employee.getFirstName());
        TextField txtLast = new TextField(employee.getLastName());
        DatePicker dpBirth = new DatePicker(employee.getBirthday());
        TextArea txtAddress = new TextArea(employee.getContact().getAddress());
        txtAddress.setPrefRowCount(2);
        TextField txtPhone = new TextField(employee.getContact().getPhone());
        TextField txtSssPart1 = new TextField();
        TextField txtSssPart2 = new TextField();
        TextField txtSssPart3 = new TextField();
        TextField txtPhilHealth = new TextField(employee.getGovernmentID().getPhilHealth());
        TextField txtTinPart1 = new TextField();
        TextField txtTinPart2 = new TextField();
        TextField txtTinPart3 = new TextField();
        TextField txtTinPart4 = new TextField();
        TextField txtPagIbig = new TextField(employee.getGovernmentID().getPagIbig());

        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("Regular", "Probationary"));
        cmbStatus.setValue(employee.getStatus());
        TextField txtPosition = new TextField(employee.getPosition().getJobTitle());
        TextField txtDept = new TextField(employee.getPosition().getDepartment());
        ComboBox<String> cmbSupervisor = createSupervisorCombo(employee.getSupervisorName(), employee.getEmployeeNumber());

        TextField txtBasicSalary = new TextField(String.valueOf(employee.getSalary().getBasicSalary()));
        TextField txtRiceSubsidy = new TextField(String.valueOf(employee.getSalary().getRiceSubsidy()));
        TextField txtPhoneAllowance = new TextField(String.valueOf(employee.getSalary().getPhoneAllowance()));
        TextField txtClothingAllowance = new TextField(String.valueOf(employee.getSalary().getClothingAllowance()));
        configureGovernmentAndPhoneInputs(txtPhone, txtPhilHealth, txtPagIbig);
        configureSssSegmentedFields(txtSssPart1, txtSssPart2, txtSssPart3);
        populateSssSegments(employee.getGovernmentID().getSss(), txtSssPart1, txtSssPart2, txtSssPart3);
        configureTinSegmentedFields(txtTinPart1, txtTinPart2, txtTinPart3, txtTinPart4);
        populateTinSegments(employee.getGovernmentID().getTin(), txtTinPart1, txtTinPart2, txtTinPart3, txtTinPart4);

        HBox sssBox = createSssInputBox(txtSssPart1, txtSssPart2, txtSssPart3);
        HBox tinBox = createTinInputBox(txtTinPart1, txtTinPart2, txtTinPart3, txtTinPart4);

        int row = 0;
        addRow(form, row++, "Employee #", txtEmpNo);
        addRow(form, row++, "First Name", txtFirst);
        addRow(form, row++, "Last Name", txtLast);
        addRow(form, row++, "Birthday", dpBirth);
        addRow(form, row++, "Address", txtAddress);
        addRow(form, row++, "Phone", txtPhone);
        addRow(form, row++, "SSS #", sssBox);
        addRow(form, row++, "PhilHealth #", txtPhilHealth);
        addRow(form, row++, "TIN #", tinBox);
        addRow(form, row++, "Pag-IBIG #", txtPagIbig);
        addRow(form, row++, "Status", cmbStatus);
        addRow(form, row++, "Position", txtPosition);
        addRow(form, row++, "Department", txtDept);
        addRow(form, row++, "Supervisor", cmbSupervisor);
        addRow(form, row++, "Basic Salary", txtBasicSalary);
        addRow(form, row++, "Rice Subsidy", txtRiceSubsidy);
        addRow(form, row++, "Phone Allowance", txtPhoneAllowance);
        addRow(form, row, "Clothing Allowance", txtClothingAllowance);

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setPrefViewportHeight(560);

        Button btnSave = new Button("Save");
        btnSave.getStyleClass().add("success-button");
        btnSave.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Update");
            confirm.setHeaderText("Are you sure you want to update this employee?");
            confirm.setContentText("This will overwrite the current employee details.");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            try {
                int updatedEmployeeNumber = Integer.parseInt(readRequired(txtEmpNo, "Employee Number"));
                Employee updated = new Employee(
                        updatedEmployeeNumber,
                        readRequired(txtFirst, "First name"),
                        readRequired(txtLast, "Last name"),
                        requireDate(dpBirth, "Birthday"),
                        new ContactInfo(readRequired(txtAddress, "Address"), readRequired(txtPhone, "Phone")),
                        new GovernmentID(
                                buildSssValue(txtSssPart1, txtSssPart2, txtSssPart3),
                                readRequired(txtPhilHealth, "PhilHealth #"),
                                buildTinValue(txtTinPart1, txtTinPart2, txtTinPart3, txtTinPart4),
                                readRequired(txtPagIbig, "Pag-IBIG #")
                        ),
                        new Position(readRequired(txtPosition, "Position"), readRequired(txtDept, "Department")),
                        new Compensation(
                                parseDouble(readRequired(txtBasicSalary, "Basic Salary"), "Basic Salary"),
                                parseDouble(readRequired(txtRiceSubsidy, "Rice Subsidy"), "Rice Subsidy"),
                                parseDouble(readRequired(txtPhoneAllowance, "Phone Allowance"), "Phone Allowance"),
                                parseDouble(readRequired(txtClothingAllowance, "Clothing Allowance"), "Clothing Allowance")
                        ),
                        cmbStatus.getValue() == null ? "Regular" : cmbStatus.getValue(),
                        extractSupervisorName(cmbSupervisor, "Supervisor")
                );
                employeeService.updateEmployee(employee.getEmployeeNumber(), updated, currentSession());
                afterSave.run();
                alertCallback.accept("Success", "Employee updated successfully.");
                stage.close();
            } catch (NumberFormatException ex) {
                alertCallback.accept("Validation Error", "Employee Number must be a valid number.");
            } catch (Exception ex) {
                alertCallback.accept("Validation Error", ex.getMessage());
            }
        });

        VBox root = new VBox(10, scroll, new HBox(10, btnSave));
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 680, 700));
        stage.showAndWait();
    }

    private Session currentSession() {
        return sessionSupplier.get();
    }

    private void populateDepartmentOptions(ComboBox<String> departmentCombo) {
        if (departmentCombo == null) {
            return;
        }

        List<String> departments = employeeService.getEmployeeDirectory(currentSession()).stream()
                .map(emp -> emp.getPosition() != null ? emp.getPosition().getDepartment() : null)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .sorted()
                .toList();

        departmentCombo.setItems(FXCollections.observableArrayList(departments));
        if (!departments.isEmpty()) {
            departmentCombo.getSelectionModel().selectFirst();
        }
    }

    private void populateSupervisorOptions(ComboBox<String> combo, Integer excludeEmployeeNumber) {
        if (combo == null) {
            return;
        }

        List<String> options = employeeService.getEmployeeDirectory(currentSession()).stream()
                .filter(emp -> excludeEmployeeNumber == null || emp.getEmployeeNumber() != excludeEmployeeNumber)
                .map(this::formatSupervisorOption)
                .distinct()
                .sorted()
                .toList();

        List<String> optionsWithNone = new ArrayList<>();
        optionsWithNone.add(SUPERVISOR_NONE);
        optionsWithNone.addAll(options);

        combo.getItems().setAll(optionsWithNone);
        combo.getProperties().put(SUPERVISOR_BASE_OPTIONS_KEY, List.copyOf(optionsWithNone));
        combo.setEditable(true);
        configureSupervisorAutocomplete(combo);
    }

    private String formatSupervisorOption(Employee employee) {
        String name = employee.getFirstName() + " " + employee.getLastName();
        String position = employee.getPosition() != null ? employee.getPosition().getJobTitle() : "";
        return position == null || position.isBlank() ? name : name + " - " + position;
    }

    private ComboBox<String> createSupervisorCombo(String currentSupervisor, Integer excludeEmployeeNumber) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setEditable(true);
        combo.setMaxWidth(Double.MAX_VALUE);
        populateSupervisorOptions(combo, excludeEmployeeNumber);

        String normalizedCurrent = currentSupervisor == null ? "" : currentSupervisor.trim();
        if (normalizedCurrent.isEmpty() || normalizedCurrent.equalsIgnoreCase(SUPERVISOR_NONE)) {
            combo.getSelectionModel().select(SUPERVISOR_NONE);
            combo.getEditor().setText(SUPERVISOR_NONE);
        } else {
            String match = combo.getItems().stream()
                    .filter(item -> extractSupervisorNameFromDisplay(item).equalsIgnoreCase(normalizedCurrent))
                    .findFirst()
                    .orElse(null);
            if (match != null) {
                combo.getSelectionModel().select(match);
                combo.getEditor().setText(match);
            } else {
                combo.getEditor().setText(normalizedCurrent);
            }
        }

        return combo;
    }

    private String extractSupervisorName(ComboBox<String> combo, String label) {
        if (combo == null) {
            throw new IllegalArgumentException(label + " is required.");
        }

        String editorText = combo.getEditor() != null ? combo.getEditor().getText() : "";
        String selectedValue = combo.getValue();
        String raw = (editorText != null && !editorText.trim().isEmpty()) ? editorText : selectedValue;
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Supervisor must match an existing employee or be set to \"None\".");
        }

        String normalizedRaw = raw.trim();
        if (normalizedRaw.equalsIgnoreCase(SUPERVISOR_NONE)) {
            return SUPERVISOR_NONE;
        }
        String normalizedSupervisorName = extractSupervisorNameFromDisplay(normalizedRaw);

        List<String> baseOptions = getSupervisorBaseOptions(combo);
        String matchedDisplay = baseOptions.stream()
                .filter(option -> !SUPERVISOR_NONE.equalsIgnoreCase(option))
                .filter(option -> option.equalsIgnoreCase(normalizedRaw)
                        || extractSupervisorNameFromDisplay(option).equalsIgnoreCase(normalizedRaw)
                        || extractSupervisorNameFromDisplay(option).equalsIgnoreCase(normalizedSupervisorName))
                .findFirst()
                .orElse(null);

        if (matchedDisplay == null) {
            throw new IllegalArgumentException("Supervisor must match an existing employee or be set to \"None\".");
        }
        return extractSupervisorNameFromDisplay(matchedDisplay);
    }

    private String extractSupervisorNameFromDisplay(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.trim();
        int idx = cleaned.indexOf(" - ");
        if (idx < 0) {
            idx = cleaned.indexOf(" — ");
        }
        if (idx >= 0) {
            return cleaned.substring(0, idx).trim();
        }
        return cleaned;
    }

    @SuppressWarnings("unchecked")
    private List<String> getSupervisorBaseOptions(ComboBox<String> combo) {
        Object raw = combo.getProperties().get(SUPERVISOR_BASE_OPTIONS_KEY);
        if (raw instanceof List<?>) {
            return (List<String>) raw;
        }
        return List.copyOf(combo.getItems());
    }

    private void configureSupervisorAutocomplete(ComboBox<String> combo) {
        if (combo == null || Boolean.TRUE.equals(combo.getProperties().get(SUPERVISOR_AUTOCOMPLETE_KEY))) {
            return;
        }
        combo.getProperties().put(SUPERVISOR_AUTOCOMPLETE_KEY, true);

        combo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            combo.getProperties().put(SUPERVISOR_UPDATING_KEY, true);
            combo.getEditor().setText(newValue);
            combo.getProperties().put(SUPERVISOR_UPDATING_KEY, false);
        });

        combo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(combo.getProperties().get(SUPERVISOR_UPDATING_KEY))) {
                return;
            }
            applySupervisorFilter(combo, newValue);
        });
    }

    private void applySupervisorFilter(ComboBox<String> combo, String searchText) {
        List<String> baseOptions = getSupervisorBaseOptions(combo);
        String keyword = searchText == null ? "" : searchText.trim().toLowerCase();

        List<String> filtered = baseOptions.stream()
                .filter(option -> keyword.isEmpty()
                        || option.toLowerCase().contains(keyword)
                        || extractSupervisorNameFromDisplay(option).toLowerCase().contains(keyword))
                .toList();

        String currentText = combo.getEditor().getText();
        int caret = combo.getEditor().getCaretPosition();
        combo.getProperties().put(SUPERVISOR_UPDATING_KEY, true);
        combo.getItems().setAll(filtered.isEmpty() ? baseOptions : filtered);
        combo.getEditor().setText(currentText);
        if (caret >= 0 && caret <= currentText.length()) {
            combo.getEditor().positionCaret(caret);
        }
        combo.getProperties().put(SUPERVISOR_UPDATING_KEY, false);

        if (combo.isFocused()) {
            combo.show();
        }
    }

    private void addRow(GridPane form, int row, String label, Node node) {
        form.add(new Label(label + ":"), 0, row);
        form.add(node, 1, row);
    }

    private String readRequired(TextInputControl field, String label) {
        if (field == null || field.getText() == null || field.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return field.getText().trim();
    }

    private String readRequired(ComboBox<String> combo, String label) {
        if (combo == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        String value = combo.getEditor() != null && !combo.getEditor().getText().trim().isEmpty()
                ? combo.getEditor().getText()
                : combo.getValue();
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    private LocalDate requireDate(DatePicker picker, String label) {
        if (picker == null || picker.getValue() == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return picker.getValue();
    }

    private double parseDouble(String raw, String label) {
        return Double.parseDouble(raw.replace(",", ""));
    }

    private void configureGovernmentAndPhoneInputs(
            TextField phoneField,
            TextField philHealthField,
            TextField pagIbigField
    ) {
        configurePhoneField(phoneField);
        configureDigitsOnlyField(philHealthField, PHILHEALTH_PROMPT, 12);
        configureDigitsOnlyField(pagIbigField, PAG_IBIG_PROMPT, 12);
    }

    private void configurePhoneField(TextField field) {
        if (field == null) {
            return;
        }
        field.setPromptText(PHONE_PROMPT);
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 15 || !newText.matches("[0-9-]*")) {
                return null;
            }
            return change;
        }));
    }

    private void configureDigitsOnlyField(TextField field, String promptText, int maxDigits) {
        if (field == null) {
            return;
        }
        field.setPromptText(promptText);
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("\\d{0," + maxDigits + "}")) {
                return null;
            }
            return change;
        }));
    }

    private void configureSssSegmentedFields(TextField part1, TextField part2, TextField part3) {
        configureSegmentedDigitsField(part1, "00", 2);
        configureSegmentedDigitsField(part2, "0000000", 7);
        configureSegmentedDigitsField(part3, "0", 1);
    }

    private void configureTinSegmentedFields(TextField... fields) {
        for (TextField field : fields) {
            configureSegmentedDigitsField(field, "000", 3);
        }
    }

    private void configureSegmentedDigitsField(TextField field, String promptText, int maxDigits) {
        if (field == null) {
            return;
        }
        field.setPromptText(promptText);
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("\\d{0," + maxDigits + "}")) {
                return null;
            }
            return change;
        }));
    }

    private HBox createSssInputBox(TextField part1, TextField part2, TextField part3) {
        return createSegmentedInputBox(
                new TextField[]{part1, part2, part3},
                new double[]{70, 150, 50}
        );
    }

    private HBox createTinInputBox(TextField... fields) {
        return createSegmentedInputBox(fields, new double[]{95, 95, 95, 95});
    }

    private HBox createSegmentedInputBox(TextField[] fields, double[] widths) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < fields.length; i++) {
            TextField field = fields[i];
            field.getStyleClass().add("hr-add-field");
            field.setPrefWidth(widths[i]);
            box.getChildren().add(field);
            if (i < fields.length - 1) {
                box.getChildren().add(new Label("-"));
            }
        }
        return box;
    }

    private void populateSssSegments(String sss, TextField... fields) {
        String[] segments = sss == null ? new String[0] : sss.split("-");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                fields[i].setText(i < segments.length ? segments[i] : "");
            }
        }
    }

    private void populateTinSegments(String tin, TextField... fields) {
        String[] segments = tin == null ? new String[0] : tin.split("-");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                fields[i].setText(i < segments.length ? segments[i] : "");
            }
        }
    }

    private String buildTinValue(TextField... fields) {
        String[] segments = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            segments[i] = readRequired(fields[i], "TIN #");
        }
        return String.join("-", segments);
    }

    private String buildSssValue(TextField... fields) {
        String[] segments = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            segments[i] = readRequired(fields[i], "SSS #");
        }
        return String.join("-", segments);
    }

    private void clearTextFields(TextField... fields) {
        for (TextField field : fields) {
            if (field != null) {
                field.clear();
            }
        }
    }

    public static class HrAddFormControls {
        public final TextField employeeNumberField;
        public final TextField firstNameField;
        public final TextField lastNameField;
        public final DatePicker birthdayPicker;
        public final TextArea addressField;
        public final TextField phoneField;
        public final TextField sssPart1;
        public final TextField sssPart2;
        public final TextField sssPart3;
        public final TextField philHealthField;
        public final TextField tinPart1;
        public final TextField tinPart2;
        public final TextField tinPart3;
        public final TextField tinPart4;
        public final TextField pagIbigField;
        public final ComboBox<String> statusCombo;
        public final TextField positionField;
        public final ComboBox<String> departmentCombo;
        public final ComboBox<String> supervisorCombo;
        public final TextField basicSalaryField;
        public final TextField riceSubsidyField;
        public final TextField phoneAllowanceField;
        public final TextField clothingAllowanceField;

        public HrAddFormControls(
                TextField employeeNumberField,
                TextField firstNameField,
                TextField lastNameField,
                DatePicker birthdayPicker,
                TextArea addressField,
                TextField phoneField,
                TextField sssPart1,
                TextField sssPart2,
                TextField sssPart3,
                TextField philHealthField,
                TextField tinPart1,
                TextField tinPart2,
                TextField tinPart3,
                TextField tinPart4,
                TextField pagIbigField,
                ComboBox<String> statusCombo,
                TextField positionField,
                ComboBox<String> departmentCombo,
                ComboBox<String> supervisorCombo,
                TextField basicSalaryField,
                TextField riceSubsidyField,
                TextField phoneAllowanceField,
                TextField clothingAllowanceField
        ) {
            this.employeeNumberField = employeeNumberField;
            this.firstNameField = firstNameField;
            this.lastNameField = lastNameField;
            this.birthdayPicker = birthdayPicker;
            this.addressField = addressField;
            this.phoneField = phoneField;
            this.sssPart1 = sssPart1;
            this.sssPart2 = sssPart2;
            this.sssPart3 = sssPart3;
            this.philHealthField = philHealthField;
            this.tinPart1 = tinPart1;
            this.tinPart2 = tinPart2;
            this.tinPart3 = tinPart3;
            this.tinPart4 = tinPart4;
            this.pagIbigField = pagIbigField;
            this.statusCombo = statusCombo;
            this.positionField = positionField;
            this.departmentCombo = departmentCombo;
            this.supervisorCombo = supervisorCombo;
            this.basicSalaryField = basicSalaryField;
            this.riceSubsidyField = riceSubsidyField;
            this.phoneAllowanceField = phoneAllowanceField;
            this.clothingAllowanceField = clothingAllowanceField;
        }
    }
}
