package com.compprog1282025.ui.terminal;

import com.compprog1282025.service.ITService;

import java.util.List;
import java.util.Scanner;

public class ITMenu extends BaseMenu {
    private final ITService itService;

    public ITMenu(ITService itService) {
        this.itService = itService;
    }

    public void displayITMenu(Scanner scanner) {
        String choice = "default";
        while (!"0".equalsIgnoreCase(choice)) {
            displayHeader("IT Menu");
            System.out.println("1. View system health");
            System.out.println("2. Run integrity audit");
            System.out.println("3. Optimize database");
            System.out.println("4. Perform backup");
            System.out.println("5. List users");
            System.out.println("6. Reset user password");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            choice = scanner.nextLine().trim();

            switch (choice) {
                case "0":
                    break;
                case "1":
                    displaySubHeader("System Health");
                    System.out.println(itService.getSystemHealth());
                    break;
                case "2":
                    displaySubHeader("Integrity Audit");
                    System.out.println(itService.runIntegrityAudit());
                    break;
                case "3":
                    optimizeDatabase();
                    break;
                case "4":
                    performBackup();
                    break;
                case "5":
                    listUsers();
                    break;
                case "6":
                    resetUserPassword(scanner);
                    break;
                default:
                    System.out.println("Invalid input, please try again");
            }
        }
    }

    private void optimizeDatabase() {
        displaySubHeader("Optimize Database");
        try {
            itService.optimizeDatabase();
            System.out.println("Database cleanup completed successfully.");
        } catch (Exception e) {
            System.out.println("Database optimization failed: " + e.getMessage());
        }
    }

    private void performBackup() {
        displaySubHeader("Perform Backup");
        try {
            List<String> log = itService.performBackup();
            if (log.isEmpty()) {
                System.out.println("No files were backed up.");
                return;
            }
            log.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private void listUsers() {
        displaySubHeader("Users");
        List<String> usernames = itService.getAllUsernames();
        if (usernames.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        usernames.forEach(System.out::println);
    }

    private void resetUserPassword(Scanner scanner) {
        displaySubHeader("Reset User Password");
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            itService.resetUserPassword(username, newPassword);
            System.out.println("Password reset successfully.");
        } catch (Exception e) {
            System.out.println("Password reset failed: " + e.getMessage());
        }
    }
}
