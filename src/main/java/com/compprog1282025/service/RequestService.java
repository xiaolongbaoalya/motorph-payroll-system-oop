package com.compprog1282025.service;

import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class RequestService {
    private static final String LEAVE_TYPE = "LEAVE";
    private static final String DEFAULT_LEAVE_START_TIME = "00:00";
    private static final String DEFAULT_LEAVE_END_TIME = "23:59";

    private final RequestDAO requestDAO;

    public RequestService() {
        this(new RequestDAO());
    }

    public RequestService(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    public void submitRequest(Request request, Session session) throws InvalidAccessException {
        if (request == null) {
            throw new IllegalArgumentException("Request payload is required.");
        }
        if (session == null || session.getUser() == null) {
            AccessControlService.deny("Submit leave request", "authenticated user");
        }
        if (request.getEmployeeNumber() <= 0) {
            throw new IllegalArgumentException("Employee number is required.");
        }
        if (!AccessControlService.isEmployeeSelf(session, request.getEmployeeNumber())) {
            AccessControlService.deny("Submit leave request for another employee", "self-service user");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Leave start and end dates are required.");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Leave end date cannot be earlier than start date.");
        }

        request.setRequestId(resolveRequestId(request.getRequestId()));
        request.setCategory(InputValidationService.requireLabel("Leave Type", request.getCategory()));
        request.setReason(InputValidationService.requireReason("Leave Reason", request.getReason()));

        request.setType(LEAVE_TYPE);
        request.setStatus(normalizeStatus(request.getStatus(), "PENDING"));
        normalizeLeaveTimes(request);

        if (request.getDateFiled() == null) {
            request.setDateFiled(LocalDate.now());
        }

        requestDAO.insert(request);
    }

    public List<Request> getEmployeeRequestHistory(int employeeNumber, Session session) throws InvalidAccessException {
        EffectiveRole role = AccessControlService.resolveRole(session);
        if (role == EffectiveRole.EMPLOYEE && !AccessControlService.isEmployeeSelf(session, employeeNumber)) {
            AccessControlService.deny("Read employee request history", "self-service user");
        }

        return requestDAO.getAll().stream()
                .filter(r -> r.getEmployeeNumber() == employeeNumber)
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public List<Request> getPendingLeaveRequests(Session session) throws InvalidAccessException {
        AccessControlService.requireRequestApproval(session, "Read pending leave requests");
        return requestDAO.getAll().stream()
                .filter(this::isPending)
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public List<Request> getRequestHistory(Session session) throws InvalidAccessException {
        AccessControlService.requireRequestApproval(session, "Read leave approval history");
        return requestDAO.getAll().stream()
                .filter(r -> !isPending(r))
                .filter(this::isLeave)
                .collect(Collectors.toList());
    }

    public void approveRequest(String requestId, Session approverSession) throws InvalidAccessException {
        updateRequestStatus(requestId, "APPROVED", approverSession);
    }

    public void rejectRequest(String requestId, Session approverSession) throws InvalidAccessException {
        updateRequestStatus(requestId, "REJECTED", approverSession);
    }

    private void updateRequestStatus(String requestId, String newStatus, Session approverSession) throws InvalidAccessException {
        AccessControlService.requireRequestApproval(approverSession, "Approve or reject leave request");

        Request request = requestDAO.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request not found: " + requestId);
        }

        if (!isLeave(request)) {
            throw new IllegalArgumentException("Only leave requests are supported.");
        }

        String normalizedStatus = normalizeStatus(newStatus, "PENDING");
        request.setStatus(normalizedStatus);
        normalizeLeaveTimes(request);

        if (!"PENDING".equals(normalizedStatus)) {
            request.setApprovedByFullName(resolveApproverFullName(approverSession));
            request.setApprovedByEmployeeNo(resolveApproverEmployeeNo(approverSession));
        }

        requestDAO.update(request);
    }

    private String resolveRequestId(String providedRequestId) {
        String trimmed = providedRequestId == null ? "" : providedRequestId.trim();
        if (trimmed.isEmpty()) {
            return generateUniqueRequestId();
        }

        InputValidationService.requireLabel("Request ID", trimmed);
        if (requestDAO.findById(trimmed) != null) {
            throw new IllegalArgumentException("Invalid input: Request ID already exists.");
        }
        return trimmed;
    }

    private String generateUniqueRequestId() {
        String candidate;
        do {
            candidate = "REQ-" + UUID.randomUUID().toString();
        } while (requestDAO.findById(candidate) != null);
        return candidate;
    }

    private void normalizeLeaveTimes(Request request) {
        if (!isLeave(request)) {
            return;
        }
        if (isBlank(request.getStartTime())) {
            request.setStartTime(DEFAULT_LEAVE_START_TIME);
        }
        if (isBlank(request.getEndTime())) {
            request.setEndTime(DEFAULT_LEAVE_END_TIME);
        }
        request.setStartTime(InputValidationService.requireTime("T-Start", request.getStartTime()));
        request.setEndTime(InputValidationService.requireTime("T-End", request.getEndTime()));
    }

    private String normalizeStatus(String status, String defaultValue) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return defaultValue;
        }
        return normalized;
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
        return request != null && LEAVE_TYPE.equalsIgnoreCase(request.getType());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}