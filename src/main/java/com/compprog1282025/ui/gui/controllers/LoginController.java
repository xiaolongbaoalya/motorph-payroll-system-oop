package com.compprog1282025.ui.gui.controllers;

import com.compprog1282025.dao.UserDAO;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.AuthService;
import com.compprog1282025.ui.gui.util.SessionContext;
import com.compprog1282025.ui.gui.util.AlertHelper;
import com.compprog1282025.ui.gui.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final UserDAO userDao = new UserDAO();
    private final AuthService authService = new AuthService(userDao);

    @FXML
    private void onLoginButtonClick(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        Session session = authService.login(username, password);

        if (session != null && session.isActive()) {
            SessionContext.getInstance().setCurrentSession(session);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // All roles currently use the unified dashboard shell; HR gets extra panes inside it.
            SceneSwitcher.toDashboard(stage);
            
        } else {
            showErrorMessage("Invalid username or password.");
        }
    }

    @FXML
    private void onForgotPasswordClick(ActionEvent event) {
        AlertHelper.showInfo("Forgot Password", "Please contact IT to reset your password.");
    }

    private void showErrorMessage(String message) {
        lblError.setText(message);
        lblError.getStyleClass().add("text-danger");
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}

