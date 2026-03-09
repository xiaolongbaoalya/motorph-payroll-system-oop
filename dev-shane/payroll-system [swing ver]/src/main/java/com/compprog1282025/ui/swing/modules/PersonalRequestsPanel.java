package com.compprog1282025.ui.swing.modules;

import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.RequestService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

public class PersonalRequestsPanel extends JPanel implements RefreshablePanel {
    private final RequestService requestService;
    private final Session session;

    private final JComboBox<String> cmbLeaveType;
    private final DatePickerControl dpFrom;
    private final DatePickerControl dpTo;
    private final JTextArea txtReason;
    private final JTable table;

    public PersonalRequestsPanel(RequestService requestService, Session session) {
        this.requestService = requestService;
        this.session = session;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel title = new JLabel("New Request Application");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        formCard.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbLeaveType = new JComboBox<>(new String[]{"Sick Leave", "Vacation Leave", "Emergency Leave", "Maternity Leave", "Paternity Leave", "Other"});
        cmbLeaveType.setPreferredSize(new Dimension(210, 30));

        dpFrom = new DatePickerControl(LocalDate.now());
        dpTo = new DatePickerControl(LocalDate.now());

        txtReason = new JTextArea(4, 30);
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Leave Type"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(cmbLeaveType, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("From Date"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(dpFrom, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("To Date"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; form.add(dpTo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST; form.add(new JLabel("Reason"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(txtReason), gbc);

        JPanel actions = new JPanel(new BorderLayout());
        JButton btnSubmit = new JButton("Submit Leave Request");
        btnSubmit.addActionListener(e -> submitRequest());
        actions.add(btnSubmit, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.add(form, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);
        formCard.add(center, BorderLayout.CENTER);

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 237)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setOpaque(false);
        JLabel historyTitle = new JLabel("Request History");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JButton btnView = new JButton("View Details");
        btnView.addActionListener(e -> viewDetails());
        historyHeader.add(historyTitle, BorderLayout.WEST);
        historyHeader.add(btnView, BorderLayout.EAST);

        tableWrap.add(historyHeader, BorderLayout.NORTH);
        tableWrap.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formCard, BorderLayout.NORTH);
        add(tableWrap, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        List<Request> rows = requestService.getEmployeeRequestHistory(session.getUser().getEmployeeNumber());
        Object[][] data = new Object[rows.size()][4];
        for (int i = 0; i < rows.size(); i++) {
            Request r = rows.get(i);
            data[i][0] = r.getRequestId();
            data[i][1] = r.getDateFiled();
            data[i][2] = r.getCategory();
            data[i][3] = r.getStatus();
        }
        SwingModuleUtil.setTableData(table, new String[]{"REQ ID", "DATE FILED", "TYPE", "STATUS"}, data);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void submitRequest() {
        String category = String.valueOf(cmbLeaveType.getSelectedItem());
        LocalDate from = dpFrom.getDate();
        LocalDate to = dpTo.getDate();
        String reason = txtReason.getText() == null ? "" : txtReason.getText().trim();

        if (category == null || category.isBlank() || from == null || to == null || reason.isEmpty()) {
            SwingModuleUtil.warn(this, "Validation", "Complete leave type, dates, and reason.");
            return;
        }
        if (to.isBefore(from)) {
            SwingModuleUtil.warn(this, "Validation", "To Date cannot be earlier than From Date.");
            return;
        }

        Request req = new Request();
        req.setRequestId("REQ-" + (System.currentTimeMillis() % 100000));
        req.setEmployeeNumber(session.getUser().getEmployeeNumber());
        req.setType("LEAVE");
        req.setCategory(category);
        req.setStartDate(from);
        req.setEndDate(to);
        req.setReason(reason);
        req.setStatus("PENDING");
        req.setDateFiled(LocalDate.now());

        requestService.submitRequest(req);
        SwingModuleUtil.info(this, "Success", "Request submitted.");
        refreshData();
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            SwingModuleUtil.warn(this, "Selection", "Select a request first.");
            return;
        }

        String reqId = String.valueOf(table.getValueAt(row, 0));
        List<Request> requests = requestService.getEmployeeRequestHistory(session.getUser().getEmployeeNumber());
        Request match = requests.stream().filter(r -> reqId.equals(r.getRequestId())).findFirst().orElse(null);
        if (match == null) {
            SwingModuleUtil.warn(this, "Missing", "Request not found.");
            return;
        }

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setText("REQ ID: " + match.getRequestId() + "\n"
                + "Date Filed: " + match.getDateFiled() + "\n"
                + "Type: " + match.getType() + "\n"
                + "Category: " + match.getCategory() + "\n"
                + "From: " + match.getStartDate() + "\n"
                + "To: " + match.getEndDate() + "\n"
                + "Status: " + match.getStatus() + "\n"
                + "Reason: " + match.getReason());

        JDialog dialog = new JDialog();
        dialog.setTitle("Request Details");
        dialog.setModal(true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JScrollPane(area));
        dialog.setVisible(true);
    }
}