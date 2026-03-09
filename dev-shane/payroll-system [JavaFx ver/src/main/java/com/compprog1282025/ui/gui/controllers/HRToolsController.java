package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.model.employee.Attendance;
import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.RequestService;
import com.compprog1282025.service.SessionContext;
import com.compprog1282025.ui.gui.controllers.hr.HRAddEmployeeController;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamAttendanceController;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamRequestsController;
import com.compprog1282025.ui.gui.controllers.hr.HREmployeeDetailsController;
import com.compprog1282025.ui.gui.util.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class HRToolsController implements Initializable {
    @FXML private VBox paneTeamAttendance;
    @FXML private VBox paneTeamRequests;
    @FXML private VBox paneAddEmployee;
    @FXML private VBox paneEmployeeDetails;

    @FXML private ComboBox<String> cmbTeamMonth;
    @FXML private ComboBox<Integer> cmbTeamYear;
    @FXML private TableView<Attendance> tblTeamAttendance;
    @FXML private TextField txtHistoryEmpNo;
    @FXML private DatePicker dpHistoryFrom;
    @FXML private DatePicker dpHistoryTo;
    @FXML private TableView<Attendance> tblEmployeeHistory;

    @FXML private TableView<Request> tblPendingLeave;
    @FXML private TableView<Request> tblRequestHistory;

    @FXML private TextField txtAddEmpNo;
    @FXML private TextField txtAddFirstName;
    @FXML private TextField txtAddLastName;

    @FXML private TableView<Employee> tblEmployeeDirectory;
    @FXML private Label lblDetailEmpNo;
    @FXML private Label lblDetailName;
    @FXML private Label lblDetailDepartment;
    @FXML private Label lblDetailPosition;
    @FXML private Label lblDetailStatus;

    private HRTeamAttendanceController teamAttendanceController;
    private HRTeamRequestsController teamRequestsController;
    private HRAddEmployeeController addEmployeeController;
    private HREmployeeDetailsController employeeDetailsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Session session = currentSession();
        if (RoleResolver.resolve(session) != EffectiveRole.HR) {
            showAlert("Access Denied", "Only HR users can access this dashboard.");
            return;
        }

        AttendanceService attendanceService = new AttendanceService(new AttendanceDAO());
        EmployeeService employeeService = new EmployeeService(new EmployeeDAO());
        RequestService requestService = new RequestService();

        teamAttendanceController = new HRTeamAttendanceController(attendanceService);
        teamRequestsController = new HRTeamRequestsController(requestService);
        addEmployeeController = new HRAddEmployeeController(employeeService);
        employeeDetailsController = new HREmployeeDetailsController(employeeService);

        setupMonthYearFilters();
        setupAttendanceTables();
        setupRequestTables();
        setupEmployeeTable();

        showTeamAttendance();
    }

    @FXML
    private void showTeamAttendance() {
        setVisiblePane(paneTeamAttendance);
        loadTeamAttendance();
    }

    @FXML
    private void showTeamRequests() {
        setVisiblePane(paneTeamRequests);
        loadTeamRequests();
    }

    @FXML
    private void showAddEmployee() {
        setVisiblePane(paneAddEmployee);
    }

    @FXML
    private void showEmployeeDetails() {
        setVisiblePane(paneEmployeeDetails);
        loadEmployeeDirectory();
    }

    @FXML
    private void loadTeamAttendance() {
        int month = cmbTeamMonth.getSelectionModel().getSelectedIndex() + 1;
        Integer year = cmbTeamYear.getValue();
        if (year == null || month <= 0) return;

        List<Attendance> rows = teamAttendanceController.loadTeamAttendance(month, year);
        tblTeamAttendance.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void loadEmployeeAttendanceHistory() {
        if (txtHistoryEmpNo.getText() == null || txtHistoryEmpNo.getText().isBlank()) {
            showAlert("Validation", "Enter an employee number.");
            return;
        }

        int employeeNumber;
        try {
            employeeNumber = Integer.parseInt(txtHistoryEmpNo.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Validation", "Employee number must be numeric.");
            return;
        }

        List<Attendance> rows = teamAttendanceController.loadEmployeeAttendanceHistory(
                employeeNumber,
                dpHistoryFrom.getValue(),
                dpHistoryTo.getValue()
        );
        tblEmployeeHistory.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void approveSelectedRequest() {
        Request selected = getSelectedTeamRequest();
        if (selected == null) {
            showAlert("Selection Required", "Select a request to approve.");
            return;
        }

        try {
            teamRequestsController.approveRequest(selected.getRequestId(), currentSession());
            loadTeamRequests();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void rejectSelectedRequest() {
        Request selected = getSelectedTeamRequest();
        if (selected == null) {
            showAlert("Selection Required", "Select a request to reject.");
            return;
        }

        try {
            teamRequestsController.rejectRequest(selected.getRequestId(), currentSession());
            loadTeamRequests();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void addEmployee() {
        try {
            int employeeNumber = Integer.parseInt(txtAddEmpNo.getText().trim());
            String firstName = txtAddFirstName.getText();
            String lastName = txtAddLastName.getText();

            if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
                showAlert("Validation", "First and last name are required.");
                return;
            }

            Employee employee = new Employee(
                    employeeNumber,
                    firstName.trim(),
                    lastName.trim(),
                    LocalDate.now(),
                    new ContactInfo("N/A", "N/A"),
                    new GovernmentID("N/A", "N/A", "N/A", "N/A"),
                    new Position("Staff", "HR"),
                    new Compensation(20000),
                    "Active",
                    "None"
            );

            addEmployeeController.addEmployee(employee, currentSession());
            showAlert("Success", "Employee added successfully.");
            txtAddEmpNo.clear();
            txtAddFirstName.clear();
            txtAddLastName.clear();
            loadEmployeeDirectory();
        } catch (NumberFormatException e) {
            showAlert("Validation", "Employee number must be numeric.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void viewSelectedEmployee() {
        Employee selected = tblEmployeeDirectory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Select an employee from the directory.");
            return;
        }
        bindEmployeeDetails(selected);
    }

    @FXML
    private void updateSelectedEmployee() {
        showAlert("Update", "Update workflow will be connected to an edit form next.");
    }

    @FXML
    private void archiveSelectedEmployee() {
        Employee selected = tblEmployeeDirectory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Select an employee from the directory.");
            return;
        }

        try {
            employeeDetailsController.archiveEmployee(selected.getEmployeeNumber(), currentSession());
            showAlert("Success", "Employee archived.");
            loadEmployeeDirectory();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        SessionContext.getInstance().clear();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.toLogin(stage);
    }

    private void loadTeamRequests() {
        tblPendingLeave.setItems(FXCollections.observableArrayList(teamRequestsController.getPendingLeaveRequests()));
        tblRequestHistory.setItems(FXCollections.observableArrayList(teamRequestsController.getRequestHistory()));
    }

    private void loadEmployeeDirectory() {
        List<Employee> employees = employeeDetailsController.loadDirectory(currentSession());
        tblEmployeeDirectory.setItems(FXCollections.observableArrayList(employees));
        if (!employees.isEmpty()) {
            bindEmployeeDetails(employees.get(0));
        }
    }

    private void setupMonthYearFilters() {
        cmbTeamMonth.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        cmbTeamMonth.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        for (int year = 2020; year <= currentYear; year++) {
            cmbTeamYear.getItems().add(year);
        }
        cmbTeamYear.setValue(currentYear);
    }

    private void setupAttendanceTables() {
        configureAttendanceTable(tblTeamAttendance);
        configureAttendanceTable(tblEmployeeHistory);
    }

    private void configureAttendanceTable(TableView<Attendance> table) {
        table.getColumns().clear();
        TableColumn<Attendance, Integer> empCol = new TableColumn<>("Emp #");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        TableColumn<Attendance, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Attendance, String> inCol = new TableColumn<>("Time In");
        inCol.setCellValueFactory(new PropertyValueFactory<>("timeInFormatted"));
        TableColumn<Attendance, String> outCol = new TableColumn<>("Time Out");
        outCol.setCellValueFactory(new PropertyValueFactory<>("timeOutFormatted"));
        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().setAll(empCol, dateCol, inCol, outCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupRequestTables() {
        configureRequestTable(tblPendingLeave);
        configureRequestTable(tblRequestHistory);
    }

    private void configureRequestTable(TableView<Request> table) {
        table.getColumns().clear();
        TableColumn<Request, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        TableColumn<Request, Integer> empCol = new TableColumn<>("Emp #");
        empCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        TableColumn<Request, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Request, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (table == tblRequestHistory) {
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

    private void setupEmployeeTable() {
        tblEmployeeDirectory.getColumns().clear();
        TableColumn<Employee, Integer> empNoCol = new TableColumn<>("Emp #");
        empNoCol.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        TableColumn<Employee, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Employee, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblEmployeeDirectory.getColumns().setAll(empNoCol, firstCol, lastCol, statusCol);
        tblEmployeeDirectory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tblEmployeeDirectory.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) bindEmployeeDetails(newV);
        });
    }

    private void bindEmployeeDetails(Employee employee) {
        lblDetailEmpNo.setText(String.valueOf(employee.getEmployeeNumber()));
        lblDetailName.setText((employee.getFirstName() == null ? "" : employee.getFirstName()) + " " + (employee.getLastName() == null ? "" : employee.getLastName()));
        lblDetailDepartment.setText(employee.getPosition() != null ? employee.getPosition().getDepartment() : "N/A");
        lblDetailPosition.setText(employee.getPosition() != null ? employee.getPosition().getJobTitle() : "N/A");
        lblDetailStatus.setText(employee.getStatus() == null ? "N/A" : employee.getStatus());
    }

    private Request getSelectedTeamRequest() {
        Request selected = tblPendingLeave.getSelectionModel().getSelectedItem();
        if (selected != null) return selected;
        return null;
    }

    private void setVisiblePane(VBox target) {
        List<VBox> panes = List.of(paneTeamAttendance, paneTeamRequests, paneAddEmployee, paneEmployeeDetails);
        for (VBox pane : panes) {
            pane.setVisible(false);
            pane.setManaged(false);
        }
        target.setVisible(true);
        target.setManaged(true);
    }

    private Session currentSession() {
        return SessionContext.getInstance().getCurrentSession();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MotorPH");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




