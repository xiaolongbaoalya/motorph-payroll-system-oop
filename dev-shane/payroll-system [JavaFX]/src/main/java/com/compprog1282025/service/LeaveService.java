package com.compprog1282025.service;

import java.time.LocalDate;

import com.compprog1282025.dao.LeaveDAO;
import com.compprog1282025.model.employee.Leave;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;

public class LeaveService {
    private final LeaveDAO leaveDao;

    public LeaveService(LeaveDAO leaveDao) {
        this.leaveDao = leaveDao;
    }

    public void applyLeave(Leave leave, Session session) throws InvalidAccessException {
        if (leave == null) {
            throw new IllegalArgumentException("Invalid input: Leave payload is required.");
        }
        Role role = session.getUser().getRole();
        if (!role.hasPermission(Permission.APPLY_LEAVE)) {
            throw new InvalidAccessException("Access denied: apply leave requires employee self-service permissions.");
        }
        if (leave.getEmployeeNumber() <= 0) {
            throw new IllegalArgumentException("Invalid input: Employee Number must be numeric.");
        }
        if (leave.getLeaveDate() == null) {
            throw new IllegalArgumentException("Invalid input: Leave date is required.");
        }
        leave.setLeaveReason(InputValidationService.requireReason("Leave Reason", leave.getLeaveReason()));
        leaveDao.insert(leave);
    }

    public void approveLeave(String leaveId, Session session) throws InvalidAccessException {
        Role role = session.getUser().getRole();
        if (!role.hasPermission(Permission.APPROVE_LEAVE)) {
            throw new InvalidAccessException("Access denied: approve leave requires HR permissions.");
        }

        Leave leave = leaveDao.findById(leaveId);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found: " + leaveId);
        }

        leave.setApprovalDate(LocalDate.now());
        leave.setApprovingStaffNumber(session.getUser().getEmployeeNumber());
        leave.setStatus("APPROVED");
        leaveDao.update(leave);
    }

    public void rejectLeave(String leaveId, String reason, Session session) throws InvalidAccessException {
        Role role = session.getUser().getRole();
        if (!role.hasPermission(Permission.APPROVE_LEAVE)) {
            throw new InvalidAccessException("Access denied: reject leave requires HR permissions.");
        }

        Leave leave = leaveDao.findById(leaveId);
        if (leave == null) {
            throw new IllegalArgumentException("Leave not found: " + leaveId);
        }

        String safeReason = InputValidationService.requireReason("Reject Reason", reason);
        leave.setApprovalDate(LocalDate.now());
        leave.setApprovingStaffNumber(session.getUser().getEmployeeNumber());
        leave.setStatus("REJECTED");
        leave.setLeaveReason(leave.getLeaveReason() + " [REJECT REASON: " + safeReason + "]");
        leaveDao.update(leave);
    }
}