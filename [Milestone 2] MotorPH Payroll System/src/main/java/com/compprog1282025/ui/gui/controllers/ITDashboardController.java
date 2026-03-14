/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.service.ITService;
import com.compprog1282025.model.user.Session;
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
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 * @author grazi
 */

public class ITDashboardController extends BaseController {

    @FXML private Label systemStatusLabel;
    @FXML private Label databasePathLabel;
    @FXML private TextArea consoleLog;

    // The Backend Service that does the real work
    private final ITService itService;
    private Supplier<Session> sessionSupplier;
    private BiConsumer<String, String> alertCallback;

    public ITDashboardController() {
        this.itService = new ITService();
    }

    public ITDashboardController(
            ITService itService,
            Supplier<Session> sessionSupplier,
            BiConsumer<String, String> alertCallback,
            Label systemStatusLabel,
            Label databasePathLabel,
            TextArea consoleLog
    ) {
        this.itService = itService;
        this.sessionSupplier = sessionSupplier;
        this.alertCallback = alertCallback;
        this.systemStatusLabel = systemStatusLabel;
        this.databasePathLabel = databasePathLabel;
        this.consoleLog = consoleLog;
    }

    @FXML
    public void initialize() {
        initializePane("src/main/resources/data/");
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
        runSystemAudit();
    }

    @FXML
    private void handleSystemHealth() {
        runSystemHealth();
    }

    @FXML
    private void handleDatabaseTools() {
        runDatabaseTools();
    }

    @FXML
    private void handleUserManagement() {
        runUserManagement();
    }

    @FXML
    private void handleDatabaseBackup() {
        runDatabaseBackup();
    }

    public void initializePane(String databasePathLabelText) {
        Session currentSession = resolveSession();
        String currentUsername = currentSession != null && currentSession.getUser() != null
                ? currentSession.getUser().getUsername()
                : "Unknown";

        if (systemStatusLabel != null) {
            systemStatusLabel.setText("System Online");
            systemStatusLabel.setStyle("");
        }
        if (databasePathLabel != null) {
            databasePathLabel.setText("Active Database: " + databasePathLabelText);
        }
        if (consoleLog != null) {
            consoleLog.clear();
        }
        logToConsole("IT Admin Session Started for account: " + currentUsername);
    }

    public void runSystemAudit() {
        logToConsole("Starting Database Cross-Reference Audit...");
        String report = itService.runIntegrityAudit();
        logToConsole(report);
        showInfo("Integrity Audit", report);
    }

    public void runSystemHealth() {
        logToConsole("Scanning system file status...");
        String healthReport = itService.getSystemHealth();
        logToConsole(healthReport);

        if (systemStatusLabel != null) {
            if (healthReport.contains("EXCELLENT")) {
                systemStatusLabel.setText("System: Healthy");
                systemStatusLabel.setStyle("-fx-text-fill: green;");
            } else {
                systemStatusLabel.setText("System: Warning");
                systemStatusLabel.setStyle("-fx-text-fill: red;");
            }
        }
        showInfo("Health Report", healthReport);
    }

    public void runDatabaseTools() {
        logToConsole("Optimizing database storage (Trimming & Cleaning)...");
        try {
            itService.optimizeDatabase();
            logToConsole("[SUCCESS] All CSV files have been trimmed and optimized.");
            showInfo("Database Tools", "Maintenance finished. Files are now clean.");
        } catch (IOException e) {
            logToConsole("[ERROR] Optimization failed: " + e.getMessage());
        }
    }

    public void runUserManagement() {
        List<String> userList = itService.getAllUsernames();

        if (userList.isEmpty()) {
            logToConsole("[ERROR] No users found in database.");
            showError("System Error", "The user database appears to be empty.");
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
                            showInfo("Success", "Password updated successfully for " + username);
                        } catch (IOException e) {
                            logToConsole("[ERROR] Failed to update password: " + e.getMessage());
                            showError("Update Failed", e.getMessage());
                        }
                    } else {
                        logToConsole("[CANCELLED] Password reset for " + username + " aborted by user.");
                    }
                }
            });
        });
    }

    public void runDatabaseBackup() {
        logToConsole("Initializing System Backup...");
        try {
            // Calling the backend service
            List<String> backupLogs = itService.performBackup();
            
            for (String log : backupLogs) {
                logToConsole(log);
            }
            
            showInfo("Backup Complete", "System data saved to /backups folder.");
        } catch (IOException e) {
            logToConsole("[CRITICAL] Backup Failed: " + e.getMessage());
            showError("Backup Failed", e.getMessage());
        }
    }

    private void logToConsole(String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (consoleLog != null) {
            consoleLog.appendText("[" + time + "] " + message + "\n");
        }
        System.out.println(message);
    }

    private Session resolveSession() {
        if (sessionSupplier != null) {
            return sessionSupplier.get();
        }
        return SessionContext.getInstance().getCurrentSession();
    }

    private void showInfo(String title, String message) {
        if (alertCallback != null) {
            alertCallback.accept(title, message);
        } else {
            AlertHelper.showInfo(title, message);
        }
    }

    private void showError(String title, String message) {
        if (alertCallback != null) {
            alertCallback.accept(title, message);
        } else {
            AlertHelper.showError(title, message);
        }
    }
}
