package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.employee.Payslip;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.MonetaryInputParser;
import com.compprog1282025.service.PayslipPdfService;
import com.compprog1282025.service.dto.PayslipComputation;
import com.compprog1282025.service.RequestService;
import com.compprog1282025.ui.gui.util.SessionContext;
import com.compprog1282025.ui.gui.controllers.dashboard.AttendanceSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.PayslipSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.ProfileSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.RequestSectionController;
import com.compprog1282025.ui.gui.util.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.awt.Desktop;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final String SUPERVISOR_NONE = "None";
    private static final String SUPERVISOR_BASE_OPTIONS_KEY = "supervisorBaseOptions";
    private static final String SUPERVISOR_AUTOCOMPLETE_KEY = "supervisorAutocompleteConfigured";
    private static final String SUPERVISOR_UPDATING_KEY = "supervisorUpdating";
    private static final String HR_HISTORY_EMP_BASE_OPTIONS_KEY = "hrHistoryEmployeeBaseOptions";
    private static final String HR_HISTORY_EMP_AUTOCOMPLETE_KEY = "hrHistoryEmployeeAutocompleteConfigured";
    private static final String HR_HISTORY_EMP_UPDATING_KEY = "hrHistoryEmployeeUpdating";


    @FXML private Label lblWelcome, lblEmpId, lblFullName, lblDate;
    @FXML private Label lblTotalHours;
    @FXML private VBox paneAttendance, paneProfile, panePayslip;
    @FXML private StackPane contentArea;
    @FXML private TableView<Attendance> tblAttendance;
    @FXML private ComboBox<String> cmbMonth, cmbPayslipMonth;
    @FXML private ComboBox<Integer> cmbYear, cmbPayslipYear;
    @FXML private Label lblNetPay, lblGrossIncome;
    @FXML private Label lblBasicSalary, lblRiceSubsidy, lblPhoneAllowance, lblClothingAllowance;
    @FXML private Label lblSSS, lblPhilHealth, lblPagIbig, lblTax;
    @FXML private Label lblTotalEarnings, lblTotalContribs, lblTotalTax, lblFinalNetPay;
    @FXML private VBox paneRequests;
    @FXML private ComboBox<String> cmbLeaveType;
    @FXML private VBox vboxLeaveFields;
    @FXML private DatePicker dpLeaveFrom, dpLeaveTo;
    
    @FXML private TextArea txtRequestReason;
    @FXML private Label lblFileName;
    @FXML private TableView<Request> tblRequestHistory;
    @FXML private Button btnSubmitRequest;
    @FXML private Label lblProfileId, lblProfileName, lblProfileBirthday;
    @FXML private Label lblProfilePhone, lblProfileAddress;
    @FXML private Label lblProfilePosition, lblProfileDepartment, lblProfileStatus, lblProfileSupervisor;
    @FXML private Label lblProfileSSS, lblProfilePhilHealth, lblProfileTIN, lblProfilePagIbig;

    @FXML private TitledPane panePersonalMenu;
    @FXML private TitledPane paneHrMenu;
    @FXML private TitledPane paneFinanceMenu;

    @FXML private VBox paneFinGeneratePayslip, paneFinPayrollHistory;
    @FXML private VBox paneHrTeamAttendance;
    @FXML private VBox paneHrTeamRequests;
    @FXML private VBox paneHrAddEmployee;
    @FXML private VBox paneHrEmployeeDetails;

    @FXML private TextField txtHrEmpNo, txtHrLastName, txtHrFirstName, txtHrPhone;
    @FXML private TextArea txtHrAddress;
    @FXML private TextField txtHrSss, txtHrPhilHealth, txtHrTin, txtHrPagIbig;
    @FXML private ComboBox<String> cmbHrStatus;
    @FXML private TextField txtHrPosition;
    @FXML private ComboBox<String> cmbHrDepartment;
    @FXML private ComboBox<String> cmbHrSupervisor;
    @FXML private TextField txtHrBasicSalary, txtHrRiceSubsidy, txtHrPhoneAllowance, txtHrClothingAllowance;
    @FXML private DatePicker dpHrBirthday;

    @FXML private TableView<Employee> tblHrEmployeeDirectory;
    @FXML private Label lblHrEmpNo, lblHrEmpName, lblHrEmpBirthday, lblHrEmpDepartment, lblHrEmpPosition, lblHrEmpStatus;
    @FXML private ComboBox<String> cmbHrTeamMonth;
    @FXML private ComboBox<Integer> cmbHrTeamYear;
    @FXML private TableView<Attendance> tblHrTeamAttendance;
    @FXML private ComboBox<String> cmbHrHistoryEmployee;
    @FXML private DatePicker dpHrHistoryFrom, dpHrHistoryTo;
    @FXML private TableView<Attendance> tblHrEmployeeHistory;
    @FXML private TableView<Request> tblHrPendingLeave, tblHrRequestHistory;
    @FXML private ComboBox<String> cmbFinMonth, cmbFinHistoryMonth;
    @FXML private ComboBox<Integer> cmbFinYear, cmbFinHistoryYear;
    @FXML private TableView<Payslip> tblFinPayrollHistory;
    @FXML private RadioButton rbFinScopeAll, rbFinScopeSelected;
    @FXML private TableView<Employee> tblFinBulkEmployees;
    @FXML private ProgressBar progressFinBulk;
    @FXML private Label lblFinBulkStatus, lblFinSelectionHint;
    @FXML private Button btnFinBulkStart, btnFinBulkCancel, btnFinClearSelection;
    @FXML private Button btnHrDeleteEmployee;

    private AttendanceSectionController attendanceSectionController;
    private PayslipSectionController payslipSectionController;
    private ProfileSectionController profileSectionController;
    private RequestSectionController requestSectionController;
    private EmployeeService employeeService;
    private AttendanceService hrAttendanceService;
    private RequestService hrRequestService;
    private FinanceService financeService;
    private PayslipPdfService payslipPdfService;
    private Task<BulkGenerationResult> financeBulkTask;
    private final Map<Integer, BooleanProperty> financeBulkSelection = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AttendanceService attendanceService = new AttendanceService(new AttendanceDAO());
        this.financeService = new FinanceService(attendanceService);
        this.payslipPdfService = new PayslipPdfService();
        this.employeeService = new EmployeeService(new EmployeeDAO());
        this.hrAttendanceService = attendanceService;
        this.hrRequestService = new RequestService();

        attendanceSectionController = new AttendanceSectionController(attendanceService, tblAttendance, cmbMonth, cmbYear, lblTotalHours);
        payslipSectionController = new PayslipSectionController(
                this.financeService,
                cmbPayslipMonth,
                cmbPayslipYear,
                lblNetPay,
                lblGrossIncome,
                lblBasicSalary,
                lblRiceSubsidy,
                lblPhoneAllowance,
                lblClothingAllowance,
                lblSSS,
                lblPhilHealth,
                lblPagIbig,
                lblTax,
                lblTotalEarnings,
                lblTotalContribs,
                lblTotalTax,
                lblFinalNetPay
        );
        profileSectionController = new ProfileSectionController(
                lblWelcome,
                lblFullName,
                lblEmpId,
                lblProfileId,
                lblProfileName,
                lblProfileBirthday,
                lblProfilePhone,
                lblProfileAddress,
                lblProfilePosition,
                lblProfileDepartment,
                lblProfileStatus,
                lblProfileSupervisor,
                lblProfileSSS,
                lblProfilePhilHealth,
                lblProfileTIN,
                lblProfilePagIbig
        );
        requestSectionController = new RequestSectionController(
                hrRequestService,
                cmbLeaveType,
                dpLeaveFrom,
                dpLeaveTo,
                txtRequestReason,
                tblRequestHistory,
                btnSubmitRequest,
                this::showStyledAlert
        );

        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        profileSectionController.loadUserData(currentSession());
        configureRoleMenus();

        populateFilters();
        attendanceSectionController.setupAttendanceTable();
        setupHrEmployeeDirectoryTable();
        if (hasHrAccess()) {
            setupHrAddForm();
            setupHrTeamAttendancePane();
            setupHrTeamRequestPane();
        }
        if (hasFinanceAccess()) {
            setupFinancePanes();
        }

        showAttendance();
        attendanceSectionController.refreshAttendanceData(currentSession());
        payslipSectionController.refreshPayslipData(currentSession());
    }

    private void configureRoleMenus() {
        boolean isHr = hasHrAccess();
        boolean isFinance = hasFinanceAccess();
        if (paneHrMenu != null) {
            paneHrMenu.setVisible(isHr);
            paneHrMenu.setManaged(isHr);
            paneHrMenu.setExpanded(false);
        }
        if (paneFinanceMenu != null) {
            paneFinanceMenu.setVisible(isFinance);
            paneFinanceMenu.setManaged(isFinance);
            paneFinanceMenu.setExpanded(false);
        }
        if (panePersonalMenu != null) {
            panePersonalMenu.setExpanded(true);
        }
    }

    private void configureHrEmployeeActionButtons() {
        boolean isAdmin = hasAdminAccess();
        if (btnHrDeleteEmployee != null) {
            btnHrDeleteEmployee.setVisible(isAdmin);
            btnHrDeleteEmployee.setManaged(isAdmin);
            btnHrDeleteEmployee.setDisable(!isAdmin);
        }
    }
    private void populateFilters() {
        var months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        cmbMonth.setItems(months);
        cmbMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        cmbPayslipMonth.setItems(months);
        cmbPayslipMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        cmbYear.getItems().clear();
        cmbPayslipYear.getItems().clear();
        for (int year = 2020; year <= currentYear; year++) {
            cmbYear.getItems().add(year);
            cmbPayslipYear.getItems().add(year);
        }

        cmbYear.setValue(currentYear);
        cmbPayslipYear.setValue(currentYear);

        if (cmbFinMonth != null) cmbFinMonth.setItems(FXCollections.observableArrayList(months));
        if (cmbFinHistoryMonth != null) cmbFinHistoryMonth.setItems(FXCollections.observableArrayList(months));
        if (cmbFinYear != null) {
            cmbFinYear.getItems().setAll(cmbPayslipYear.getItems());
            cmbFinYear.setValue(currentYear);
        }
        if (cmbFinHistoryYear != null) {
            cmbFinHistoryYear.getItems().setAll(cmbPayslipYear.getItems());
            cmbFinHistoryYear.setValue(currentYear);
        }
        if (cmbFinMonth != null) cmbFinMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        if (cmbFinHistoryMonth != null) cmbFinHistoryMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        cmbMonth.setOnAction(e -> attendanceSectionController.refreshAttendanceData(currentSession()));
        cmbYear.setOnAction(e -> attendanceSectionController.refreshAttendanceData(currentSession()));

        cmbPayslipMonth.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!Objects.equals(oldVal, newVal)) {
                payslipSectionController.refreshPayslipData(currentSession());
            }
        });
        cmbPayslipYear.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!Objects.equals(oldVal, newVal)) {
                payslipSectionController.refreshPayslipData(currentSession());
            }
        });
    }

    @FXML
    private void handleDownloadPayslip() {
        try {
            Session session = currentSession();
            if (session == null || session.getUser() == null) {
                showStyledAlert("PDF Generator", "No active session.");
                return;
            }
            int year = cmbPayslipYear.getValue();
            int month = cmbPayslipMonth.getSelectionModel().getSelectedIndex() + 1;
            PayslipComputation computation = financeService.computePayslip(currentSession(), session.getUser().getEmployee(), year, month);
            if (computation == null) {
                showStyledAlert("PDF Generator", "No payslip data found for this period.");
                return;
            }
            Path path = payslipPdfService.generatePayslipPdf(session.getUser().getEmployee(), computation);
            showStyledAlert("PDF Generated", "Saved to: " + path.toString());
        } catch (Exception ex) {
            showStyledAlert("PDF Generator", ex.getMessage());
        }
    }

    @FXML
    private void showAttendance() {
        hideAllPanes();
        paneAttendance.setVisible(true);
        paneAttendance.setManaged(true);
        attendanceSectionController.refreshAttendanceData(currentSession());
    }

    @FXML
    private void showAboutMe() {
        hideAllPanes();
        paneProfile.setVisible(true);
        paneProfile.setManaged(true);
    }

    @FXML
    private void showPayslip() {
        hideAllPanes();
        panePayslip.setVisible(true);
        panePayslip.setManaged(true);
        payslipSectionController.refreshPayslipData(currentSession());
    }

    @FXML
    private void showRequests() {
        hideAllPanes();
        paneRequests.setVisible(true);
        paneRequests.setManaged(true);
        requestSectionController.prepareForDisplay(currentSession());
    }

    @FXML
    private void showHrTeamAttendance() {
        if (!hasHrAccess()) {
            showStyledAlert("Access Denied", "Only HR users can access HR panes.");
            return;
        }
        hideAllPanes();
        paneHrTeamAttendance.setVisible(true);
        paneHrTeamAttendance.setManaged(true);
        handleHrLoadTeamAttendance();
    }

    @FXML
    private void showHrTeamRequests() {
        if (!hasHrAccess()) {
            showStyledAlert("Access Denied", "Only HR users can access HR panes.");
            return;
        }
        hideAllPanes();
        paneHrTeamRequests.setVisible(true);
        paneHrTeamRequests.setManaged(true);
        loadHrTeamRequestData();
    }

    @FXML
    private void showHrAddEmployee() {
        if (!hasHrAccess()) {
            showStyledAlert("Access Denied", "Only HR users can access HR panes.");
            return;
        }
        hideAllPanes();
        paneHrAddEmployee.setVisible(true);
        paneHrAddEmployee.setManaged(true);
        populateSupervisorOptions(cmbHrSupervisor, null);
    }

    @FXML
    private void showHrEmployeeDetails() {
        if (!hasHrAccess()) {
            showStyledAlert("Access Denied", "Only HR users can access HR panes.");
            return;
        }
        hideAllPanes();
        paneHrEmployeeDetails.setVisible(true);
        paneHrEmployeeDetails.setManaged(true);
        loadHrEmployeeDirectory();
    }

    private void hideAllPanes() {
        List<VBox> panes = List.of(
                paneAttendance, paneProfile, panePayslip, paneRequests,
                paneFinGeneratePayslip, paneFinPayrollHistory,
                paneHrTeamAttendance, paneHrTeamRequests, paneHrAddEmployee, paneHrEmployeeDetails
        );
        for (VBox pane : panes) {
            if (pane != null) {
                pane.setVisible(false);
                pane.setManaged(false);
            }
        }
    }

    @FXML
    private void handleTimeIn(ActionEvent event) {
        try {
            attendanceSectionController.timeIn(currentSession());
            showStyledAlert("Success", "Timed In Successfully");
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleTimeOut(ActionEvent event) {
        try {
            attendanceSectionController.timeOut(currentSession());
            showStyledAlert("Success", "Timed Out Successfully");
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionContext.getInstance().clear();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchTo(stage, "/com/compprog1282025/ui/gui/views/auth/Login.fxml");
    }

    @FXML
    private void refreshPayslipData() {
        payslipSectionController.refreshPayslipData(currentSession());
    }

    @FXML
    private void handleSubmitRequest() {
        requestSectionController.submitRequest(currentSession());
    }

    @FXML
    private void handleViewMyRequestDetails() {
        requestSectionController.viewSelectedRequestDetails(currentSession());
    }

    @FXML
    private void handleHrLoadTeamAttendance() {
        if (cmbHrTeamMonth == null || cmbHrTeamYear == null || tblHrTeamAttendance == null) {
            return;
        }
        int month = cmbHrTeamMonth.getSelectionModel().getSelectedIndex() + 1;
        Integer year = cmbHrTeamYear.getValue();
        if (month <= 0 || year == null) {
            return;
        }
        List<Attendance> rows = hrAttendanceService.getTeamAttendance(month, year, currentSession());
        tblHrTeamAttendance.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void handleHrLoadEmployeeHistory() {
        if (cmbHrHistoryEmployee == null || tblHrEmployeeHistory == null) {
            return;
        }

        Integer employeeNumber = resolveSelectedHistoryEmployeeNumber();
        if (employeeNumber == null) {
            showStyledAlert("Validation", "Please select a valid employee.");
            return;
        }

        try {
            List<Attendance> rows = hrAttendanceService.getEmployeeAttendanceHistory(
                    employeeNumber,
                    dpHrHistoryFrom != null ? dpHrHistoryFrom.getValue() : null,
                    dpHrHistoryTo != null ? dpHrHistoryTo.getValue() : null,
                    currentSession()
            );
            tblHrEmployeeHistory.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            showStyledAlert("Access Denied", e.getMessage());
        }
    }

    @FXML
    private void handleHrApproveRequest() {
        Request selected = getSelectedHrRequest();
        if (selected == null) {
            showStyledAlert("Selection Required", "Select a pending request first.");
            return;
        }
        try {
            hrRequestService.approveRequest(selected.getRequestId(), currentSession());
            loadHrTeamRequestData();
            showStyledAlert("Success", "Request approved.");
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleHrRejectRequest() {
        Request selected = getSelectedHrRequest();
        if (selected == null) {
            showStyledAlert("Selection Required", "Select a pending request first.");
            return;
        }
        try {
            hrRequestService.rejectRequest(selected.getRequestId(), currentSession());
            loadHrTeamRequestData();
            showStyledAlert("Success", "Request rejected.");
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleHrViewRequestDetails() {
        Request selected = getSelectedHrRequestForDetails();
        if (selected == null) {
            showStyledAlert("Selection Required", "Select a request first.");
            return;
        }
        openHrRequestDetailsWindow(selected);
    }
    @FXML
    private void handleHrAddEmployee() {
        if (!hasHrAccess()) {
            showStyledAlert("Access Denied", "Only HR users can add employees.");
            return;
        }
        try {
            Employee employee = buildEmployeeFromAddForm();
            employeeService.createEmployee(employee, currentSession());
            showStyledAlert("Success", "Employee added successfully.");
            clearHrAddForm();
            loadHrEmployeeDirectory();
        } catch (Exception e) {
            showStyledAlert("Validation Error", e.getMessage());
        }
    }

    @FXML
    private void handleHrViewEmployee() {
        Employee selected = getSelectedHrEmployee();
        if (selected == null) {
            showStyledAlert("Selection Required", "Please select an employee first.");
            return;
        }
        openEmployeeViewWindow(selected);
    }

    @FXML
    private void handleHrUpdateEmployee() {
        Employee selected = getSelectedHrEmployee();
        if (selected == null) {
            showStyledAlert("Selection Required", "Please select an employee first.");
            return;
        }
        openEmployeeUpdateWindow(selected);
    }

    @FXML
    private void handleHrArchiveEmployee() {
        Employee selected = getSelectedHrEmployee();
        if (selected == null) {
            showStyledAlert("Selection Required", "Please select an employee first.");
            return;
        }
        try {
            employeeService.archiveEmployee(selected.getEmployeeNumber(), currentSession());
            showStyledAlert("Success", "Employee archived to archived_employees.csv.");
            loadHrEmployeeDirectory();
            clearHrEmployeeDetailLabels();
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }



    @FXML
    private void handleHrDeleteEmployee() {
        if (!hasAdminAccess()) {
            showStyledAlert("Access Denied", "Only Admin users can delete employees.");
            return;
        }

        Employee selected = getSelectedHrEmployee();
        if (selected == null) {
            showStyledAlert("Selection Required", "Please select an employee to delete.");
            return;
        }

        String password = promptAdminPassword();
        if (password == null) {
            showStyledAlert("Cancelled", "Employee deletion was canceled.");
            return;
        }

        if (!verifyCurrentAdminPassword(password)) {
            showStyledAlert("Validation", "Password verification failed.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Do you really want to DELETE this Employee?");
        confirm.setContentText("This action permanently removes employee, attendance, request, and login records.");
        Optional<ButtonType> decision = confirm.showAndWait();
        if (decision.isEmpty() || decision.get() != ButtonType.OK) {
            showStyledAlert("Cancelled", "Employee deletion was canceled.");
            return;
        }

        try {
            employeeService.removeEmployee(selected.getEmployeeNumber(), currentSession());
            loadHrEmployeeDirectory();
            clearHrEmployeeDetailLabels();
            setupHrHistoryEmployeeSelector();
            populateSupervisorOptions(cmbHrSupervisor, null);
            setupFinanceBulkControls();
            showStyledAlert("Success", "Employee deleted successfully.");
        } catch (Exception e) {
            showStyledAlert("Error", e.getMessage());
        }
    }

    private String promptAdminPassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Admin Verification");
        dialog.setHeaderText("Enter your password to continue employee deletion.");

        ButtonType verifyButton = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(verifyButton, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        VBox content = new VBox(8, new Label("Admin Password"), passwordField);
        dialog.getDialogPane().setContent(content);

        Node verifyNode = dialog.getDialogPane().lookupButton(verifyButton);
        verifyNode.setDisable(true);
        passwordField.textProperty().addListener((obs, oldVal, newVal) ->
                verifyNode.setDisable(newVal == null || newVal.trim().isEmpty()));

        dialog.setResultConverter(buttonType -> buttonType == verifyButton ? passwordField.getText() : null);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private boolean verifyCurrentAdminPassword(String password) {
        Session session = currentSession();
        if (session == null || session.getUser() == null || password == null || password.isBlank()) {
            return false;
        }

        String username = session.getUser().getUsername();
        AuthService authService = new AuthService(new UserDAO());
        Session verified = authService.login(username, password);
        return verified != null
                && verified.getUser() != null
                && verified.getUser().getEmployeeNumber() == session.getUser().getEmployeeNumber();
    }
    private void setupHrTeamAttendancePane() {
        if (cmbHrTeamMonth == null || cmbHrTeamYear == null) {
            return;
        }
        cmbHrTeamMonth.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        cmbHrTeamMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        cmbHrTeamYear.getItems().clear();
        int currentYear = LocalDate.now().getYear();
        for (int year = 2020; year <= currentYear; year++) {
            cmbHrTeamYear.getItems().add(year);
        }
        cmbHrTeamYear.setValue(currentYear);

        configureAttendanceTableForHr(tblHrTeamAttendance, true);
        configureAttendanceTableForHr(tblHrEmployeeHistory, false);
        setupHrHistoryEmployeeSelector();
    }

    private void setupHrHistoryEmployeeSelector() {
        if (cmbHrHistoryEmployee == null) {
            return;
        }

        List<String> options = employeeService.getEmployeeDirectory(currentSession()).stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .map(emp -> emp.getEmployeeNumber() + " - " + emp.getFirstName() + " " + emp.getLastName())
                .toList();

        cmbHrHistoryEmployee.setItems(FXCollections.observableArrayList(options));
        cmbHrHistoryEmployee.setEditable(true);
        cmbHrHistoryEmployee.setPrefWidth(320);
        cmbHrHistoryEmployee.setMaxWidth(420);
        cmbHrHistoryEmployee.getProperties().put(HR_HISTORY_EMP_BASE_OPTIONS_KEY, List.copyOf(options));
        configureHrHistoryEmployeeAutocomplete(cmbHrHistoryEmployee);

    }

    @SuppressWarnings("unchecked")
    private List<String> getHrHistoryEmployeeBaseOptions(ComboBox<String> combo) {
        Object raw = combo.getProperties().get(HR_HISTORY_EMP_BASE_OPTIONS_KEY);
        if (raw instanceof List<?>) {
            return (List<String>) raw;
        }
        return List.of();
    }

    private void configureHrHistoryEmployeeAutocomplete(ComboBox<String> combo) {
        if (combo == null || Boolean.TRUE.equals(combo.getProperties().get(HR_HISTORY_EMP_AUTOCOMPLETE_KEY))) {
            return;
        }
        combo.getProperties().put(HR_HISTORY_EMP_AUTOCOMPLETE_KEY, true);

        combo.setOnShowing(event -> {
            List<String> baseOptions = getHrHistoryEmployeeBaseOptions(combo);
            combo.getProperties().put(HR_HISTORY_EMP_UPDATING_KEY, true);
            combo.getItems().setAll(baseOptions);
            combo.getProperties().put(HR_HISTORY_EMP_UPDATING_KEY, false);
        });

        combo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(combo.getProperties().get(HR_HISTORY_EMP_UPDATING_KEY))) {
                return;
            }
            applyHrHistoryEmployeeFilter(combo, newValue);
        });
    }

    private void applyHrHistoryEmployeeFilter(ComboBox<String> combo, String query) {
        List<String> baseOptions = getHrHistoryEmployeeBaseOptions(combo);
        if (baseOptions.isEmpty()) {
            return;
        }

        String keyword = query == null ? "" : query.trim().toLowerCase();
        List<String> filtered = baseOptions.stream()
                .filter(option -> keyword.isEmpty()
                        || option.toLowerCase().contains(keyword)
                        || option.startsWith(query == null ? "" : query.trim()))
                .toList();

        combo.getProperties().put(HR_HISTORY_EMP_UPDATING_KEY, true);
        combo.getItems().setAll(filtered.isEmpty() ? baseOptions : filtered);
        combo.getEditor().setText(query == null ? "" : query);
        combo.getEditor().positionCaret((query == null ? "" : query).length());
        combo.getProperties().put(HR_HISTORY_EMP_UPDATING_KEY, false);
    }

    private Integer resolveSelectedHistoryEmployeeNumber() {
        if (cmbHrHistoryEmployee == null) {
            return null;
        }

        String editorText = cmbHrHistoryEmployee.getEditor() != null ? cmbHrHistoryEmployee.getEditor().getText() : "";
        String selectedValue = cmbHrHistoryEmployee.getValue();
        String raw = (editorText != null && !editorText.trim().isEmpty()) ? editorText.trim() : (selectedValue == null ? "" : selectedValue.trim());
        if (raw.isEmpty()) {
            return null;
        }

        List<String> baseOptions = getHrHistoryEmployeeBaseOptions(cmbHrHistoryEmployee);
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
    private void configureAttendanceTableForHr(TableView<Attendance> table, boolean includeEmployeeNumber) {
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

    private void setupHrTeamRequestPane() {
        configureRequestTableForHr(tblHrPendingLeave);
        configureRequestTableForHr(tblHrRequestHistory);
    }

    private void configureRequestTableForHr(TableView<Request> table) {
        if (table == null) {
            return;
        }
        table.getColumns().clear();
        TableColumn<Request, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<Request, Integer> empCol = new TableColumn<>("Employee #");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        TableColumn<Request, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Request, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        if (table == tblHrRequestHistory) {
            TableColumn<Request, String> approverNameCol = new TableColumn<>("Approved By");
            approverNameCol.setCellValueFactory(new PropertyValueFactory<>("approvedByFullName"));
            TableColumn<Request, String> approverNoCol = new TableColumn<>("Approver #");
            approverNoCol.setCellValueFactory(new PropertyValueFactory<>("approvedByEmployeeNo"));
            table.getColumns().setAll(idCol, empCol, typeCol, catCol, statusCol, approverNameCol, approverNoCol);
        } else {
            table.getColumns().setAll(idCol, empCol, typeCol, catCol, statusCol);
        }

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadHrTeamRequestData() {
        if (tblHrPendingLeave != null) {
            tblHrPendingLeave.setItems(FXCollections.observableArrayList(hrRequestService.getPendingLeaveRequests(currentSession())));
        }
        if (tblHrRequestHistory != null) {
            tblHrRequestHistory.setItems(FXCollections.observableArrayList(hrRequestService.getRequestHistory(currentSession())));
        }
    }

    private Request getSelectedHrRequest() {
        return tblHrPendingLeave != null ? tblHrPendingLeave.getSelectionModel().getSelectedItem() : null;
    }

    private Request getSelectedHrRequestForDetails() {
        if (tblHrPendingLeave != null) {
            Request selectedLeave = tblHrPendingLeave.getSelectionModel().getSelectedItem();
            if (selectedLeave != null) {
                return selectedLeave;
            }
        }
        return tblHrRequestHistory != null ? tblHrRequestHistory.getSelectionModel().getSelectedItem() : null;
    }
    private void setupHrAddForm() {
        if (cmbHrStatus != null) {
            cmbHrStatus.setItems(FXCollections.observableArrayList("Regular", "Probationary"));
            cmbHrStatus.setValue("Regular");
        }
        populateHrDepartmentOptions();
        if (dpHrBirthday != null) {
            dpHrBirthday.setEditable(false);
        }
        populateSupervisorOptions(cmbHrSupervisor, null);
        if (cmbHrSupervisor != null) {
            cmbHrSupervisor.setEditable(true);
            cmbHrSupervisor.getSelectionModel().select(SUPERVISOR_NONE);
            cmbHrSupervisor.getEditor().setText(SUPERVISOR_NONE);
        }
    }


    private void populateHrDepartmentOptions() {
        if (cmbHrDepartment == null) {
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

        cmbHrDepartment.setItems(FXCollections.observableArrayList(departments));
        if (!departments.isEmpty()) {
            cmbHrDepartment.getSelectionModel().selectFirst();
        }
    }
    private void setupHrEmployeeDirectoryTable() {
        if (tblHrEmployeeDirectory == null) return;
        tblHrEmployeeDirectory.getColumns().clear();

        TableColumn<Employee, Integer> idCol = new TableColumn<>("Employee #");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));

        TableColumn<Employee, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cell -> javafx.beans.property.SimpleStringProperty.stringExpression(
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getPosition() != null ? cell.getValue().getPosition().getDepartment() : "N/A")
        ));

        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblHrEmployeeDirectory.getColumns().setAll(idCol, lastCol, firstCol, deptCol, statusCol);
        tblHrEmployeeDirectory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblHrEmployeeDirectory.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) bindHrEmployeeDetails(newV);
        });
    }

    private void loadHrEmployeeDirectory() {
        if (tblHrEmployeeDirectory == null) return;
        List<Employee> employees = employeeService.getEmployeeDirectory(currentSession());
        tblHrEmployeeDirectory.setItems(FXCollections.observableArrayList(employees));
        if (!employees.isEmpty()) {
            tblHrEmployeeDirectory.getSelectionModel().selectFirst();
            bindHrEmployeeDetails(tblHrEmployeeDirectory.getSelectionModel().getSelectedItem());
        }
    }

    private Employee buildEmployeeFromAddForm() {
        int employeeNumber = Integer.parseInt(readRequired(txtHrEmpNo, "Employee number"));
        String lastName = readRequired(txtHrLastName, "Last name");
        String firstName = readRequired(txtHrFirstName, "First name");
        LocalDate birthday = requireDate(dpHrBirthday, "Birthday");
        String address = readRequired(txtHrAddress, "Address");
        String phone = readRequired(txtHrPhone, "Phone");
        String sss = readRequired(txtHrSss, "SSS #");
        String philHealth = readRequired(txtHrPhilHealth, "PhilHealth #");
        String tin = readRequired(txtHrTin, "TIN #");
        String pagIbig = readRequired(txtHrPagIbig, "Pag-IBIG #");
        String status = cmbHrStatus.getValue() == null ? "Regular" : cmbHrStatus.getValue();
        String position = readRequired(txtHrPosition, "Position");
        String department = readRequired(cmbHrDepartment, "Department");
        String supervisor = extractSupervisorName(cmbHrSupervisor, "Supervisor");

        double basicSalary = parseDouble(readRequired(txtHrBasicSalary, "Basic Salary"), "Basic Salary");
        double riceSubsidy = parseDouble(readRequired(txtHrRiceSubsidy, "Rice Subsidy"), "Rice Subsidy");
        double phoneAllowance = parseDouble(readRequired(txtHrPhoneAllowance, "Phone Allowance"), "Phone Allowance");
        double clothingAllowance = parseDouble(readRequired(txtHrClothingAllowance, "Clothing Allowance"), "Clothing Allowance");

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

    private void clearHrAddForm() {
        txtHrEmpNo.clear();
        txtHrLastName.clear();
        txtHrFirstName.clear();
        dpHrBirthday.setValue(null);
        txtHrAddress.clear();
        txtHrPhone.clear();
        txtHrSss.clear();
        txtHrPhilHealth.clear();
        txtHrTin.clear();
        txtHrPagIbig.clear();
        txtHrPosition.clear();
        if (cmbHrDepartment != null) {
            if (!cmbHrDepartment.getItems().isEmpty()) {
                cmbHrDepartment.getSelectionModel().selectFirst();
            } else {
                cmbHrDepartment.getSelectionModel().clearSelection();
            }
        }
        if (cmbHrSupervisor != null) {
            cmbHrSupervisor.getSelectionModel().select(SUPERVISOR_NONE);
            cmbHrSupervisor.getEditor().setText(SUPERVISOR_NONE);
        }
        txtHrBasicSalary.clear();
        txtHrRiceSubsidy.clear();
        txtHrPhoneAllowance.clear();
        txtHrClothingAllowance.clear();
        cmbHrStatus.setValue("Regular");
    }

    private void bindHrEmployeeDetails(Employee employee) {
        if (employee == null) {
            clearHrEmployeeDetailLabels();
            return;
        }
        lblHrEmpNo.setText(String.valueOf(employee.getEmployeeNumber()));
        lblHrEmpName.setText(employee.getLastName() + ", " + employee.getFirstName());
        lblHrEmpBirthday.setText(employee.getBirthday() != null ? employee.getBirthday().toString() : "N/A");
        lblHrEmpDepartment.setText(employee.getPosition() != null ? employee.getPosition().getDepartment() : "N/A");
        lblHrEmpPosition.setText(employee.getPosition() != null ? employee.getPosition().getJobTitle() : "N/A");
        lblHrEmpStatus.setText(employee.getStatus() != null ? employee.getStatus() : "N/A");
    }

    private void clearHrEmployeeDetailLabels() {
        lblHrEmpNo.setText("-");
        lblHrEmpName.setText("-");
        lblHrEmpBirthday.setText("-");
        lblHrEmpDepartment.setText("-");
        lblHrEmpPosition.setText("-");
        lblHrEmpStatus.setText("-");
    }

    private Employee getSelectedHrEmployee() {
        return tblHrEmployeeDirectory == null ? null : tblHrEmployeeDirectory.getSelectionModel().getSelectedItem();
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
        return position == null || position.isBlank() ? name : name + " \u2014 " + position;
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

        List<String> baseOptions = getSupervisorBaseOptions(combo);
        String matchedDisplay = baseOptions.stream()
                .filter(option -> !SUPERVISOR_NONE.equalsIgnoreCase(option))
                .filter(option -> option.equalsIgnoreCase(normalizedRaw)
                        || extractSupervisorNameFromDisplay(option).equalsIgnoreCase(normalizedRaw))
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
        int idx = cleaned.indexOf(" \u2014 ");
        if (idx < 0) {
            idx = cleaned.indexOf(" - ");
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

    private void openHrRequestDetailsWindow(Request request) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Request Details - " + request.getRequestId());

        Employee employee = employeeService.getEmployee(request.getEmployeeNumber());
        String employeeName = employee != null ? employee.getFullName() : "Unknown Employee";

        GridPane details = new GridPane();
        details.setHgap(12);
        details.setVgap(8);
        details.setPadding(new Insets(14));

        int row = 0;
        addRow(details, row++, "Request ID", new Label(request.getRequestId()));
        addRow(details, row++, "Employee Name", new Label(employeeName));
        addRow(details, row++, "Employee #", new Label(String.valueOf(request.getEmployeeNumber())));
        addRow(details, row++, "Type", new Label(request.getType() == null ? "" : request.getType()));
        addRow(details, row++, "Category", new Label(request.getCategory() == null ? "" : request.getCategory()));
        addRow(details, row++, "Date Submitted", new Label(request.getDateFiled() == null ? "" : request.getDateFiled().toString()));

        String requestDetails = String.format("From: %s | To: %s",
                request.getStartDate() == null ? "" : request.getStartDate(),
                request.getEndDate() == null ? "" : request.getEndDate());
        addRow(details, row++, "Request Details", new Label(requestDetails));
        addRow(details, row++, "Reason", new Label(request.getReason() == null ? "" : request.getReason()));
        addRow(details, row++, "Status", new Label(request.getStatus() == null ? "" : request.getStatus()));
        addRow(details, row++, "Approved By", new Label(request.getApprovedByFullName() == null ? "" : request.getApprovedByFullName()));
        addRow(details, row, "Approver #", new Label(request.getApprovedByEmployeeNo() == null ? "" : request.getApprovedByEmployeeNo()));

        Scene scene = new Scene(details, 560, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }
    private void openEmployeeViewWindow(Employee employee) {
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

    private void openEmployeeUpdateWindow(Employee employee) {
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
        valueCol.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        form.getColumnConstraints().setAll(labelCol, valueCol);

        TextField txtEmpNo = new TextField(String.valueOf(employee.getEmployeeNumber()));
        TextField txtFirst = new TextField(employee.getFirstName());
        TextField txtLast = new TextField(employee.getLastName());
        DatePicker dpBirth = new DatePicker(employee.getBirthday());
        TextArea txtAddress = new TextArea(employee.getContact().getAddress());
        txtAddress.setPrefRowCount(2);
        TextField txtPhone = new TextField(employee.getContact().getPhone());

        TextField txtSss = new TextField(employee.getGovernmentID().getSss());
        TextField txtPhilHealth = new TextField(employee.getGovernmentID().getPhilHealth());
        TextField txtTin = new TextField(employee.getGovernmentID().getTin());
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

        int r = 0;
        addRow(form, r++, "Employee #", txtEmpNo);
        addRow(form, r++, "First Name", txtFirst);
        addRow(form, r++, "Last Name", txtLast);
        addRow(form, r++, "Birthday", dpBirth);
        addRow(form, r++, "Address", txtAddress);
        addRow(form, r++, "Phone", txtPhone);
        addRow(form, r++, "SSS #", txtSss);
        addRow(form, r++, "PhilHealth #", txtPhilHealth);
        addRow(form, r++, "TIN #", txtTin);
        addRow(form, r++, "Pag-IBIG #", txtPagIbig);
        addRow(form, r++, "Status", cmbStatus);
        addRow(form, r++, "Position", txtPosition);
        addRow(form, r++, "Department", txtDept);
        addRow(form, r++, "Supervisor", cmbSupervisor);
        addRow(form, r++, "Basic Salary", txtBasicSalary);
        addRow(form, r++, "Rice Subsidy", txtRiceSubsidy);
        addRow(form, r++, "Phone Allowance", txtPhoneAllowance);
        addRow(form, r, "Clothing Allowance", txtClothingAllowance);

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
                                readRequired(txtSss, "SSS #"),
                                readRequired(txtPhilHealth, "PhilHealth #"),
                                readRequired(txtTin, "TIN #"),
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
                loadHrEmployeeDirectory();
                showStyledAlert("Success", "Employee updated successfully.");
                stage.close();
            } catch (NumberFormatException ex) {
                showStyledAlert("Validation Error", "Employee Number must be a valid number.");
            } catch (Exception ex) {
                showStyledAlert("Validation Error", ex.getMessage());
            }
        });

        VBox root = new VBox(10, scroll, new HBox(10, btnSave));
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 680, 700));
        stage.showAndWait();
    }


    private void setupFinancePanes() {
        setupFinanceHistoryTable();
        setupFinanceBulkControls();
    }
    private void setupFinanceBulkControls() {
        if (rbFinScopeAll == null || rbFinScopeSelected == null || tblFinBulkEmployees == null) {
            return;
        }

        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        rbFinScopeAll.setToggleGroup(group);
        rbFinScopeSelected.setToggleGroup(group);
        rbFinScopeAll.setSelected(true);

        tblFinBulkEmployees.getColumns().clear();
        financeBulkSelection.clear();

        TableColumn<Employee, Boolean> selectCol = new TableColumn<>("Select");
        selectCol.setCellValueFactory(cell -> {
            int empNo = cell.getValue().getEmployeeNumber();
            financeBulkSelection.putIfAbsent(empNo, new SimpleBooleanProperty(false));
            return financeBulkSelection.get(empNo);
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);

        TableColumn<Employee, String> empNoCol = new TableColumn<>("Employee #");
        empNoCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getEmployeeNumber())));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastName() + ", " + cell.getValue().getFirstName()));

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPosition() != null ? cell.getValue().getPosition().getDepartment() : "N/A"));

        tblFinBulkEmployees.getColumns().setAll(selectCol, empNoCol, nameCol, deptCol);
        tblFinBulkEmployees.setEditable(true);
        tblFinBulkEmployees.setItems(FXCollections.observableArrayList(
                employeeService.getAllEmployees(currentSession()).stream()
                        .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                        .toList()
        ));

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> updateFinanceSelectionModeState());
        updateFinanceSelectionModeState();

        if (progressFinBulk != null) {
            progressFinBulk.setProgress(0);
        }
        if (lblFinBulkStatus != null) {
            lblFinBulkStatus.setText("Ready for payslip generation.");
        }
    }

    private void updateFinanceSelectionModeState() {
        boolean selectedMode = rbFinScopeSelected != null && rbFinScopeSelected.isSelected();
        if (tblFinBulkEmployees != null) {
            tblFinBulkEmployees.setDisable(!selectedMode);
        }
        if (btnFinClearSelection != null) {
            btnFinClearSelection.setDisable(!selectedMode);
        }
        if (lblFinSelectionHint != null) {
            lblFinSelectionHint.setText(selectedMode
                    ? "Tick checkboxes to choose employees for generation."
                    : "All Employees mode will generate payslips for the whole list.");
        }
        if (!selectedMode) {
            handleFinClearSelection();
        }
    }

    @FXML
    private void handleFinClearSelection() {
        financeBulkSelection.values().forEach(property -> property.set(false));
        if (tblFinBulkEmployees != null) {
            tblFinBulkEmployees.refresh();
        }
    }

    private void setupFinanceHistoryTable() {
        if (tblFinPayrollHistory == null) {
            return;
        }

        tblFinPayrollHistory.getColumns().clear();

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

        tblFinPayrollHistory.getColumns().setAll(empNoCol, nameCol, periodCol, grossCol, deductionsCol, netCol, statusCol);
        tblFinPayrollHistory.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Payslip> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openFinancePayrollDetailPopup(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleFinLoadPayrollHistory() {
        if (!hasFinanceAccess()) {
            showStyledAlert("Access Denied", "Only Finance users can access Finance panes.");
            return;
        }
        if (tblFinPayrollHistory == null) {
            return;
        }

        Integer year = cmbFinHistoryYear == null ? null : cmbFinHistoryYear.getValue();
        int month = cmbFinHistoryMonth == null ? -1 : cmbFinHistoryMonth.getSelectionModel().getSelectedIndex() + 1;
        if (year == null || month < 1) {
            showStyledAlert("Validation Error", "Please choose month and year first.");
            return;
        }

        List<Payslip> history = employeeService.getAllEmployees(currentSession()).stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeNumber))
                .map(employee -> financeService.computePayslip(currentSession(), employee, year, month))
                .filter(Objects::nonNull)
                .map(PayslipComputation::getPayslip)
                .toList();

        tblFinPayrollHistory.setItems(FXCollections.observableArrayList(history));
    }
    @FXML
    private void showFinGeneratePayslips() {
        if (!hasFinanceAccess()) {
            showStyledAlert("Access Denied", "Only Finance users can access Finance panes.");
            return;
        }
        hideAllPanes();
        paneFinGeneratePayslip.setVisible(true);
        paneFinGeneratePayslip.setManaged(true);

    }

    @FXML
    private void showFinPayrollHistory() {
        if (!hasFinanceAccess()) {
            showStyledAlert("Access Denied", "Only Finance users can access Finance panes.");
            return;
        }
        hideAllPanes();
        paneFinPayrollHistory.setVisible(true);
        paneFinPayrollHistory.setManaged(true);
        handleFinLoadPayrollHistory();
    }

        private String formatCurrency(double amount) {
        return String.format("PHP %,.2f", amount);
    }

    private void openFinancePayslipPreview(Employee employee, PayslipComputation computation, Path outputPath) {
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

        Label pathLabel = new Label("PDF saved to: " + outputPath.toString());
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

    private void openFinancePayrollDetailPopup(Payslip payslip) {
        Employee employee = employeeService.getEmployee(payslip.getEmployeeNumber());
        if (employee == null) {
            showStyledAlert("Missing Data", "Employee record not found for selected payroll row.");
            return;
        }

        PayslipComputation computation;
        try {
            computation = financeService.computePayslip(
                    currentSession(),
                    employee,
                    payslip.getYearMonth().getYear(),
                    payslip.getYearMonth().getMonthValue()
            );
        } catch (Exception e) {
            showStyledAlert("Access Denied", e.getMessage());
            return;
        }
        if (computation == null) {
            showStyledAlert("Missing Data", "Unable to load payroll details for selected record.");
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
                showStyledAlert("Open File", "Generated file was not found.");
                return;
            }
            Desktop.getDesktop().open(outputPath.toFile());
        } catch (Exception ex) {
            showStyledAlert("Open File", "Unable to open file: " + ex.getMessage());
        }
    }

    private void openOutputFolder(Path folderPath) {
        try {
            if (folderPath == null || !Files.exists(folderPath)) {
                showStyledAlert("Open Output Folder", "Output folder was not found.");
                return;
            }
            Desktop.getDesktop().open(folderPath.toFile());
        } catch (Exception ex) {
            showStyledAlert("Open Output Folder", "Unable to open folder: " + ex.getMessage());
        }
    }

    @FXML
    private void handleFinStartBulkGeneration() {
        if (!hasFinanceAccess()) {
            showStyledAlert("Access Denied", "Only Finance users can generate bulk payslips.");
            return;
        }
        if (financeBulkTask != null && financeBulkTask.isRunning()) {
            showStyledAlert("Bulk Generation", "A bulk generation task is already running.");
            return;
        }

        Integer year = cmbFinYear == null ? null : cmbFinYear.getValue();
        int month = cmbFinMonth == null ? -1 : cmbFinMonth.getSelectionModel().getSelectedIndex() + 1;
        if (year == null || month <= 0) {
            showStyledAlert("Validation", "Select month and year before bulk generation.");
            return;
        }

        List<Employee> targets = getBulkTargetEmployees();
        if (targets.isEmpty()) {
            showStyledAlert("Validation", "No employees selected for bulk generation.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Bulk Payslip Generation");
        confirm.setHeaderText("Generate bulk payslips for " + targets.size() + " employee(s)?");
        confirm.setContentText("Employees without valid payroll data will be skipped and included in the summary.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        financeBulkTask = createBulkPayslipTask(targets, year, month);
        bindBulkTaskUi(financeBulkTask);
        new Thread(financeBulkTask, "finance-bulk-payslip-task").start();
    }

    @FXML
    private void handleFinCancelBulkGeneration() {
        if (financeBulkTask != null && financeBulkTask.isRunning()) {
            financeBulkTask.cancel();
        }
    }

    private void bindBulkTaskUi(Task<BulkGenerationResult> task) {
        if (progressFinBulk != null) {
            progressFinBulk.progressProperty().unbind();
            progressFinBulk.progressProperty().bind(task.progressProperty());
        }
        if (lblFinBulkStatus != null) {
            lblFinBulkStatus.textProperty().unbind();
            lblFinBulkStatus.textProperty().bind(task.messageProperty());
        }
        if (btnFinBulkStart != null) {
            btnFinBulkStart.setDisable(true);
        }
        if (btnFinBulkCancel != null) {
            btnFinBulkCancel.setDisable(false);
        }

        task.setOnSucceeded(e -> finalizeBulkTask(task.getValue(), task.isCancelled()));
        task.setOnCancelled(e -> finalizeBulkTask(task.getValue(), true));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            finalizeBulkTask(new BulkGenerationResult(), true);
            showStyledAlert("Bulk Generation Error", ex == null ? "Unknown error" : ex.getMessage());
        });
    }

    private void finalizeBulkTask(BulkGenerationResult result, boolean cancelled) {
        if (progressFinBulk != null) {
            progressFinBulk.progressProperty().unbind();
            progressFinBulk.setProgress(cancelled ? 0 : 1);
        }
        if (lblFinBulkStatus != null) {
            lblFinBulkStatus.textProperty().unbind();
            lblFinBulkStatus.setText(cancelled ? "Bulk generation cancelled." : "Bulk generation completed.");
        }
        if (btnFinBulkStart != null) {
            btnFinBulkStart.setDisable(false);
        }
        if (btnFinBulkCancel != null) {
            btnFinBulkCancel.setDisable(true);
        }
        showBulkSummary(result, cancelled);
        financeBulkTask = null;
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
                        PayslipComputation computation = financeService.computePayslip(currentSession(), employee, year, month);
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

    private List<Employee> getBulkTargetEmployees() {
        if (rbFinScopeSelected != null && rbFinScopeSelected.isSelected()) {
            if (tblFinBulkEmployees == null) {
                return List.of();
            }
            return tblFinBulkEmployees.getItems().stream()
                    .filter(emp -> financeBulkSelection.containsKey(emp.getEmployeeNumber())
                            && financeBulkSelection.get(emp.getEmployeeNumber()).get())
                    .toList();
        }
        return employeeService.getAllEmployees(currentSession()).stream()
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
        String selected = combo.getValue();
        if (selected == null || selected.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return selected.trim();
    }
    private LocalDate requireDate(DatePicker picker, String label) {
        if (picker == null || picker.getValue() == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return picker.getValue();
    }

    private double parseDouble(String raw, String label) {
        return MonetaryInputParser.parseAmount(raw, label);
    }

    private Session currentSession() {
        return SessionContext.getInstance().getCurrentSession();
    }

    private boolean hasAdminAccess() {
        EffectiveRole role = RoleResolver.resolve(currentSession());
        return role == EffectiveRole.ADMIN;
    }
    private boolean hasHrAccess() {
        EffectiveRole role = RoleResolver.resolve(currentSession());
        return role == EffectiveRole.HR || role == EffectiveRole.ADMIN;
    }

    private boolean hasFinanceAccess() {
        EffectiveRole role = RoleResolver.resolve(currentSession());
        return role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN;
    }

    private boolean isHR() {
        return RoleResolver.resolve(currentSession()) == EffectiveRole.HR;
    }

    private void showStyledAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MotorPH");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
















































