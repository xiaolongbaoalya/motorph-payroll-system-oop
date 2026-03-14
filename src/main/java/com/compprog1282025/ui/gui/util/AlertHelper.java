package com.compprog1282025.ui.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

// This class helps us show pop-up messages to the user.
// It makes sure all our alerts look the same across the whole app.
public class AlertHelper {

    // Shows a red error message (useful for wrong passwords or system errors)
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Keeps it clean and simple
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Shows a blue info message (useful for success confirmations)
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Shows a yellow warning message with a "Yes/No" choice
    // Useful for things like "Are you sure you want to log out?"
    public static boolean showConfirmation(String title, String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // This shows the dialog and waits for the user to click OK or Cancel
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

        // Returns true if they clicked OK, false otherwise
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }
}