package com.compprog1282025.dao;

import com.compprog1282025.model.user.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Testing accounts, pw is hashed when stored in csv take note
 * username, password, employee num, role, enabled
 * garcia, password123, 10001, Admin, true
 * lim, password123, 10002, HR, true
 * aquino, password123, 10003, IT, true
 * reyes, password123, 10004, Finance, true
 * hernandez, password123, 10005, Operations, true
 */

public class UserDAO implements DAO<User, Integer> {
    public static final String USER_CSV_PATH = "data/users.csv";
    private final ArrayList<User> userList;

    public UserDAO() {
        this.userList = new ArrayList<>();
        loadData();
    }

    @Override
    public void loadData() {
        userList.clear();
        try (CSVReader reader = new CSVReader(new FileReader(USER_CSV_PATH))) {
            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                String username = line[0];
                String passwordHash = line[1];
                int employeeNumber = Integer.parseInt(line[2]);
                String roleStr = line[3];
                boolean enabled = Boolean.parseBoolean(line[4]);

                // Unknown or malformed role values default to employee behavior (Operations user class).
                Role role = parseRoleOrDefault(roleStr);

                switch (role) {
                    case ADMIN:
                        userList.add(new AdminUser(username, passwordHash, employeeNumber, role, enabled));
                        break;
                    case HR:
                        userList.add(new HRUser(username, passwordHash, employeeNumber, role, enabled));
                        break;
                    case IT:
                        userList.add(new ITUser(username, passwordHash, employeeNumber, role, enabled));
                        break;
                    case FINANCE:
                        userList.add(new FinanceUser(username, passwordHash, employeeNumber, role, enabled));
                        break;
                    case MKT:
                    case OPERATIONS:
                    default:
                        userList.add(new OperationsUser(username, passwordHash, employeeNumber, Role.OPERATIONS, enabled));
                        break;
                }
            }
            System.out.println("System: " + userList.size() + " users loaded for testing.");
        } catch (Exception e) {
            System.err.println("Error: Could not load users.csv at " + USER_CSV_PATH);
            e.printStackTrace();
        }
    }

    private Role parseRoleOrDefault(String roleStr) {
        if (roleStr == null) {
            return Role.OPERATIONS;
        }
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Role.OPERATIONS;
        }
    }

    public User findByUsername(String username) {
        for (User current : userList) {
            if (current.getUsername().equalsIgnoreCase(username)) {
                return current;
            }
        }
        return null;
    }

    @Override
    public void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(USER_CSV_PATH))) {
            writer.writeNext(new String[]{"username", "passwordHash", "employeeNumber", "role", "enabled"});
            for (User user : userList) {
                writer.writeNext(new String[]{
                        user.getUsername(),
                        user.getPasswordHash(),
                        String.valueOf(user.getEmployeeNumber()),
                        user.getRole().name(),
                        String.valueOf(user.isEnabled())
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(User user) {
        this.userList.add(user);
        saveData();
    }

    @Override
    public void update(User user) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getEmployeeNumber() == user.getEmployeeNumber()) {
                userList.set(i, user);
                saveData();
                return;
            }
        }
    }

    @Override
    public void delete(Integer employeeNumber) {
        userList.removeIf(user -> user.getEmployeeNumber() == employeeNumber.intValue());
        saveData();
    }

    @Override
    public User findById(Integer employeeNumber) {
        for (User current : userList) {
            if (current.getEmployeeNumber() == employeeNumber) {
                return current;
            }
        }
        return null;
    }

    @Override
    public ArrayList<User> getAll() {
        return userList;
    }
}
