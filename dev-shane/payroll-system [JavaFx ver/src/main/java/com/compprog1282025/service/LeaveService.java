package com.compprog1282025.service;

import java.time.LocalDate;

import com.compprog1282025.dao.LeaveDAO;
import com.compprog1282025.model.employee.Leave;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.Session;

public class LeaveService {
	private LeaveDAO leaveDao;

	public LeaveService(LeaveDAO leaveDao) {
		this.leaveDao = leaveDao;
	}
	
	public void applyLeave(Leave leave, Session session) throws InvalidAccessException {
		Role role = session.getUser().getRole();
    	if (role.hasPermission(Permission.APPLY_LEAVE)) {
    		leaveDao.insert(leave);
    	} else {
    		throw new InvalidAccessException("You do not have the permissions to perform this action.");
    	}
	}
	
	public void approveLeave(String leaveId, Session session) throws InvalidAccessException {
		Role role = session.getUser().getRole();
    	if (role.hasPermission(Permission.APPROVE_LEAVE)) {
    		Leave leave = leaveDao.findById(leaveId);
    		leave.setApprovalDate(LocalDate.now());
    		leave.setApprovingStaffNumber(session.getUser().getEmployeeNumber());
    		leave.setStatus("APPROVED");
    		leaveDao.update(leave);
    	} else {
    		throw new InvalidAccessException("You do not have the permissions to perform this action.");
    	}
	}
	
	public void rejectLeave(String leaveId, String reason, Session session) throws InvalidAccessException {
		Role role = session.getUser().getRole();
    	if (role.hasPermission(Permission.APPROVE_LEAVE)) {
    		Leave leave = leaveDao.findById(leaveId);
    		leave.setApprovalDate(LocalDate.now());
    		leave.setApprovingStaffNumber(session.getUser().getEmployeeNumber());
    		leave.setStatus("REJECTED");
    		leave.setLeaveReason(leave.getLeaveReason() + "[REJECT REASON: " + reason + "]" );
    		leaveDao.update(leave);
    	} else {
    		throw new InvalidAccessException("You do not have the permissions to perform this action.");
    	}
	}

}
