package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AccessControlService;
import com.compprog1282025.service.InvalidAccessException;
import com.compprog1282025.service.RequestService;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HRTeamRequestsController {
    private final RequestService requestService;
    private final Supplier<Session> sessionSupplier;
    private final BiConsumer<String, String> alertCallback;

    public HRTeamRequestsController(RequestService requestService) {
        this(requestService, null, null);
    }

    public HRTeamRequestsController(
            RequestService requestService,
            Supplier<Session> sessionSupplier,
            BiConsumer<String, String> alertCallback
    ) {
        this.requestService = requestService;
        this.sessionSupplier = sessionSupplier;
        this.alertCallback = alertCallback;
    }

    public List<Request> getPendingLeaveRequests(Session session) throws InvalidAccessException {
        return requestService.getPendingLeaveRequests(session);
    }

    public List<Request> getRequestHistory(Session session) throws InvalidAccessException {
        return requestService.getRequestHistory(session);
    }

    public void approveRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.approveRequest(requestId, session);
    }

    public void rejectRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.rejectRequest(requestId, session);
    }

    public void load(Session session) throws InvalidAccessException {
        getPendingLeaveRequests(session);
        getRequestHistory(session);
    }

    public void configureTables(TableView<Request> pendingTable, TableView<Request> historyTable) {
        configureRequestTable(pendingTable, false);
        configureRequestTable(historyTable, true);
    }

    public void loadCurrentSessionData(TableView<Request> pendingTable, TableView<Request> historyTable) {
        Session session = requireSession();
        pendingTable.setItems(FXCollections.observableArrayList(getPendingLeaveRequests(session)));
        historyTable.setItems(FXCollections.observableArrayList(getRequestHistory(session)));
    }

    public boolean approveSelected(TableView<Request> pendingTable, TableView<Request> historyTable) {
        Request selected = getSelectedPendingRequest(pendingTable);
        if (selected == null) {
            showAlert("Selection Required", "Select a pending request first.");
            return false;
        }
        try {
            approveRequest(selected.getRequestId(), requireSession());
            loadCurrentSessionData(pendingTable, historyTable);
            showAlert("Success", "Request approved.");
            return true;
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return false;
        }
    }

    public boolean rejectSelected(TableView<Request> pendingTable, TableView<Request> historyTable) {
        Request selected = getSelectedPendingRequest(pendingTable);
        if (selected == null) {
            showAlert("Selection Required", "Select a pending request first.");
            return false;
        }
        try {
            rejectRequest(selected.getRequestId(), requireSession());
            loadCurrentSessionData(pendingTable, historyTable);
            showAlert("Success", "Request rejected.");
            return true;
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            return false;
        }
    }

    public Request getSelectedForDetails(TableView<Request> pendingTable, TableView<Request> historyTable) {
        Request pending = pendingTable == null ? null : pendingTable.getSelectionModel().getSelectedItem();
        if (pending != null) {
            return pending;
        }
        return historyTable == null ? null : historyTable.getSelectionModel().getSelectedItem();
    }

    private void ensureHR(Session session) throws InvalidAccessException {
        if (!AccessControlService.canApproveRequests(session)) {
            throw new InvalidAccessException("Only HR or Admin users can approve or reject team requests.");
        }
    }

    private void configureRequestTable(TableView<Request> table, boolean history) {
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

        if (history) {
            TableColumn<Request, String> approverNameCol = new TableColumn<>("Approved By");
            approverNameCol.setCellValueFactory(new PropertyValueFactory<>("approvedByFullName"));
            TableColumn<Request, String> approverNoCol = new TableColumn<>("Approver #");
            approverNoCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getApprovedByEmployeeNo()));
            table.getColumns().setAll(idCol, empCol, typeCol, catCol, statusCol, approverNameCol, approverNoCol);
        } else {
            table.getColumns().setAll(idCol, empCol, typeCol, catCol, statusCol);
        }

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private Request getSelectedPendingRequest(TableView<Request> pendingTable) {
        return pendingTable == null ? null : pendingTable.getSelectionModel().getSelectedItem();
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
