package com.compprog1282025.ui.gui.controllers.dashboard;

import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AccessControlService;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;

public class DashboardAccessHelper {

    public boolean hasAdminAccess(Session session) {
        return AccessControlService.canDeleteEmployees(session);
    }

    public boolean hasHrAccess(Session session) {
        return AccessControlService.canAccessHrPanel(session);
    }

    public boolean hasItAccess(Session session) {
        return AccessControlService.canAccessItPanel(session);
    }

    public boolean hasFinanceAccess(Session session) {
        return AccessControlService.canAccessFinancePanel(session);
    }

    public void configureRoleMenus(
            Session session,
            TitledPane panePersonalMenu,
            TitledPane paneHrMenu,
            TitledPane paneFinanceMenu,
            TitledPane paneItMenu
    ) {
        boolean isHr = hasHrAccess(session);
        boolean isFinance = hasFinanceAccess(session);
        boolean isIt = hasItAccess(session);

        if (paneHrMenu != null) {
            paneHrMenu.setVisible(isHr);
            paneHrMenu.setManaged(isHr);
            paneHrMenu.setExpanded(false);
        }
        if (paneFinanceMenu != null) {
            paneFinanceMenu.setVisible(isFinance);
            paneFinanceMenu.setManaged(isFinance);
            paneFinanceMenu.setExpanded(false);
        }
        if (paneItMenu != null) {
            paneItMenu.setVisible(isIt);
            paneItMenu.setManaged(isIt);
            paneItMenu.setExpanded(false);
        }
        if (panePersonalMenu != null) {
            panePersonalMenu.setExpanded(true);
        }
    }

    public void configureHrEmployeeActionButtons(Session session, Button btnHrDeleteEmployee) {
        boolean isAdmin = hasAdminAccess(session);
        if (btnHrDeleteEmployee != null) {
            btnHrDeleteEmployee.setVisible(isAdmin);
            btnHrDeleteEmployee.setManaged(isAdmin);
            btnHrDeleteEmployee.setDisable(!isAdmin);
        }
    }
}
