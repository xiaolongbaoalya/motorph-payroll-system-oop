/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.service.ITService;
import com.compprog1282025.ui.gui.util.AlertHelper;
import com.compprog1282025.ui.gui.util.SceneSwitcher;
import com.compprog1282025.ui.gui.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author grazi
 */

public class ITDashboardController extends BaseController {

    @FXML private Label systemStatusLabel;
    @FXML private Label databasePathLabel;
    @FXML private TextArea consoleLog;

    // The Backend Service that does the real work
    private final ITService itService = new ITService();

    @FXML
    public void initialize() {
        // Dynamic username retrieval from Session
        String currentUsername = SessionContext.getInstance().getCurrentSession().getUser().getUsername();
        
        systemStatusLabel.setText("System Online");
        databasePathLabel.setText("Active Database: src/main/resources/data/");
        logToConsole("IT Admin Session Started for account: " + currentUsername);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionContext.getInstance().clear();
        logToConsole("Session cleared. Redirecting to login...");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneSwitcher.switchTo(stage, "/com/compprog1282025/ui/gui/views/auth/Login.fxml");
    }

    @FXML
    private void handleSystemAudit() {
        logToConsole("Starting Database Cross-Reference Audit...");
        String report = itService.runIntegrityAudit();
        logToConsole(report);
        AlertHelper.showInfo("Integrity Audit", report);
    }

    @FXML
    private void handleSystemHealth() {
        logToConsole("Scanning system file status...");
        String healthReport = itService.getSystemHealth();
        logToConsole(healthReport);

        if (healthReport.contains("EXCELLENT")) {
            systemStatusLabel.setText("System: Healthy");
            systemStatusLabel.setStyle("-fx-text-fill: green;");
        } else {
            systemStatusLabel.setText("System: Warning");
            systemStatusLabel.setStyle("-fx-text-fill: red;");
        }
        AlertHelper.showInfo("Health Report", healthReport);
    }

    @FXML
    private void handleDatabaseTools() {
        logToConsole("Optimizing database storage (Trimming & Cleaning)...");
        try {
            itService.optimizeDatabase();
            logToConsole("[SUCCESS] All CSV files have been trimmed and optimized.");
            AlertHelper.showInfo("Database Tools", "Maintenance finished. Files are now clean.");
        } catch (IOException e) {
            logToConsole("[ERROR] Optimization failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserManagement() {
        // DYNAMIC: Fetch real usernames from the ITService/DAO
        List<String> userList = itService.getAllUsernames();

        if (userList.isEmpty()) {
            logToConsole("[ERROR] No users found in database.");
            AlertHelper.showError("System Error", "The user database appears to be empty.");
            return;
        }

        // ChoiceDialog initialized with the first user in the list as default
        ChoiceDialog<String> dialog = new ChoiceDialog<>(userList.get(0), userList);
        dialog.setTitle("IT User Management");
        dialog.setHeaderText("Reset Employee Password");
        dialog.setContentText("Select user to reset:");

        dialog.showAndWait().ifPresent(selectedSelection -> {
            // Cleaning the input
            // Splits "aguilar.d (ID: 10031)" by space and takes index 0 -> "aguilar.d"
            String username = selectedSelection.split(" ")[0];

            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setTitle("Reset Password");
            passwordDialog.setHeaderText("Setting new password for: " + username);
            passwordDialog.setContentText("Enter new password:");

            passwordDialog.showAndWait().ifPresent(newPass -> {
                if (!newPass.trim().isEmpty()) {
                    // Confirmation dialog
                    boolean confirm = AlertHelper.showConfirmation(
                        "Confirm Password Reset", 
                        "Are you sure you want to change the password for: " + username + "?",
                        "This will overwrite the current password in the database."
                    );

                    if (confirm) {
                        try {
                            // Calling the backend servive with clean username
                            itService.resetUserPassword(username, newPass);
                            logToConsole("[SUCCESS] Password reset for " + username + " processed by ITService.");
                            AlertHelper.showInfo("Success", "Password updated successfully for " + username);
                        } catch (IOException e) {
                            logToConsole("[ERROR] Failed to update password: " + e.getMessage());
                            AlertHelper.showError("Update Failed", e.getMessage());
                        }
                    } else {
                        logToConsole("[CANCELLED] Password reset for " + username + " aborted by user.");
                    }
                }
            });
        });
    }

    @FXML
    private void handleDatabaseBackup() {
        logToConsole("Initializing System Backup...");
        try {
            // Calling the backend service
            List<String> backupLogs = itService.performBackup();
            
            for (String log : backupLogs) {
                logToConsole(log);
            }
            
            AlertHelper.showInfo("Backup Complete", "System data saved to /backups folder.");
        } catch (IOException e) {
            logToConsole("[CRITICAL] Backup Failed: " + e.getMessage());
            AlertHelper.showError("Backup Failed", e.getMessage());
        }
    }

    private void logToConsole(String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (consoleLog != null) {
            consoleLog.appendText("[" + time + "] " + message + "\n");
        }
        System.out.println(message);
    }
}