package com.compprog1282025.main;

import com.compprog1282025.dao.AttendanceDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.LeaveDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.service.AttendanceService;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.service.EmployeeService;
import com.compprog1282025.service.FinanceService;
import com.compprog1282025.service.LeaveService;
import com.compprog1282025.ui.terminal.EmployeeMenu;
import com.compprog1282025.ui.terminal.FinanceMenu;
import com.compprog1282025.ui.terminal.HRMenu;
import com.compprog1282025.ui.terminal.MainMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Java is looking for files in: " + System.getProperty("user.dir"));
        try (Scanner scanner = new Scanner(System.in)) {
            EmployeeDAO employeeDao = new EmployeeDAO();
            AttendanceDAO attendanceDao = new AttendanceDAO();
            UserDAO userDao = new UserDAO();
            LeaveDAO leaveDao = new LeaveDAO();

            EmployeeService empSvc = new EmployeeService(employeeDao);
            AttendanceService attSvc = new AttendanceService(attendanceDao);
            FinanceService fncSvc = new FinanceService(attSvc);
            LeaveService leaveSvc = new LeaveService(leaveDao);

            MainMenu mainMenu = new MainMenu();
            EmployeeMenu empMenu = new EmployeeMenu(empSvc, attSvc, fncSvc, leaveSvc);
            HRMenu hrMenu = new HRMenu(empSvc);
            FinanceMenu finMenu = new FinanceMenu(fncSvc, empSvc);

            employeeDao.loadData();
            attendanceDao.loadData();
            userDao.loadData();

            AuthService auth = new AuthService(userDao);

            Employee currEmp = null;
            Session session;

            mainMenu.displayHeader("MotorPH Payroll System");
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            session = auth.login(username, password);
            if (session != null && session.isActive()) {
                System.out.println("--------------------");
                mainMenu.displayLoginSuccess(session);
                currEmp = empSvc.getEmployee(session.getUser().getEmployeeNumber());
            } else {
                mainMenu.displayLoginFail();
                return;
            }

            while (session.isActive()) {
                mainMenu.displayStartMenu(session);
                String mainChoice = scanner.nextLine();
                EffectiveRole effectiveRole = RoleResolver.resolve(session);

                switch (mainChoice.trim()) {
                    case "0":
                        if (session.getAttendance() != null && session.getAttendance().getTimeOut() == null) {
                            attSvc.timeOutAttendance(session, currEmp);
                        }
                        mainMenu.displayExit();
                        session.invalidateSession();
                        break;
                    case "1":
                        empMenu.displayPersonalMenu(session, scanner);
                        break;
                    case "2":
                        if (effectiveRole == EffectiveRole.HR || effectiveRole == EffectiveRole.ADMIN) {
                            hrMenu.displayHRMenu(session, scanner);
                        } else {
                            mainMenu.displayInvalid();
                        }
                        break;
                    case "3":
                        if (effectiveRole == EffectiveRole.FINANCE) {
                            finMenu.displayFinanceMenu(session, scanner);
                        } else {
                            mainMenu.displayInvalid();
                        }
                        break;
                    default:
                        mainMenu.displayInvalid();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
