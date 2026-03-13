package com.compprog1282025.service;

import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;

final class AccessControlService {
    private AccessControlService() {
    }

    static EffectiveRole resolveRole(Session session) {
        return RoleResolver.resolve(session);
    }

    static boolean isEmployeeSelf(Session session, int employeeNumber) {
        return session != null
                && session.getUser() != null
                && session.getUser().getEmployeeNumber() == employeeNumber;
    }

    static void requireEmployeeManagement(Session session, String action) throws InvalidAccessException {
        EffectiveRole role = resolveRole(session);
        if (role == EffectiveRole.HR || role == EffectiveRole.ADMIN) {
            return;
        }
        deny(action, "HR or ADMIN");
    }

    static void requireEmployeeDelete(Session session, String action) throws InvalidAccessException {
        if (resolveRole(session) == EffectiveRole.ADMIN) {
            return;
        }
        deny(action, "ADMIN");
    }

    static void requireRequestApproval(Session session, String action) throws InvalidAccessException {
        EffectiveRole role = resolveRole(session);
        if (role == EffectiveRole.HR || role == EffectiveRole.ADMIN) {
            return;
        }
        deny(action, "HR or ADMIN");
    }

    static void requirePayrollProcessing(Session session, String action) throws InvalidAccessException {
        EffectiveRole role = resolveRole(session);
        if (role == EffectiveRole.FINANCE || role == EffectiveRole.ADMIN) {
            return;
        }
        deny(action, "FINANCE or ADMIN");
    }

    static void deny(String action, String requiredRoleDescription) throws InvalidAccessException {
        throw new InvalidAccessException(
                "Access denied: " + action + " requires role " + requiredRoleDescription + "."
        );
    }
}