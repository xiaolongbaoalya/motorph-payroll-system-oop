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
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.ITService;
import com.compprog1282025.service.PayslipPdfService;
import com.compprog1282025.service.RequestService;
import com.compprog1282025.ui.gui.util.SessionContext;
import com.compprog1282025.ui.gui.controllers.dashboard.AttendanceSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.DashboardAccessHelper;
import com.compprog1282025.ui.gui.controllers.dashboard.PayslipSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.ProfileSectionController;
import com.compprog1282025.ui.gui.controllers.dashboard.RequestSectionController;
import com.compprog1282025.ui.gui.controllers.finance.FinanceBulkPayslipHelper;
import com.compprog1282025.ui.gui.controllers.finance.FinanceHistoryHelper;
import com.compprog1282025.ui.gui.controllers.hr.HrEmployeeFormHelper;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamAttendanceController;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamRequestsController;
import com.compprog1282025.ui.gui.util.SceneSwitcher;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final String SUPERVISOR_NONE = "None";
    private static final String SUPERVISOR_BASE_OPTIONS_KEY = "supervisorBaseOptions";
    private static final String SUPERVISOR_AUTOCOMPLETE_KEY = "supervisorAutocompleteConfigured";
    private static final String SUPERVISOR_UPDATING_KEY = "supervisorUpdating";
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
    @FXML private TitledPane paneItMenu;

    @FXML private VBox paneFinGeneratePayslip, paneFinPayrollHistory;
    @FXML private VBox paneHrTeamAttendance;
    @FXML private VBox paneHrTeamRequests;
    @FXML private VBox paneHrAddEmployee;
    @FXML private VBox paneHrEmployeeDetails;
    @FXML private VBox paneItAdminTools;
    @FXML private Label lblItSystemStatus;
    @FXML private Label lblItDatabasePath;
    @FXML private TextArea txtItConsoleLog;

    @FXML private TextField txtHrEmpNo, txtHrLastName, txtHrFirstName, txtHrPhone;
    @FXML private TextArea txtHrAddress;
    @FXML private TextField txtHrPhilHealth, txtHrPagIbig;
    @FXML private TextField txtHrSssPart1, txtHrSssPart2, txtHrSssPart3;
    @FXML private TextField txtHrTinPart1, txtHrTinPart2, txtHrTinPart3, txtHrTinPart4;
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
    @FXML private Button btnFinBulkStart, btnFinClearSelection;
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
    private ITService itService;
    private DashboardAccessHelper dashboardAccessHelper;
    private HrEmployeeFormHelper hrEmployeeFormHelper;
    private HRTeamRequestsController hrTeamRequestsController;
    private HRTeamAttendanceController hrTeamAttendanceController;
    private ITDashboardController itPaneController;
    private FinanceBulkPayslipHelper financeBulkPayslipHelper;
    private FinanceHistoryHelper financeHistoryHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AttendanceService attendanceService = new AttendanceService(new AttendanceDAO());
        this.financeService = new FinanceService(attendanceService);
        this.payslipPdfService = new PayslipPdfService();
        this.employeeService = new EmployeeService(new EmployeeDAO());
        this.hrAttendanceService = attendanceService;
        this.hrRequestService = new RequestService();
        this.itService = new ITService();
        this.dashboardAccessHelper = new DashboardAccessHelper();
        this.hrEmployeeFormHelper = new HrEmployeeFormHelper(this.employeeService, this::currentSession, this::showStyledAlert);
        this.hrTeamRequestsController = new HRTeamRequestsController(this.hrRequestService, this::currentSession, this::showStyledAlert);
        this.hrTeamAttendanceController = new HRTeamAttendanceController(this.hrAttendanceService, this.employeeService, this::currentSession, this::showStyledAlert);
        this.itPaneController = new ITDashboardController(
                this.itService,
                this::currentSession,
                this::showStyledAlert,
                lblItSystemStatus,
                lblItDatabasePath,
                txtItConsoleLog
        );
        this.financeBulkPayslipHelper = new FinanceBulkPayslipHelper(
                this.employeeService,
                this.financeService,
                this.payslipPdfService,
                this::currentSession,
                this::hasFinanceAccess,
                details -> showStyledAlert(details[0], details[1]),
                rbFinScopeAll,
                rbFinScopeSelected,
                tblFinBulkEmployees,
                progressFinBulk,
                lblFinBulkStatus,
                lblFinSelectionHint,
                btnFinBulkStart,
                btnFinClearSelection
        );
        this.financeHistoryHelper = new FinanceHistoryHelper(
                this.financeService,
                this.employeeService,
                this::currentSession,
                this::hasFinanceAccess,
                this::showStyledAlert
        );

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
        configureHrEmployeeActionButtons();

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
        if (hasItAccess()) {
            initializeItPane();
        }

        showAttendance();
        attendanceSectionController.refreshAttendanceData(currentSession());
        payslipSectionController.refreshPayslipData(currentSession());
    }

    private void configureRoleMenus() {
        dashboardAccessHelper.configureRoleMenus(currentSession(), panePersonalMenu, paneHrMenu, paneFinanceMenu, paneItMenu);
    }

    private void configureHrEmployeeActionButtons() {
        dashboardAccessHelper.configureHrEmployeeActionButtons(currentSession(), btnHrDeleteEmployee);
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
    
    @FXML
    private void showItAdminTools() {
        if (!hasItAccess()) {
            showStyledAlert("Access Denied", "Only IT personnel can access these tools.");
            return;
        }

        hideAllPanes();

        if (paneItAdminTools != null) {
            paneItAdminTools.setVisible(true);
            paneItAdminTools.setManaged(true);
        }
    }

    private void initializeItPane() {
        itPaneController.initializePane("data/");
    }

    private void hideAllPanes() {
        List<VBox> panes = List.of(
                paneAttendance, paneProfile, panePayslip, paneRequests,
                paneFinGeneratePayslip, paneFinPayrollHistory,
                paneHrTeamAttendance, paneHrTeamRequests, paneHrAddEmployee, paneHrEmployeeDetails, paneItAdminTools
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
    private void handleItSystemAudit() {
        itPaneController.runSystemAudit();
    }

    @FXML
    private void handleItSystemHealth() {
        itPaneController.runSystemHealth();
    }

    @FXML
    private void handleItUserManagement() {
        itPaneController.runUserManagement();
    }

    @FXML
    private void handleItDatabaseBackup() {
        itPaneController.runDatabaseBackup();
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
        hrTeamAttendanceController.loadTeamAttendance(cmbHrTeamMonth, cmbHrTeamYear, tblHrTeamAttendance);
    }

    @FXML
    private void handleHrLoadEmployeeHistory() {
        hrTeamAttendanceController.loadEmployeeHistory(cmbHrHistoryEmployee, dpHrHistoryFrom, dpHrHistoryTo, tblHrEmployeeHistory);
    }

    @FXML
    private void handleHrApproveRequest() {
        hrTeamRequestsController.approveSelected(tblHrPendingLeave, tblHrRequestHistory);
    }

    @FXML
    private void handleHrRejectRequest() {
        hrTeamRequestsController.rejectSelected(tblHrPendingLeave, tblHrRequestHistory);
    }

    @FXML
    private void handleHrViewRequestDetails() {
        Request selected = hrTeamRequestsController.getSelectedForDetails(tblHrPendingLeave, tblHrRequestHistory);
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
            hrTeamAttendanceController.refreshHistoryEmployeeSelector(cmbHrHistoryEmployee);
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
        hrTeamAttendanceController.setupPane(
                cmbHrTeamMonth,
                cmbHrTeamYear,
                tblHrTeamAttendance,
                cmbHrHistoryEmployee,
                tblHrEmployeeHistory
        );
    }

    private void setupHrTeamRequestPane() {
        hrTeamRequestsController.configureTables(tblHrPendingLeave, tblHrRequestHistory);
    }

    private void loadHrTeamRequestData() {
        hrTeamRequestsController.loadCurrentSessionData(tblHrPendingLeave, tblHrRequestHistory);
    }
    private void setupHrAddForm() {
        hrEmployeeFormHelper.setupAddForm(getHrAddFormControls());
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
        return hrEmployeeFormHelper.buildEmployeeFromAddForm(getHrAddFormControls());
    }

    private void clearHrAddForm() {
        hrEmployeeFormHelper.clearAddForm(getHrAddFormControls());
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

    private HrEmployeeFormHelper.HrAddFormControls getHrAddFormControls() {
        return new HrEmployeeFormHelper.HrAddFormControls(
                txtHrEmpNo,
                txtHrFirstName,
                txtHrLastName,
                dpHrBirthday,
                txtHrAddress,
                txtHrPhone,
                txtHrSssPart1,
                txtHrSssPart2,
                txtHrSssPart3,
                txtHrPhilHealth,
                txtHrTinPart1,
                txtHrTinPart2,
                txtHrTinPart3,
                txtHrTinPart4,
                txtHrPagIbig,
                cmbHrStatus,
                txtHrPosition,
                cmbHrDepartment,
                cmbHrSupervisor,
                txtHrBasicSalary,
                txtHrRiceSubsidy,
                txtHrPhoneAllowance,
                txtHrClothingAllowance
        );
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
        hrEmployeeFormHelper.openEmployeeViewWindow(employee);
    }

    private void openEmployeeUpdateWindow(Employee employee) {
        hrEmployeeFormHelper.openEmployeeUpdateWindow(employee, this::loadHrEmployeeDirectory);
    }


    private void setupFinancePanes() {
        setupFinanceHistoryTable();
        setupFinanceBulkControls();
    }
    private void setupFinanceBulkControls() {
        financeBulkPayslipHelper.setupControls();
    }

    @FXML
    private void handleFinClearSelection() {
        financeBulkPayslipHelper.clearSelection();
    }

    private void setupFinanceHistoryTable() {
        financeHistoryHelper.setupHistoryTable(tblFinPayrollHistory);
    }

    @FXML
    private void handleFinLoadPayrollHistory() {
        Integer year = cmbFinHistoryYear == null ? null : cmbFinHistoryYear.getValue();
        int month = cmbFinHistoryMonth == null ? -1 : cmbFinHistoryMonth.getSelectionModel().getSelectedIndex() + 1;
        financeHistoryHelper.loadPayrollHistory(year, month, tblFinPayrollHistory);
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

    private void openFinancePayrollDetailPopup(Payslip payslip) {
        financeHistoryHelper.openPayrollDetailPopup(payslip);
    }

    @FXML
    private void handleFinStartBulkGeneration() {
        Integer year = cmbFinYear == null ? null : cmbFinYear.getValue();
        int month = cmbFinMonth == null ? -1 : cmbFinMonth.getSelectionModel().getSelectedIndex() + 1;
        financeBulkPayslipHelper.startBulkGeneration(year, month);
    }
    private void addRow(GridPane form, int row, String label, Node node) {
        form.add(new Label(label + ":"), 0, row);
        form.add(node, 1, row);
    }

    private Session currentSession() {
        return SessionContext.getInstance().getCurrentSession();
    }

    private boolean hasAdminAccess() {
        return dashboardAccessHelper.hasAdminAccess(currentSession());
    }
    private boolean hasHrAccess() {
        return dashboardAccessHelper.hasHrAccess(currentSession());
    }
    private boolean hasItAccess() {
        return dashboardAccessHelper.hasItAccess(currentSession());
    }

    private boolean hasFinanceAccess() {
        return dashboardAccessHelper.hasFinanceAccess(currentSession());
    }

    private void showStyledAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MotorPH");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
















































