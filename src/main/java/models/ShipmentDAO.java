package models;

import com.mysql.cj.protocol.Resultset;
import utilities.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {
    public boolean addShipment(Shipment shipment, int senderID) {
        String sql = "INSERT INTO Shipment (SenderID, ReceiverName, Destination , DestinationAddress, Contents, isUrgent, preferredTimeSlot, DeliveryDate, Status) VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderID);
            stmt.setString(2, shipment.getReceiverName());
            stmt.setObject(3, shipment.getDestination());
            stmt.setString(4, shipment.getDestinationAddress());
            stmt.setString(5, shipment.getContent());
            stmt.setBoolean(6, shipment.isUrgent());
            stmt.setObject(7, shipment.getPreferredTimeSlot()); //Handles NULL values
            stmt.setDate(8, shipment.getDeliveryDate() != null ? new java.sql.Date(shipment.getDeliveryDate().getTime()) : null);
            stmt.setString(9, shipment.getStatus() != null ? shipment.getStatus() : "Pending");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Shipment Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateShipment(Shipment shipment) {
        String sql = "UPDATE Shipment SET ReceiverName = ?, Destination = ?, DestinationAddress = ?, Contents = ?, isUrgent = ?, preferredTimeSlot = ?, DeliveryDate = ?, Status = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shipment.getReceiverName());
            stmt.setObject(2, shipment.getDestination());
            stmt.setString(3, shipment.getDestinationAddress());
            stmt.setString(4, shipment.getContent());
            stmt.setBoolean(5, shipment.isUrgent());
            stmt.setObject(6, shipment.getPreferredTimeSlot());
            stmt.setDate(7, shipment.getDeliveryDate() != null ? new java.sql.Date(shipment.getDeliveryDate().getTime()) : null);
            stmt.setString(8, shipment.getStatus());
            stmt.setInt(9, shipment.getShipmentID());

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
        String sql = "SELECT PackageID, SentTo, Destination, DestinationAddress, Content, Status, DeliveryDate, Urgent, TimeSlot FROM CustomerShipmentView WHERE SenderID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderID);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                shipmentData.add(new Object[] {
                   rs.getInt("PackageID"),
                   rs.getString("SentTo"),
                   rs.getString("Destination"),
                   rs.getString("DestinationAddress"),
                   rs.getString("Content"),
                   rs.getString("Status"),
                   rs.getString("DeliveryDate"),
                   rs.getBoolean("Urgent"),
                   rs.getString("TimeSlot")
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

    //------------------------------------- ADMIN-ONLY FUNCTIONS -------------------------------------------------------

    public Object[][] getAllAdminShipments() {
        List<Object[]> shipmentData = new ArrayList<>();
        String sql = "SELECT * FROM AdminShipmentView";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                shipmentData.add(new Object[] {
                   rs.getInt("ShipmentID"),
                   rs.getInt("SenderID"),
                   rs.getString("ReceiverName"),
                   rs.getString("Destination"),
                   rs.getString("DestinationAddress"),
                   rs.getString("Contents"),
                   rs.getBoolean("isUrgent"),
                   rs.getString("preferredTimeSlot"),
                   rs.getDate("DeliveryDate"),
                   rs.getInt("DriverID"),
                   rs.getString("DriverName"),
                   rs.getString("Status"),
                   rs.getString("Location"),
                   rs.getInt("Delay"),
                   rs.getTimestamp("EstimatedDateTime"),
                   rs.getTimestamp("ActualDeliveryTime")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Load Shipments: " + e.getMessage());
        }
        return shipmentData.toArray(new Object[0][0]);
    }

    //Filter by status
    public Object[][] getAdminShipmentsByStatus(String status) {
        List<Object[]> shipmentData = new ArrayList<>();
        String sql = "SELECT * FROM AdminShipmentView WHERE Status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shipmentData.add(new Object[] {
                        rs.getInt("ShipmentID"),
                        rs.getInt("SenderID"),
                        rs.getString("ReceiverName"),
                        rs.getString("Destination"),
                        rs.getString("DestinationAddress"),
                        rs.getString("Contents"),
                        rs.getBoolean("isUrgent"),
                        rs.getString("preferredTimeSlot"),
                        rs.getDate("DeliveryDate"),
                        rs.getInt("DriverID"),
                        rs.getString("DriverName"),
                        rs.getString("Status"),
                        rs.getString("Location"),
                        rs.getInt("Delay"),
                        rs.getTimestamp("EstimatedDateTime"),
                        rs.getTimestamp("ActualDeliveryTime")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Filter Shipments: " + e.getMessage());
        }
        return shipmentData.toArray(new Object[0][0]);
    }

    //Update Status method
    public boolean updateShipmentStatus(int shipmentID, String status) {
        String sql = "UPDATE Shipment SET Status = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, shipmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to Update Shipment Status: " + e.getMessage());
            return false;
        }
    }

    // get shipment status method---------------

    public Object[][] getShipmentStatus(int shipmentID) {
        List<Object[]> shipmentData = new ArrayList<>();
        String sql = "SELECT Status,Location,EstimatedDateTime,Delay FROM AdminShipmentView WHERE shipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                shipmentData.add(new Object[] {
                        rs.getString("Status"),
                        rs.getString("Location"),
                        rs.getTimestamp("EstimatedDateTime"),
                        rs.getInt("Delay")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get Shipment Status: " + e.getMessage());
        }
        return shipmentData.toArray(new Object[0][0]);
    }
}
