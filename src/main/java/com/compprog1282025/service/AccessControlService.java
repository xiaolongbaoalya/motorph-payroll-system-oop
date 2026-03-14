package com.compprog1282025.service;

import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;

public final class AccessControlService {
    private AccessControlService() {
    }

    public static EffectiveRole resolveRole(Session session) {
        return RoleResolver.resolve(session);
    }

    public static boolean isAuthenticated(Session session) {
        return session != null && session.getUser() != null && session.isActive();
    }

    public static boolean isEmployeeSelf(Session session, int employeeNumber) {
        return session != null
                && session.getUser() != null
                && session.getUser().getEmployeeNumber() == employeeNumber;
    }

    public static boolean hasPermission(Session session, Permission permission) {
        Role role = session == null || session.getUser() == null ? null : session.getUser().getRole();
        return role != null && role.hasPermission(permission);
    }

    public static boolean canAccessHrPanel(Session session) {
        EffectiveRole role = resolveRole(session);
        return role == EffectiveRole.HR || role == EffectiveRole.ADMIN;
    }

    public static boolean canAccessFinancePanel(Session session) {
        EffectiveRole role = resolveRole(session);
        return role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN;
    }

    public static boolean canAccessItPanel(Session session) {
        EffectiveRole role = resolveRole(session);
        return role == EffectiveRole.IT || role == EffectiveRole.ADMIN;
    }

    public static boolean canManageEmployees(Session session) {
        return canAccessHrPanel(session);
    }

    public static boolean canDeleteEmployees(Session session) {
        return resolveRole(session) == EffectiveRole.ADMIN;
    }

    public static boolean canApproveRequests(Session session) {
        return canAccessHrPanel(session);
    }

    public static boolean canProcessPayroll(Session session) {
        return canAccessFinancePanel(session);
    }

    public static boolean canViewEmployeeDirectory(Session session) {
        EffectiveRole role = resolveRole(session);
        return role == EffectiveRole.HR || role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN;
    }

    public static boolean canViewEmployeeAttendanceHistory(Session session, int employeeNumber) {
        return resolveRole(session) != EffectiveRole.EMPLOYEE || isEmployeeSelf(session, employeeNumber);
    }

    public static boolean canViewEmployeeRequests(Session session, int employeeNumber) {
        return resolveRole(session) != EffectiveRole.EMPLOYEE || isEmployeeSelf(session, employeeNumber);
    }

    public static boolean canApplyLeave(Session session) {
        return hasPermission(session, Permission.APPLY_LEAVE);
    }

    public static boolean canApproveLeave(Session session) {
        return hasPermission(session, Permission.APPROVE_LEAVE);
    }

    public static void requireEmployeeManagement(Session session, String action) throws InvalidAccessException {
        if (canManageEmployees(session)) {
            return;
        }
        deny(action, "HR or ADMIN");
    }

    public static void requireEmployeeDelete(Session session, String action) throws InvalidAccessException {
        if (canDeleteEmployees(session)) {
            return;
        }
        deny(action, "ADMIN");
    }

    public static void requireRequestApproval(Session session, String action) throws InvalidAccessException {
        if (canApproveRequests(session)) {
            return;
        }
        deny(action, "HR or ADMIN");
    }

    public static void requirePayrollProcessing(Session session, String action) throws InvalidAccessException {
        if (canProcessPayroll(session)) {
            return;
        }
        deny(action, "FINANCE or ADMIN");
    }

    public static void requireAttendanceHistoryAccess(Session session, int employeeNumber, String action) throws InvalidAccessException {
        if (canViewEmployeeAttendanceHistory(session, employeeNumber)) {
            return;
        }
        deny(action, "self-service user");
    }

    public static void requireRequestHistoryAccess(Session session, int employeeNumber, String action) throws InvalidAccessException {
        if (canViewEmployeeRequests(session, employeeNumber)) {
            return;
        }
        deny(action, "self-service user");
    }

    public static void requireLeaveApplication(Session session, String action) throws InvalidAccessException {
        if (canApplyLeave(session)) {
            return;
        }
        deny(action, "employee self-service permissions");
    }

    public static void requireLeaveApproval(Session session, String action) throws InvalidAccessException {
        if (canApproveLeave(session)) {
            return;
        }
        deny(action, "HR permissions");
    }

    public static void deny(String action, String requiredRoleDescription) throws InvalidAccessException {
        throw new InvalidAccessException(
                "Access denied: " + action + " requires role " + requiredRoleDescription + "."
        );
    }
}
