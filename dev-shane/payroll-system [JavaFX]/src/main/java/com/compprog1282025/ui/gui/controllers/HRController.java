package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.RequestService;
import com.compprog1282025.ui.gui.controllers.hr.HRAddEmployeeController;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamAttendanceController;
import com.compprog1282025.ui.gui.controllers.hr.HRTeamRequestsController;
import com.compprog1282025.ui.gui.controllers.hr.HREmployeeDetailsController;

public class HRController {
    // Legacy entry-point wrapper kept for compatibility with existing FXML/controller wiring.
    // New HR functionality is split into dedicated controllers under ui.gui.controllers.hr.

    private final HRTeamAttendanceController teamAttendanceController;
    private final HRTeamRequestsController teamRequestsController;
    private final HRAddEmployeeController addEmployeeController;
    private final HREmployeeDetailsController employeeDetailsController;

    public HRController() {
        AttendanceService attendanceService = new AttendanceService(new AttendanceDAO());
        EmployeeService employeeService = new EmployeeService(new EmployeeDAO());
        RequestService requestService = new RequestService();

        this.teamAttendanceController = new HRTeamAttendanceController(attendanceService);
        this.teamRequestsController = new HRTeamRequestsController(requestService);
        this.addEmployeeController = new HRAddEmployeeController(employeeService);
        this.employeeDetailsController = new HREmployeeDetailsController(employeeService);
    }

    public HRTeamAttendanceController getTeamAttendanceController() {
        return teamAttendanceController;
    }

    public HRTeamRequestsController getTeamRequestsController() {
        return teamRequestsController;
    }

    public HRAddEmployeeController getAddEmployeeController() {
        return addEmployeeController;
    }

    public HREmployeeDetailsController getEmployeeDetailsController() {
        return employeeDetailsController;
    }
}
