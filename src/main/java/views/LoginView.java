package views;

import controllers.UserController;
import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JPanel LoginBackPanel;
    private JTextField txtLoginUsername;
    private JPasswordField txtLoginPassword;
    private JButton btnLogin;
    private JTextField txtRegisterUsername;
    private JPasswordField txtRegisterPassword;
    private JTextField txtRegisterEmail;
    private JButton btnRegister;

    UserController userController;

    public LoginView() {
        setContentPane(LoginBackPanel); //Can't do this in the main method cause loginBackPanel is public

        userController = new UserController();

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtRegisterUsername.getText();
                String password = new String(txtRegisterPassword.getPassword());
                String email = txtRegisterEmail.getText();

                if (userController.registerUser(username, password, email)) {
                    JOptionPane.showMessageDialog(LoginBackPanel, "Registration Successful", "Success", 1);
                    txtRegisterUsername.setText("");
                    txtRegisterPassword.setText("");
                    txtRegisterEmail.setText("");
                }
                else {
                    JOptionPane.showMessageDialog(LoginBackPanel, "Registration Failed", "Failure", 1);
                }
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtLoginUsername.getText();
                String password = new String(txtLoginPassword.getPassword());

                User user = userController.loginUser(username, password);

                if (user != null && user.getRole().equals("customer")) {
                    new CustomerView(user.getUsername()).setVisible(true);
                    dispose(); //Gets rid of LoginView
                }
                else if (user != null && user.getRole().equals("admin")) {
                    new AdminView(user.getUsername()).setVisible(true);
                    dispose();
                }
                else if (user != null && user.getRole().equals("driver")) {
                    new DriverView(user.getUsername()).setVisible(true);
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(LoginBackPanel, "Login Failed", "Failure", 1);
                }
            }
        });
    }
}
