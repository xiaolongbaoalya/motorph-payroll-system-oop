package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.user.Session;
import javafx.scene.layout.VBox;

import java.util.List;

public class HRDashboardController {
    private final VBox paneTeamAttendance;
    private final VBox paneTeamRequests;
    private final VBox paneAddEmployee;
    private final VBox paneEmployeeDetails;

    private final HRTeamAttendanceController teamAttendanceController;
    private final HRTeamRequestsController teamRequestsController;
    private final HRAddEmployeeController addEmployeeController;
    private final HREmployeeDetailsController employeeDetailsController;

    public HRDashboardController(
            VBox paneTeamAttendance,
            VBox paneTeamRequests,
            VBox paneAddEmployee,
            VBox paneEmployeeDetails,
            HRTeamAttendanceController teamAttendanceController,
            HRTeamRequestsController teamRequestsController,
            HRAddEmployeeController addEmployeeController,
            HREmployeeDetailsController employeeDetailsController
    ) {
        this.paneTeamAttendance = paneTeamAttendance;
        this.paneTeamRequests = paneTeamRequests;
        this.paneAddEmployee = paneAddEmployee;
        this.paneEmployeeDetails = paneEmployeeDetails;
        this.teamAttendanceController = teamAttendanceController;
        this.teamRequestsController = teamRequestsController;
        this.addEmployeeController = addEmployeeController;
        this.employeeDetailsController = employeeDetailsController;
    }

    public void showTeamAttendance(Session session) {
        hideAllPanes();
        paneTeamAttendance.setVisible(true);
        paneTeamAttendance.setManaged(true);
        teamAttendanceController.loadDefault(session);
    }

    public void showTeamRequests(Session session) {
        hideAllPanes();
        paneTeamRequests.setVisible(true);
        paneTeamRequests.setManaged(true);
        teamRequestsController.load(session);
    }

    public void showAddEmployee() {
        hideAllPanes();
        paneAddEmployee.setVisible(true);
        paneAddEmployee.setManaged(true);
    }

    public void showEmployeeDetails(Session session) {
        hideAllPanes();
        paneEmployeeDetails.setVisible(true);
        paneEmployeeDetails.setManaged(true);
        employeeDetailsController.loadDirectory(session);
    }

    private void hideAllPanes() {
        List<VBox> panes = List.of(paneTeamAttendance, paneTeamRequests, paneAddEmployee, paneEmployeeDetails);
        for (VBox pane : panes) {
            if (pane != null) {
                pane.setVisible(false);
                pane.setManaged(false);
            }
        }
    }
}
