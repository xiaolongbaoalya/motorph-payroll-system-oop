package com.compprog1282025.model.user;

public enum Permission {
	
	// Employee related permissions
	VIEW_PROFILE,
	VIEW_EMPLOYEE,
	CREATE_EMPLOYEE,
	UPDATE_EMPLOYEE,
	DELETE_EMPLOYEE,
	// Leave related permissions
	APPLY_LEAVE,
	APPROVE_LEAVE,
	// Finance related permissions
	CALCULATE_SALARY,
	GENERATE_PAYSLIP,
	// IT related permissions
	VIEW_USERS,
	CREATE_USERS,
	UPDATE_USERS,
	DELETE_USERS,
	// Attendance related permissions
	VIEW_ALL_ATTENDANCE,
	ADD_ATTENDANCE,
	UPDATE_ATTENDANCE,
	DELETE_ATTENDANCE,
	
}
