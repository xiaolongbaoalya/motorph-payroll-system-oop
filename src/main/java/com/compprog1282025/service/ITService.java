/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.compprog1282025.service;

import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author grazi
 */
public class ITService {

    private final UserDAO userDAO = new UserDAO();

    public List<String> getAllUsernames() {
        return userDAO.getAll().stream()
                // Map to a string that combines Name and ID
                .map(u -> u.getUsername() + " (ID: " + u.getEmployeeNumber() + ")") 
                .distinct() 
                .sorted()   
                .collect(Collectors.toList());
    }

    //Resets a user's password using BCrypt hashing and updates via DAO.
    public void resetUserPassword(String username, String newPassword) throws IOException {
        User user = userDAO.getAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPasswordHash(hashed); // Matches User.java setter
            userDAO.update(user);         // Persists via UserDAO
        } else {
            throw new IOException("User " + username + " not found.");
        }
    }

    // Cross-references User objects with Employee objects to find orphaned accounts.
    // Uses data normalization (trimming/quote removal) for accuracy.
    public String runIntegrityAudit() {
        try {
            List<User> users = userDAO.getAll();
            
            //Performs a high-integrity cross-reference audit. 
            //Manually parses the raw Employee CSV to verify User records independently of the DAO layer, ensuring accuracy despite formatting mismatches or hidden BOM characters.
            java.nio.file.Path path = java.nio.file.Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "employees.csv");
            if (!java.nio.file.Files.exists(path)) {
                path = java.nio.file.Paths.get("data", "employees.csv"); // Fallback
            }

            List<String> lines = java.nio.file.Files.readAllLines(path);
            java.util.Set<Integer> validIds = new java.util.HashSet<>();

            for (String line : lines) {
                // Split by comma, but handle quotes
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 0) {
                    try {
                        // Clean the first column (Employee #)
                        String cleanId = parts[0].replaceAll("[^0-9]", "");
                        if (!cleanId.isEmpty()) {
                            validIds.add(Integer.parseInt(cleanId));
                        }
                    } catch (NumberFormatException e) {
                        // Skip header row
                    }
                }
            }

            // 2. Cross-reference users against the ID manually
            List<User> orphans = users.stream()
                .filter(u -> !validIds.contains(u.getEmployeeNumber()))
                .collect(Collectors.toList());

            if (orphans.isEmpty()) {
                return "[SUCCESS] Audit Complete: All accounts are correctly linked to Employee records.";
            } else {
                String details = orphans.stream()
                    .map(u -> u.getUsername() + " (ID: " + u.getEmployeeNumber() + ")")
                    .collect(Collectors.joining(", "));
                return "[WARNING] Orphaned accounts found: " + details;
            }
        } catch (Exception e) {
            return "[ERROR] Audit failed: " + e.getMessage();
        }
    }

    //Checks for the physical presence and health of core data files.
    public String getSystemHealth() {
        String[] fileNames = {"users.csv", "employees.csv", "attendance.csv"};
        StringBuilder report = new StringBuilder("System Report:\n");
        boolean allHealthy = true;

        String dataPath = System.getProperty("user.dir") + File.separator + "src/main/resources/data/";

        for (String fileName : fileNames) {
            File file = new File(dataPath + fileName);
            if (!file.exists()) file = new File("data" + File.separator + fileName); // Fallback path

            if (file.exists() && file.length() > 0) {
                report.append("- ").append(fileName).append(": ONLINE\n");
            } else {
                report.append("- ").append(fileName).append(": OFFLINE/MISSING\n");
                allHealthy = false;
            }
        }
        return allHealthy ? "HEALTH: EXCELLENT\n" + report : "HEALTH: POOR\n" + report;
    }

    //Cleans up data files by removing empty lines.
    public void optimizeDatabase() throws IOException {
        String[] files = {"users.csv", "employees.csv", "attendance.csv"};
        String dataPath = System.getProperty("user.dir") + File.separator + "src/main/resources/data/";

        for (String f : files) {
            File file = new File(dataPath + f);
            if (!file.exists()) file = new File("data/" + f);

            if (file.exists()) {
                List<String> lines = Files.readAllLines(file.toPath());
                List<String> cleaned = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());
                Files.write(file.toPath(), cleaned);
            }
        }
    }

    //Creates timestamped copies of the database files in a backup directory.
    public List<String> performBackup() throws IOException {
        List<String> log = new ArrayList<>();
        File backupDir = new File("backups");
        if (!backupDir.exists()) backupDir.mkdir();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String[] fileNames = {"users.csv", "employees.csv", "attendance.csv"};
        String dataPath = System.getProperty("user.dir") + File.separator + "src/main/resources/data/";

        for (String f : fileNames) {
            File source = new File(dataPath + f);
            if (!source.exists()) source = new File("data" + File.separator + f);

            if (source.exists()) {
                File dest = new File(backupDir, timestamp + "_" + f);
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.add("Backup: " + f + " copied successfully.");
            }
        }
        return log;
    }
}