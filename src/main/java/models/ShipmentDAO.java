package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {
    public boolean addShipment(Shipment shipment, int senderID) {
        String sql = "INSERT INTO Shipment (SenderID, ReceiverName, DestinationAddress, Contents, isUrgent, PreferredTimeSlot) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderID);
            stmt.setString(2, shipment.getReceiverName());
            stmt.setString(3, shipment.getDestination());
            stmt.setString(4, shipment.getContent());
            stmt.setBoolean(5, shipment.isUrgent());
            stmt.setString(6, shipment.getPreferredTimeSlot());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Shipment Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateShipment(Shipment shipment) {
        String sql = "UPDATE Shipment SET ReceiverName = ?, DestinationAddress = ?, Contents = ?, isUrgent = ?, PreferredTimeSlot = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shipment.getReceiverName());
            stmt.setString(2, shipment.getDestination());
            stmt.setString(3, shipment.getContent());
            stmt.setBoolean(4, shipment.isUrgent());
            stmt.setString(5, shipment.getPreferredTimeSlot());
            stmt.setInt(6, shipment.getShipmentID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Shipment Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteShipment(int shipmentID) {
        String sql = "DELETE FROM Shipment WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, shipmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete Shipment Failed: " + e.getMessage());
            return false;
        }
    }

    public Object[][] getShipmentsByCustomer(int senderID) {
        List<Object[]> shipmentData = new ArrayList<>();
        String sql = "SELECT [Package ID], [Sent To], [Destination], [Content], [Status], [Delivery Date], [Urgent], [Time Slot] FROM CustomerShipmentView WHERE SenderID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderID);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                shipmentData.add(new Object[] {
                   rs.getInt("Package ID"),
                   rs.getString("Sent To"),
                   rs.getString("Destination"),
                   rs.getString("Content"),
                   rs.getString("Status"),
                   rs.getString("Delivery Date"),
                   rs.getBoolean("Urgent"),
                   rs.getString("Time Slot")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get Shipments: " + e.getMessage());
        }
        return shipmentData.toArray(new Object[0][0]);
    }

    public int getUserIDbyUsername(String username) {
        String sql = "SELECT UserID FROM `User` WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get User ID: " + e.getMessage());
        }
        return -1;
    }
}
