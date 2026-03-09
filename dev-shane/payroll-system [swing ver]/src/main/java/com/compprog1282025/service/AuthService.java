package com.compprog1282025.service;

import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.model.user.*;
import com.compprog1282025.model.employee.Employee;

public class AuthService {
    private UserDAO userDao;

    public AuthService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public Session login(String username, String password) {
        User user = userDao.findByUsername(username);

        // Use BCrypt to compare the plain text 'password' with the 'passwordHash'
        // Inside your login method
        if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPasswordHash())) {
            // login successful...

            if (user.getEmployee() == null) {
                EmployeeDAO empDao = new EmployeeDAO();
                user.setEmployee(empDao.findById(user.getEmployeeNumber()));
            }
            return new Session(user);
        }
        return null;

    }


}