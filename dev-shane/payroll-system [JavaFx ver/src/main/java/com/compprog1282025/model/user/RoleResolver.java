package com.compprog1282025.model.user;

public final class RoleResolver {
    private RoleResolver() {
    }

    public static EffectiveRole resolve(Role role) {
        if (role == Role.HR) {
            return EffectiveRole.HR;
        }
        if (role == Role.ADMIN) {
            return EffectiveRole.ADMIN;
        }
        if (role == Role.FINANCE) {
            return EffectiveRole.FINANCE;
        }
        return EffectiveRole.EMPLOYEE;
    }

    public static EffectiveRole resolve(Session session) {
        if (session == null || session.getUser() == null) {
            return EffectiveRole.EMPLOYEE;
        }
        return resolve(session.getUser().getRole());
    }
}
