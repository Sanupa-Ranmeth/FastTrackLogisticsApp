package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {
        String SQL = "INSERT INTO [User] (Username, Password, Email, Role) VALUES (?, ?, ?, 'customer')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Registration Failed: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String SQL = "SELECT * FROM [User] WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("Email"),
                    rs.getString("Role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Login Failed: " + e.getMessage());
        }
        return null;
    }

}
