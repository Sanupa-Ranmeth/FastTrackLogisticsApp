import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import views.LoginView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Setting FlatLaf as Look and Feel
            try {
                FlatMacLightLaf.setup();
            } catch (Exception e) {
                System.out.println("Failed to initialize FlatLaF");
            }

            //Fully rounded corners and buttons
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);

            LoginView loginView = new LoginView();
            loginView.setTitle("FastTrack Logistics");
            loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginView.setSize(800, 600);
            loginView.setLocationRelativeTo(null); //Center window
            loginView.setVisible(true);
        });
    }
}
