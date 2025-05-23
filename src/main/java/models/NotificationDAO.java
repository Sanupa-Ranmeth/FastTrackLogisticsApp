package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private UserDAO userDAO;

    public List<Notification>getAllNotifications(int userID) {
        List<Notification> notifications = new ArrayList<>();
        try{
            String sql = "SELECT * FROM notification WHERE userID = ?";
            PreparedStatement stmt = userDAO.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("notificationID"),
                        rs.getInt("userID"),
                        rs.getString("message"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getBoolean("isRead")
                );
                notifications.add(n);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    public void saveNotification(Notification notification) {
        try {
            String sql = "INSERT INTO notification (userID, message, timestamp, isRead) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = userDAO.prepareStatement(sql);
            stmt.setInt(1, notification.getUserID());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setBoolean(4, notification.isRead());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
