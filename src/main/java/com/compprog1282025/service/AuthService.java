package com.compprog1282025.service;

import com.compprog1282025.dao.EmployeeDAO;
import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.model.user.User;

public class AuthService {
    private final UserDAO userDao;

    public AuthService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public Session login(String username, String password) {
        User user = userDao.findByUsername(username);

        if (user != null && user.authenticate(password)) {
            if (user.getEmployee() == null) {
                EmployeeDAO empDao = new EmployeeDAO();
                user.setEmployee(empDao.findById(user.getEmployeeNumber()));
            }
            return new Session(user);
        }
        return null;
    }
}