package com.compprog1282025.service;

import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.Session;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RequestService {
    private final RequestDAO requestDAO;

    public RequestService() {
        this(new RequestDAO());
    }

    public RequestService(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    public void submitRequest(Request request) {
        // Enforce leave-only request creation.
        request.setType("LEAVE");
        requestDAO.insert(request);
    }

    public List<Request> getEmployeeRequestHistory(int employeeNumber) {
        return requestDAO.getAll().stream()
                .filter(r -> r.getEmployeeNumber() == employeeNumber)
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public List<Request> getPendingLeaveRequests() {
        return requestDAO.getAll().stream()
                .filter(this::isPending)
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public List<Request> getRequestHistory() {
        return requestDAO.getAll().stream()
                .filter(r -> !isPending(r))
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public void approveRequest(String requestId) {
        updateRequestStatus(requestId, "APPROVED", null);
    }

    public void rejectRequest(String requestId) {
        updateRequestStatus(requestId, "REJECTED", null);
    }

    public void approveRequest(String requestId, Session approverSession) {
        updateRequestStatus(requestId, "APPROVED", approverSession);
    }

    public void rejectRequest(String requestId, Session approverSession) {
        updateRequestStatus(requestId, "REJECTED", approverSession);
    }

    private void updateRequestStatus(String requestId, String newStatus, Session approverSession) {
        Request request = requestDAO.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }

        if (!isLeave(request)) {
            throw new IllegalArgumentException("Only leave requests are supported.");
        }

        String normalizedStatus = newStatus == null ? "" : newStatus.trim().toUpperCase(Locale.ROOT);
        request.setStatus(normalizedStatus);

        if (!"PENDING".equals(normalizedStatus)) {
            request.setApprovedByFullName(resolveApproverFullName(approverSession));
            request.setApprovedByEmployeeNo(resolveApproverEmployeeNo(approverSession));
        }

        requestDAO.update(request);
    }

    private String resolveApproverFullName(Session session) {
        if (session == null || session.getUser() == null) {
            return "";
        }

        Employee employee = session.getUser().getEmployee();
        if (employee != null && employee.getFullName() != null && !employee.getFullName().isBlank()) {
            return employee.getFullName();
        }

        String username = session.getUser().getUsername();
        return username == null ? "" : username;
    }

    private String resolveApproverEmployeeNo(Session session) {
        if (session == null || session.getUser() == null) {
            return "";
        }
        return String.valueOf(session.getUser().getEmployeeNumber());
    }

    private boolean isPending(Request request) {
        return request != null && "PENDING".equalsIgnoreCase(request.getStatus());
    }

    private boolean isLeave(Request request) {
        return request != null && "LEAVE".equalsIgnoreCase(request.getType());
    }
}
