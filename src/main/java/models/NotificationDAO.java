package models;

import utilities.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public List<Notification> getAllNotifications(int userID) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notification WHERE userID = ? ORDER BY Timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification(
                            rs.getInt("notificationID"),
                            rs.getInt("userID"),
                            rs.getString("Type"),
                            rs.getString("content"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getBoolean("isRead"));
                    notifications.add(n);
                }
            }
        }catch (SQLException e) {
            System.out.println("Fetching notifications failed: " + e.getMessage());
        }
        return notifications;
    }

    /*
    public Object[][] getAllNotifications(int userID) {
        List<Object[]> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(new Object[]{
                        rs.getString("Type"),
                        rs.getString("Content"),
                        rs.getTimestamp("Timestamp")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to load notifications: " + e.getMessage());
        }
        return notifications.toArray(new Object[0][0]);
    }

     */

    public boolean saveNotification(Notification notification, int userID) {
        String sql = "INSERT INTO Notification (UserID, Type, Content) VALUES (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setString(2, notification.getType());
            stmt.setString(3, notification.getMessage());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("An error occurred while saving notification: " + e.getMessage());
            return false;
        }
    }
    public boolean deleteNotification(int notificationID){
        String sql = "DELETE FROM Notification WHERE notificationID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationID);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.out.println("Failed to Delete Notification: " + e.getMessage());
            return false;
        }
    }
    public boolean markAsRead(int notificationID){
        String sql = "UPDATE Notification SET isRead = 1 WHERE notificationID = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,notificationID);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.out.println("Failed to Mark as Read: " + e.getMessage());
            return false;
        }
    }
}
