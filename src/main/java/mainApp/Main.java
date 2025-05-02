package mainApp;

import views.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setTitle("FastTrack Logistics");
            loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginView.setSize(800, 600);
            loginView.setLocationRelativeTo(null); //Center window
            loginView.setVisible(true);
        });
    }
}
