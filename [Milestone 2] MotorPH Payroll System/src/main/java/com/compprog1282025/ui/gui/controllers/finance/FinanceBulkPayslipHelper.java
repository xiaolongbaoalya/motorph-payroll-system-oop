package com.compprog1282025.ui.gui.controllers.finance;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.PayslipPdfService;
import com.compprog1282025.service.dto.PayslipComputation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FinanceBulkPayslipHelper {
    private final EmployeeService employeeService;
    private final FinanceService financeService;
    private final PayslipPdfService payslipPdfService;
    private final Supplier<com.compprog1282025.model.user.Session> sessionSupplier;
    private final Supplier<Boolean> hasFinanceAccessSupplier;
    private final Consumer<String[]> alertCallback;

    private final RadioButton rbScopeAll;
    private final RadioButton rbScopeSelected;
    private final TableView<Employee> bulkEmployeesTable;
    private final ProgressBar progressBar;
    private final Label statusLabel;
    private final Label selectionHintLabel;
    private final Button startButton;
    private final Button clearSelectionButton;

    private final Map<Integer, BooleanProperty> selectionMap = new LinkedHashMap<>();
    private Task<BulkGenerationResult> bulkTask;

    public FinanceBulkPayslipHelper(
            EmployeeService employeeService,
            FinanceService financeService,
            PayslipPdfService payslipPdfService,
            Supplier<com.compprog1282025.model.user.Session> sessionSupplier,
            Supplier<Boolean> hasFinanceAccessSupplier,
            Consumer<String[]> alertCallback,
            RadioButton rbScopeAll,
            RadioButton rbScopeSelected,
            TableView<Employee> bulkEmployeesTable,
            ProgressBar progressBar,
            Label statusLabel,
            Label selectionHintLabel,
            Button startButton,
            Button clearSelectionButton
    ) {
        this.employeeService = employeeService;
        this.financeService = financeService;
        this.payslipPdfService = payslipPdfService;
        this.sessionSupplier = sessionSupplier;
        this.hasFinanceAccessSupplier = hasFinanceAccessSupplier;
        this.alertCallback = alertCallback;
        this.rbScopeAll = rbScopeAll;
        this.rbScopeSelected = rbScopeSelected;
        this.bulkEmployeesTable = bulkEmployeesTable;
        this.progressBar = progressBar;
        this.statusLabel = statusLabel;
        this.selectionHintLabel = selectionHintLabel;
        this.startButton = startButton;
        this.clearSelectionButton = clearSelectionButton;
    }

    public void setupControls() {
        if (rbScopeAll == null || rbScopeSelected == null || bulkEmployeesTable == null) {
            return;
        }

        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        rbScopeAll.setToggleGroup(group);
        rbScopeSelected.setToggleGroup(group);
        rbScopeAll.setSelected(true);

        bulkEmployeesTable.getColumns().clear();
        selectionMap.clear();

        TableColumn<Employee, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(cell -> {
            int empNo = cell.getValue().getEmployeeNumber();
            selectionMap.putIfAbsent(empNo, new SimpleBooleanProperty(false));
            return selectionMap.get(empNo);
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);

        TableColumn<Employee, String> empNoCol = new TableColumn<>("Employee #");
        empNoCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEmployeeNumber())));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastName() + ", " + cell.getValue().getFirstName()));

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPosition() != null ? cell.getValue().getPosition().getDepartment() : "N/A"));

        bulkEmployeesTable.getColumns().setAll(selectCol, empNoCol, nameCol, deptCol);
        bulkEmployeesTable.setEditable(true);
        bulkEmployeesTable.setItems(FXCollections.observableArrayList(
                employeeService.getAllEmployees(sessionSupplier.get()).stream()
                        .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                        .toList()
        ));

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> updateSelectionModeState());
        updateSelectionModeState();

        if (progressBar != null) {
            progressBar.setProgress(0);
        }
        if (statusLabel != null) {
            statusLabel.setText("Ready for payslip generation.");
        }
    }

    public void clearSelection() {
        selectionMap.values().forEach(property -> property.set(false));
        if (bulkEmployeesTable != null) {
            bulkEmployeesTable.refresh();
        }
    }

    public void startBulkGeneration(Integer year, int month) {
        if (!hasFinanceAccessSupplier.get()) {
            showAlert("Access Denied", "Only Finance users can generate bulk payslips.");
            return;
        }
        if (bulkTask != null && bulkTask.isRunning()) {
            showAlert("Bulk Generation", "A bulk generation task is already running.");
            return;
        }
        if (year == null || month <= 0) {
            showAlert("Validation", "Select month and year before bulk generation.");
            return;
        }

        List<Employee> targets = getTargetEmployees();
        if (targets.isEmpty()) {
            showAlert("Validation", "No employees selected for bulk generation.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Bulk Payslip Generation");
        confirm.setHeaderText("Generate bulk payslips for " + targets.size() + " employee(s)?");
        confirm.setContentText("Employees without valid payroll data will be skipped and included in the summary.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        bulkTask = createBulkPayslipTask(targets, year, month);
        bindBulkTaskUi(bulkTask);
        new Thread(bulkTask, "finance-bulk-payslip-task").start();
    }

    private void updateSelectionModeState() {
        boolean selectedMode = rbScopeSelected != null && rbScopeSelected.isSelected();
        if (bulkEmployeesTable != null) {
            bulkEmployeesTable.setDisable(!selectedMode);
        }
        if (clearSelectionButton != null) {
            clearSelectionButton.setDisable(!selectedMode);
        }
        if (selectionHintLabel != null) {
            selectionHintLabel.setText(selectedMode
                    ? "Tick checkboxes to choose employees for generation."
                    : "All Employees mode will generate payslips for the whole list.");
        }
        if (!selectedMode) {
            clearSelection();
        }
    }

    private void bindBulkTaskUi(Task<BulkGenerationResult> task) {
        if (progressBar != null) {
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(task.progressProperty());
        }
        if (statusLabel != null) {
            statusLabel.textProperty().unbind();
            statusLabel.textProperty().bind(task.messageProperty());
        }
        if (startButton != null) {
            startButton.setDisable(true);
        }

        task.setOnSucceeded(e -> finalizeBulkTask(task.getValue(), task.isCancelled()));
        task.setOnCancelled(e -> finalizeBulkTask(task.getValue(), true));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            finalizeBulkTask(new BulkGenerationResult(), true);
            showAlert("Bulk Generation Error", ex == null ? "Unknown error" : ex.getMessage());
        });
    }

    private void finalizeBulkTask(BulkGenerationResult result, boolean cancelled) {
        if (progressBar != null) {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(cancelled ? 0 : 1);
        }
        if (statusLabel != null) {
            statusLabel.textProperty().unbind();
            statusLabel.setText(cancelled ? "Bulk generation cancelled." : "Bulk generation completed.");
        }
        if (startButton != null) {
            startButton.setDisable(false);
        }
        showBulkSummary(result, cancelled);
        bulkTask = null;
    }

    private Task<BulkGenerationResult> createBulkPayslipTask(List<Employee> employees, int year, int month) {
        return new Task<>() {
            @Override
            protected BulkGenerationResult call() {
                BulkGenerationResult result = new BulkGenerationResult();
                result.totalEmployees = employees.size();
                result.outputFolder = Paths.get("generated", "payslips", String.format("%04d-%02d", year, month));

                for (int i = 0; i < employees.size(); i++) {
                    if (isCancelled()) {
                        updateMessage("Bulk generation cancelled.");
                        break;
                    }

                    Employee employee = employees.get(i);
                    result.processedEmployees++;
                    updateMessage("Generating " + (i + 1) + " / " + employees.size() + " - " + employee.getEmployeeNumber());

                    try {
                        PayslipComputation computation = financeService.computePayslip(sessionSupplier.get(), employee, year, month);
                        if (computation == null) {
                            result.addSkipped(employee, "No payroll record for selected period");
                        } else {
                            Path generated = payslipPdfService.generatePayslipPdf(employee, computation);
                            result.generatedPdfs++;
                            result.outputFolder = generated.getParent();
                        }
                    } catch (NullPointerException ex) {
                        result.addSkipped(employee, "Incomplete payroll data");
                    } catch (Exception ex) {
                        result.addSkipped(employee, "Generation error: " + ex.getMessage());
                    }

                    updateProgress(i + 1, employees.size());
                }

                updateMessage("Bulk generation completed.");
                return result;
            }
        };
    }

    private List<Employee> getTargetEmployees() {
        if (rbScopeSelected != null && rbScopeSelected.isSelected()) {
            if (bulkEmployeesTable == null) {
                return List.of();
            }
            return bulkEmployeesTable.getItems().stream()
                    .filter(emp -> selectionMap.containsKey(emp.getEmployeeNumber())
                            && selectionMap.get(emp.getEmployeeNumber()).get())
                    .toList();
        }
        return employeeService.getAllEmployees(sessionSupplier.get()).stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .toList();
    }

    private void showBulkSummary(BulkGenerationResult result, boolean cancelled) {
        if (result == null) {
            return;
        }
        StringBuilder body = new StringBuilder();
        body.append("Total employees targeted: ").append(result.totalEmployees).append("\n");
        body.append("Total employees processed: ").append(result.processedEmployees).append("\n");
        body.append("Total PDFs generated: ").append(result.generatedPdfs).append("\n");
        body.append("Total skipped employees: ").append(result.skippedReasons.size()).append("\n");
        if (cancelled) {
            body.append("Status: Cancelled\n");
        }

        if (!result.skippedReasons.isEmpty()) {
            body.append("\nSkipped Employees:\n");
            result.skippedReasons.forEach((k, v) -> body.append("- ").append(k).append(": ").append(v).append("\n"));
        }

        Alert summary = new Alert(Alert.AlertType.INFORMATION);
        summary.setTitle("Bulk Generation Summary");
        summary.setHeaderText("Payslip Bulk Generation Result");
        TextArea details = new TextArea(body.toString());
        details.setEditable(false);
        details.setWrapText(true);
        details.setPrefWidth(520);
        details.setPrefHeight(280);
        summary.getDialogPane().setContent(details);
        ButtonType openFolderBtn = new ButtonType("Open Output Folder", ButtonBar.ButtonData.LEFT);
        summary.getButtonTypes().setAll(openFolderBtn, ButtonType.OK);
        ButtonType choice = summary.showAndWait().orElse(ButtonType.OK);
        if (choice == openFolderBtn) {
            openOutputFolder(result.outputFolder);
        }
    }

    private void openOutputFolder(Path folderPath) {
        try {
            if (folderPath == null || !Files.exists(folderPath)) {
                showAlert("Open Output Folder", "Output folder was not found.");
                return;
            }
            Desktop.getDesktop().open(folderPath.toFile());
        } catch (Exception ex) {
            showAlert("Open Output Folder", "Unable to open folder: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        alertCallback.accept(new String[]{title, message});
    }

    private static class BulkGenerationResult {
        int totalEmployees;
        int processedEmployees;
        int generatedPdfs;
        Path outputFolder;
        final Map<String, String> skippedReasons = new LinkedHashMap<>();

        void addSkipped(Employee employee, String reason) {
            String key = employee.getEmployeeNumber() + " - " + employee.getLastName() + ", " + employee.getFirstName();
            skippedReasons.put(key, reason);
        }
    }
}
