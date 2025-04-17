package controllers;

import models.User;
import models.UserDAO;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    public boolean registerUser(String username, String password, String email) {
        if (username == null || password == null || email == null || username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            return false;
        }

        User user = new User(username, password, email, "customer");
        return userDAO.registerUser(user);
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        return userDAO.loginUser(username, password);
    }
}
