package com.compprog1282025.ui.gui.controllers.dashboard;

import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.RequestService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

public class RequestSectionController {
    private final RequestService requestService;
    private final ComboBox<String> cmbLeaveType;
    private final DatePicker dpLeaveFrom;
    private final DatePicker dpLeaveTo;
    private final TextArea txtRequestReason;
    private final TableView<Request> tblRequestHistory;
    private final Button btnSubmitRequest;
    private final BiConsumer<String, String> alert;

    private boolean initialized;

    public RequestSectionController(
            RequestService requestService,
            ComboBox<String> cmbLeaveType,
            DatePicker dpLeaveFrom,
            DatePicker dpLeaveTo,
            TextArea txtRequestReason,
            TableView<Request> tblRequestHistory,
            Button btnSubmitRequest,
            BiConsumer<String, String> alert
    ) {
        this.requestService = requestService;
        this.cmbLeaveType = cmbLeaveType;
        this.dpLeaveFrom = dpLeaveFrom;
        this.dpLeaveTo = dpLeaveTo;
        this.txtRequestReason = txtRequestReason;
        this.tblRequestHistory = tblRequestHistory;
        this.btnSubmitRequest = btnSubmitRequest;
        this.alert = alert;
    }

    public void prepareForDisplay(Session session) {
        if (!initialized) {
            setupRequestForm();
            setupDateValidation();
            setupRequestTable();
            initialized = true;
        }
        refreshRequestHistory(session);
    }

    public void submitRequest(Session session) {
        if (session == null || session.getUser() == null) {
            return;
        }

        Employee emp = session.getUser().getEmployee();

        if (cmbLeaveType.getValue() == null || cmbLeaveType.getValue().trim().isEmpty()) {
            alert.accept("Validation Error", "Please select a leave type.");
            return;
        }
        if (dpLeaveFrom.getValue() == null || dpLeaveTo.getValue() == null) {
            alert.accept("Validation Error", "Please select both From and To dates.");
            return;
        }
        if (txtRequestReason.getText() == null || txtRequestReason.getText().trim().isEmpty()) {
            alert.accept("Validation Error", "Please enter a reason for your request.");
            return;
        }

        Request newReq = new Request();
        newReq.setRequestId("REQ-" + System.currentTimeMillis() % 10000);
        newReq.setEmployeeNumber(emp.getEmployeeNumber());
        newReq.setType("LEAVE");
        newReq.setCategory(cmbLeaveType.getValue());
        newReq.setStartDate(dpLeaveFrom.getValue());
        newReq.setEndDate(dpLeaveTo.getValue());
        newReq.setReason(txtRequestReason.getText().trim());
        newReq.setStatus("PENDING");
        newReq.setDateFiled(LocalDate.now());

        requestService.submitRequest(newReq);
        refreshRequestHistory(session);
        alert.accept("Success", "Your leave request has been filed.");
    }

    public void viewSelectedRequestDetails(Session session) {
        if (session == null || session.getUser() == null || tblRequestHistory == null) {
            return;
        }

        Request selected = tblRequestHistory.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.accept("Selection Required", "Select a request from your history first.");
            return;
        }

        openRequestDetailsWindow(selected);
    }

    private void setupRequestForm() {
        cmbLeaveType.setItems(FXCollections.observableArrayList(
                "Sick Leave", "Vacation Leave", "Bereavement Leave",
                "Maternity/Paternity Leave", "Leave Without Pay", "Others"
        ));
        btnSubmitRequest.setDisable(false);
        txtRequestReason.setDisable(false);
    }

    private void setupDateValidation() {
        dpLeaveTo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dpLeaveFrom.getValue() != null && newVal.isBefore(dpLeaveFrom.getValue())) {
                alert.accept("Invalid Date", "The 'To' date cannot be earlier than the 'From' date.");
                dpLeaveTo.setValue(null);
            }
        });
    }

    private void setupRequestTable() {
        tblRequestHistory.getColumns().clear();

        TableColumn<Request, String> idCol = new TableColumn<>("REQ ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<Request, LocalDate> dateFiledCol = new TableColumn<>("DATE FILED");
        dateFiledCol.setCellValueFactory(new PropertyValueFactory<>("dateFiled"));

        TableColumn<Request, String> typeCol = new TableColumn<>("TYPE");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Request, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblRequestHistory.getColumns().setAll(idCol, dateFiledCol, typeCol, statusCol);
        tblRequestHistory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshRequestHistory(Session session) {
        if (session != null && session.getUser() != null) {
            int empId = session.getUser().getEmployee().getEmployeeNumber();
            List<Request> myRequests = requestService.getEmployeeRequestHistory(empId);
            tblRequestHistory.setItems(FXCollections.observableArrayList(myRequests));
        }
    }

    private void openRequestDetailsWindow(Request request) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("My Request Details - " + request.getRequestId());

        GridPane details = new GridPane();
        details.setHgap(12);
        details.setVgap(8);
        details.setPadding(new Insets(14));

        int row = 0;
        addRow(details, row++, "REQ ID", request.getRequestId());
        addRow(details, row++, "DATE FILED", request.getDateFiled() == null ? "" : request.getDateFiled().toString());
        addRow(details, row++, "TYPE", nullToBlank(request.getType()));
        addRow(details, row++, "LEAVE TYPE", nullToBlank(request.getCategory()));
        addRow(details, row++, "LEAVE FROM", request.getStartDate() == null ? "" : request.getStartDate().toString());
        addRow(details, row++, "LEAVE TO", request.getEndDate() == null ? "" : request.getEndDate().toString());
        addRow(details, row++, "STATUS", nullToBlank(request.getStatus()));
        addRow(details, row, "REASON", nullToBlank(request.getReason()));

        VBox root = new VBox(details);
        root.setPadding(new Insets(4));
        stage.setScene(new Scene(root, 560, 400));
        stage.showAndWait();
    }

    private void addRow(GridPane pane, int row, String label, String value) {
        Label key = new Label(label + ":");
        key.setStyle("-fx-font-weight: bold;");
        Label val = new Label(nullToBlank(value));
        val.setWrapText(true);
        val.setMaxWidth(Double.MAX_VALUE);
        pane.add(key, 0, row);
        pane.add(val, 1, row);
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
