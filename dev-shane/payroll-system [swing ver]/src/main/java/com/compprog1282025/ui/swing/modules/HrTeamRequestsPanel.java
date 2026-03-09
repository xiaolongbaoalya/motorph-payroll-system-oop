package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.RequestService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class HrTeamRequestsPanel extends JPanel implements RefreshablePanel {
    private final RequestService requestService;
    private final EmployeeService employeeService;
    private final Session session;

    private final JTable tblPending;
    private final JTable tblHistory;

    public HrTeamRequestsPanel(RequestService requestService, EmployeeService employeeService, Session session) {
        this.requestService = requestService;
        this.employeeService = employeeService;
        this.session = session;

        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tblPending = new JTable();
        tblPending.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHistory = new JTable();
        tblHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.addTab("Pending Leave Requests", new JScrollPane(tblPending));
        tabs.addTab("Request History", new JScrollPane(tblHistory));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnDetails = new JButton("View Details");
        btnApprove.addActionListener(e -> updateSelected(true));
        btnReject.addActionListener(e -> updateSelected(false));
        btnDetails.addActionListener(e -> viewDetails());

        actions.add(new JLabel("Manage leave requests"));
        actions.add(btnApprove);
        actions.add(btnReject);
        actions.add(btnDetails);

        add(actions, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        List<Request> pending = requestService.getPendingLeaveRequests();
        List<Request> history = requestService.getRequestHistory();
        SwingModuleUtil.setTableData(tblPending, columns(), toRows(pending, false));
        SwingModuleUtil.setTableData(tblHistory, columns(), toRows(history, true));
    }

    private String[] columns() {
        return new String[]{"ID", "Emp#", "Employee", "Category", "From", "To", "Status", "Approver"};
    }

    private Object[][] toRows(List<Request> requests, boolean includeApprover) {
        Object[][] data = new Object[requests.size()][8];
        for (int i = 0; i < requests.size(); i++) {
            Request r = requests.get(i);
            data[i][0] = r.getRequestId();
            data[i][1] = r.getEmployeeNumber();
            var emp = employeeService.getEmployee(r.getEmployeeNumber());
            data[i][2] = emp == null ? "Unknown" : emp.getFirstName() + " " + emp.getLastName();
            data[i][3] = r.getCategory();
            data[i][4] = r.getStartDate();
            data[i][5] = r.getEndDate();
            data[i][6] = r.getStatus();
            data[i][7] = includeApprover ? r.getApprovedByFullName() + " (" + r.getApprovedByEmployeeNo() + ")" : "";
        }
        return data;
    }

    private void updateSelected(boolean approve) {
        int row = tblPending.getSelectedRow();
        if (row < 0) {
            SwingModuleUtil.warn(this, "Selection", "Select a pending request first.");
            return;
        }
        String id = String.valueOf(tblPending.getValueAt(row, 0));
        try {
            if (approve) {
                requestService.approveRequest(id, session);
            } else {
                requestService.rejectRequest(id, session);
            }
            refreshData();
        } catch (Exception ex) {
            SwingModuleUtil.error(this, "Error", ex.getMessage());
        }
    }

    private void viewDetails() {
        JTable active = tblPending.getSelectedRow() >= 0 ? tblPending : tblHistory;
        int row = active.getSelectedRow();
        if (row < 0) {
            SwingModuleUtil.warn(this, "Selection", "Select a request first.");
            return;
        }
        String id = String.valueOf(active.getValueAt(row, 0));
        List<Request> all = requestService.getPendingLeaveRequests();
        all.addAll(requestService.getRequestHistory());
        Request match = all.stream().filter(r -> id.equals(r.getRequestId())).findFirst().orElse(null);
        if (match == null) {
            SwingModuleUtil.warn(this, "Missing", "Request not found.");
            return;
        }
        String msg = "Request ID: " + match.getRequestId() + "\n"
                + "Employee #: " + match.getEmployeeNumber() + "\n"
                + "Category: " + match.getCategory() + "\n"
                + "Date Filed: " + match.getDateFiled() + "\n"
                + "From: " + match.getStartDate() + "\n"
                + "To: " + match.getEndDate() + "\n"
                + "Status: " + match.getStatus() + "\n"
                + "Reason: " + match.getReason() + "\n"
                + "Approver: " + match.getApprovedByFullName() + " (" + match.getApprovedByEmployeeNo() + ")";
        SwingModuleUtil.info(this, "Request Details", msg);
    }
}
