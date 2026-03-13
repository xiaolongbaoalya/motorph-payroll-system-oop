package com.compprog1282025.ui.terminal;

import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.Permission;
import com.compprog1282025.model.user.Role;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;

public class MainMenu extends BaseMenu {

    public void displayLoginSuccess(Session session) {
        System.out.println("Welcome " + session.getUser().toString());
    }

    public void displayLoginFail() {
        System.out.println("Username or password is incorrect.");
    }

    public void displayStartMenu(Session session) {
        this.displayHeader("Main Menu");

        Role role = session.getUser().getRole();
        EffectiveRole effectiveRole = RoleResolver.resolve(session);

        if (role != null && role.hasPermission(Permission.VIEW_PROFILE)) {
            System.out.println("1. Personal Menu");
        }
        if (effectiveRole == EffectiveRole.HR || effectiveRole == EffectiveRole.ADMIN) {
            System.out.println("2. HR Menu");
        }
        if (effectiveRole == EffectiveRole.FINANCE || effectiveRole == EffectiveRole.ADMIN) {
            System.out.println("3. Finance Menu");
        }

        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    public void displayInvalid() {
        System.out.println("That was not a valid input, please try again.");
    }

    public void displayExit() {
        System.out.println("Thank you for using the system.");
        System.out.print("Exiting...");
    }
}
