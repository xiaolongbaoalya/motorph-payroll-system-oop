package com.compprog1282025.model.employee;

import java.time.LocalDate;

import com.compprog1282025.service.DateTimeUtil;

public class Leave {
	private String leaveId;
	private int employeeNumber;
	private LocalDate leaveDate;
	private String leaveReason;
	private String status;
	private int approvingStaffNumber;
	private LocalDate approvalDate;
	
	public Leave(String leaveId, int employeeNumber, LocalDate leaveDate, String leaveReason, String status, int approvingStaffNumber, LocalDate approvalDate) {
		super();
		this.leaveId = leaveId;
		this.employeeNumber = employeeNumber;
		this.leaveDate = leaveDate;
		this.status = status;
		this.approvingStaffNumber = approvingStaffNumber;
		this.approvalDate = approvalDate;
	}
	
	public Leave(String leaveId, int employeeNumber, LocalDate leaveDate, String leaveReason) {
		super();
		this.leaveId = leaveId;
		this.employeeNumber = employeeNumber;
		this.leaveDate = leaveDate;
		this.leaveReason = leaveReason;
		this.status = "PENDING";
		this.approvingStaffNumber = -1;
		this.approvalDate = DateTimeUtil.getInvalidDate();
	}

	public String getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}
	
	public int getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(int employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public LocalDate getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(LocalDate leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	public String getLeaveReason() {
		return leaveReason;
	}
	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public int getApprovingStaffNumber() {
		return approvingStaffNumber;
	}
	public void setApprovingStaffNumber(int approvingStaffNumber) {
		this.approvingStaffNumber = approvingStaffNumber;
	}

	public LocalDate getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(LocalDate approvalDate) {
		this.approvalDate = approvalDate;
	}
	
	
}
