package com.compprog1282025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// This is the "Main Entry Point" for the MotorPH Graphical User Interface.
public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Path to your login screen - ensure this matches your folder structure!
            String fxmlPath = "/com/compprog1282025/ui/gui/views/auth/Login.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            // CHANGED THIS PART: Set a fixed size (Width, Height) for a professional look.
            // 1024x768 prevents the UI from looking squashed.
            Scene scene = new Scene(root, 1024, 768);

            primaryStage.setTitle("MotorPH Payroll System - Login");

            // Prevent the window from being resized too small by the user
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true); // Changed to true so the 1024x768 size works properly

            primaryStage.setScene(scene);
            primaryStage.centerOnScreen(); // Centers the window on the monitor
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Critical Error: Could not load Login.fxml. Check your file paths!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // This launches the JavaFX engine
        launch(args);
    }
}