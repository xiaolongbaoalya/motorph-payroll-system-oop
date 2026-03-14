package com.compprog1282025.ui.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public static void toDashboard(Stage stage) {
        switchTo(stage, "/com/compprog1282025/ui/gui/views/dashboard/Dashboard.fxml");
    }

    public static void toHRTools(Stage stage) {
        switchTo(stage, "/com/compprog1282025/ui/gui/views/modules/hr/HRTools.fxml");
    }

    public static void toLogin(Stage stage) {
        switchTo(stage, "/com/compprog1282025/ui/gui/views/auth/Login.fxml");
    }

    public static void switchTo(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
