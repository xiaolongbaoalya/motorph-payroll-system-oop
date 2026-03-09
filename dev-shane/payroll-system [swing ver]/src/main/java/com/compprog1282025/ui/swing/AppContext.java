package com.compprog1282025.ui.swing;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.RequestDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.RequestService;

public class AppContext {
    private final AttendanceDAO attendanceDAO;
    private final EmployeeDAO employeeDAO;
    private final RequestDAO requestDAO;
    private final UserDAO userDAO;

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final FinanceService financeService;
    private final RequestService requestService;
    private final AuthService authService;

    private Session currentSession;

    public AppContext() {
        this.attendanceDAO = new AttendanceDAO();
        this.employeeDAO = new EmployeeDAO();
        this.requestDAO = new RequestDAO();
        this.userDAO = new UserDAO();

        this.attendanceService = new AttendanceService(attendanceDAO);
        this.employeeService = new EmployeeService(employeeDAO, attendanceDAO, requestDAO, userDAO);
        this.financeService = new FinanceService(attendanceService);
        this.requestService = new RequestService(requestDAO);
        this.authService = new AuthService(userDAO);
    }

    public AttendanceService getAttendanceService() {
        return attendanceService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    public FinanceService getFinanceService() {
        return financeService;
    }

    public RequestService getRequestService() {
        return requestService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public void clearCurrentSession() {
        this.currentSession = null;
    }
}