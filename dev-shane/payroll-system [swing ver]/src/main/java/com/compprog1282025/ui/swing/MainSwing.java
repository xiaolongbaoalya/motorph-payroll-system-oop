package com.compprog1282025.ui.swing;

import javax.swing.SwingUtilities;

public class MainSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext appContext = new AppContext();
            LoginFrame loginFrame = new LoginFrame(appContext);
            loginFrame.setVisible(true);
        });
    }
}
