package com.compprog1282025.model.user;

import java.util.Set;

// This serves as the Role handler, are they IT, Admin, Finance, HR, MKT, Operations? More like a security guard

// This is an enum that defines the different roles that a user can have in the system. Each role corresponds to a specific set of permissions and access rights within the application. The roles defined here include ADMIN, FINANCE, HR, MKT, OPERATIONS, and IT. This enum is used throughout the application to check user permissions and to determine what actions a user is allowed to perform based on their assigned role.

public enum Role {

    ADMIN(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.DELETE_EMPLOYEE,
            Permission.APPLY_LEAVE,
            Permission.APPROVE_LEAVE,
            Permission.CALCULATE_SALARY,
            Permission.GENERATE_PAYSLIP
    )),
    FINANCE(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.APPLY_LEAVE,
            Permission.CALCULATE_SALARY,
            Permission.GENERATE_PAYSLIP
    )),
    HR(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.CREATE_EMPLOYEE,
            Permission.UPDATE_EMPLOYEE,
            Permission.APPLY_LEAVE,
            Permission.APPROVE_LEAVE
    )),
    MKT(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.APPLY_LEAVE
    )),
    OPERATIONS(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.APPLY_LEAVE
    )),
    IT(Set.of(
            Permission.VIEW_PROFILE,
            Permission.VIEW_EMPLOYEE,
            Permission.APPLY_LEAVE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

}
