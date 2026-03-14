package com.compprog1282025.ui.gui.controllers.finance;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.dto.PayslipComputation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FinanceHistoryHelper {
    private final FinanceService financeService;
    private final EmployeeService employeeService;
    private final Supplier<Session> sessionSupplier;
    private final Supplier<Boolean> hasFinanceAccessSupplier;
    private final BiConsumer<String, String> alertCallback;

    public FinanceHistoryHelper(
            FinanceService financeService,
            EmployeeService employeeService,
            Supplier<Session> sessionSupplier,
            Supplier<Boolean> hasFinanceAccessSupplier,
            BiConsumer<String, String> alertCallback
    ) {
        this.financeService = financeService;
        this.employeeService = employeeService;
        this.sessionSupplier = sessionSupplier;
        this.hasFinanceAccessSupplier = hasFinanceAccessSupplier;
        this.alertCallback = alertCallback;
    }

    public void setupHistoryTable(TableView<Payslip> table) {
        if (table == null) {
            return;
        }

        table.getColumns().clear();

        TableColumn<Payslip, String> empNoCol = new TableColumn<>("Employee #");
        empNoCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEmployeeNumber())));

        TableColumn<Payslip, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(c -> {
            Employee employee = employeeService.getEmployee(c.getValue().getEmployeeNumber());
            String name = employee == null ? "Unknown" : employee.getLastName() + ", " + employee.getFirstName();
            return new SimpleStringProperty(name);
        });

        TableColumn<Payslip, String> periodCol = new TableColumn<>("Payroll Period");
        periodCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getYearMonth().toString()));

        TableColumn<Payslip, String> grossCol = new TableColumn<>("Gross Pay");
        grossCol.setCellValueFactory(c -> new SimpleStringProperty(formatCurrency(c.getValue().getGrossSalary())));

        TableColumn<Payslip, String> deductionsCol = new TableColumn<>("Total Deductions");
        deductionsCol.setCellValueFactory(c -> {
            Payslip p = c.getValue();
            double total = p.getSss() + p.getPhilhealth() + p.getPagibig() + p.getWithholding();
            return new SimpleStringProperty(formatCurrency(total));
        });

        TableColumn<Payslip, String> netCol = new TableColumn<>("Net Pay");
        netCol.setCellValueFactory(c -> new SimpleStringProperty(formatCurrency(c.getValue().getNetSalary())));

        TableColumn<Payslip, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty("Computed"));

        table.getColumns().setAll(empNoCol, nameCol, periodCol, grossCol, deductionsCol, netCol, statusCol);
        table.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Payslip> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openPayrollDetailPopup(row.getItem());
                }
            });
            return row;
        });
    }

    public void loadPayrollHistory(Integer year, int month, TableView<Payslip> table) {
        if (!hasFinanceAccessSupplier.get()) {
            showAlert("Access Denied", "Only Finance users can access Finance panes.");
            return;
        }
        if (table == null) {
            return;
        }
        if (year == null || month < 1) {
            showAlert("Validation Error", "Please choose month and year first.");
            return;
        }

        List<Payslip> history = employeeService.getAllEmployees(sessionSupplier.get()).stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .map(employee -> financeService.computePayslip(sessionSupplier.get(), employee, year, month))
                .filter(Objects::nonNull)
                .map(PayslipComputation::getPayslip)
                .toList();

        table.setItems(FXCollections.observableArrayList(history));
    }

    public void openPayslipPreview(Employee employee, PayslipComputation computation, Path outputPath) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Payslip Preview - " + employee.getEmployeeNumber());

        VBox root = new VBox(10);
        root.setPadding(new Insets(14));

        Label title = new Label("Payslip Preview");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane employeeInfo = new GridPane();
        employeeInfo.setHgap(12);
        employeeInfo.setVgap(8);
        addRow(employeeInfo, 0, "Employee #", new Label(String.valueOf(employee.getEmployeeNumber())));
        addRow(employeeInfo, 1, "Employee Name", new Label(employee.getLastName() + ", " + employee.getFirstName()));
        addRow(employeeInfo, 2, "Payroll Period", new Label(computation.getPayslip().getYearMonth().toString()));
        addRow(employeeInfo, 3, "Position", new Label(employee.getPosition().getJobTitle()));
        addRow(employeeInfo, 4, "Department", new Label(employee.getPosition().getDepartment()));

        GridPane details = new GridPane();
        details.setHgap(12);
        details.setVgap(8);
        int row = 0;
        addRow(details, row++, "Basic Salary", new Label(formatCurrency(computation.getBasicSalary())));
        addRow(details, row++, "Rice Subsidy", new Label(formatCurrency(computation.getRiceSubsidy())));
        addRow(details, row++, "Phone Allowance", new Label(formatCurrency(computation.getPhoneAllowance())));
        addRow(details, row++, "Clothing Allowance", new Label(formatCurrency(computation.getClothingAllowance())));
        addRow(details, row++, "SSS", new Label(formatCurrency(computation.getPayslip().getSss())));
        addRow(details, row++, "PhilHealth", new Label(formatCurrency(computation.getPayslip().getPhilhealth())));
        addRow(details, row++, "Pag-IBIG", new Label(formatCurrency(computation.getPayslip().getPagibig())));
        addRow(details, row++, "Withholding Tax", new Label(formatCurrency(computation.getPayslip().getWithholding())));
        addRow(details, row++, "Total Earnings", new Label(formatCurrency(computation.getTotalEarnings())));
        addRow(details, row++, "Total Deductions", new Label(formatCurrency(computation.getTotalDeductions())));

        Label net = new Label("Final Net Pay: " + formatCurrency(computation.getPayslip().getNetSalary()));
        net.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");

        Label pathLabel = new Label("PDF saved to: " + outputPath);
        pathLabel.setWrapText(true);

        ScrollPane scroll = new ScrollPane(new VBox(12,
                new Label("Employee Information"), employeeInfo,
                new Separator(), new Label("Payroll Details"), details,
                new Separator(), net, pathLabel
        ));
        scroll.setFitToWidth(true);

        Button openFile = new Button("Open File");
        openFile.getStyleClass().add("success-button");
        openFile.setOnAction(e -> openPdfFile(outputPath));

        Button close = new Button("Close");
        close.getStyleClass().add("primary-button");
        close.setOnAction(e -> stage.close());

        Button openFolder = new Button("Open Output Folder");
        openFolder.getStyleClass().add("primary-button");
        openFolder.setOnAction(e -> openOutputFolder(outputPath.getParent()));

        HBox actions = new HBox(10, openFile, openFolder, close);
        root.getChildren().addAll(title, scroll, actions);
        stage.setScene(new Scene(root, 620, 640));
        stage.showAndWait();
    }

    public void openPayrollDetailPopup(Payslip payslip) {
        Employee employee = employeeService.getEmployee(payslip.getEmployeeNumber());
        if (employee == null) {
            showAlert("Missing Data", "Employee record not found for selected payroll row.");
            return;
        }

        PayslipComputation computation;
        try {
            computation = financeService.computePayslip(
                    sessionSupplier.get(),
                    employee,
                    payslip.getYearMonth().getYear(),
                    payslip.getYearMonth().getMonthValue()
            );
        } catch (Exception e) {
            showAlert("Access Denied", e.getMessage());
            return;
        }
        if (computation == null) {
            showAlert("Missing Data", "Unable to load payroll details for selected record.");
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Payroll Details - " + employee.getEmployeeNumber());

        GridPane details = new GridPane();
        details.setHgap(12);
        details.setVgap(8);
        details.setPadding(new Insets(14));

        int row = 0;
        addRow(details, row++, "Employee #", new Label(String.valueOf(employee.getEmployeeNumber())));
        addRow(details, row++, "Employee Name", new Label(employee.getLastName() + ", " + employee.getFirstName()));
        addRow(details, row++, "Payroll Period", new Label(computation.getPayslip().getYearMonth().toString()));
        addRow(details, row++, "Basic Salary", new Label(formatCurrency(computation.getBasicSalary())));
        addRow(details, row++, "Rice Subsidy", new Label(formatCurrency(computation.getRiceSubsidy())));
        addRow(details, row++, "Phone Allowance", new Label(formatCurrency(computation.getPhoneAllowance())));
        addRow(details, row++, "Clothing Allowance", new Label(formatCurrency(computation.getClothingAllowance())));
        addRow(details, row++, "Gross Pay", new Label(formatCurrency(computation.getTotalEarnings())));
        addRow(details, row++, "SSS", new Label(formatCurrency(computation.getPayslip().getSss())));
        addRow(details, row++, "PhilHealth", new Label(formatCurrency(computation.getPayslip().getPhilhealth())));
        addRow(details, row++, "Pag-IBIG", new Label(formatCurrency(computation.getPayslip().getPagibig())));
        addRow(details, row++, "Withholding Tax", new Label(formatCurrency(computation.getPayslip().getWithholding())));
        addRow(details, row++, "Total Deductions", new Label(formatCurrency(computation.getTotalDeductions())));
        addRow(details, row++, "Net Pay", new Label(formatCurrency(computation.getPayslip().getNetSalary())));
        addRow(details, row, "Status", new Label("Computed"));

        ScrollPane scroll = new ScrollPane(details);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 560, 600));
        stage.showAndWait();
    }

    private void openPdfFile(Path outputPath) {
        try {
            if (outputPath == null || !Files.exists(outputPath)) {
                showAlert("Open File", "Generated file was not found.");
                return;
            }
            Desktop.getDesktop().open(outputPath.toFile());
        } catch (Exception ex) {
            showAlert("Open File", "Unable to open file: " + ex.getMessage());
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

    private String formatCurrency(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    private void addRow(GridPane form, int row, String label, Label node) {
        form.add(new Label(label + ":"), 0, row);
        form.add(node, 1, row);
    }

    private void showAlert(String title, String message) {
        alertCallback.accept(title, message);
    }
}
