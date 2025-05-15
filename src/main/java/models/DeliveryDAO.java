package models;

import utilities.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO {
    public boolean createDelivery (Delivery delivery) {
        String sql = "INSERT INTO Delivery (ShipmentID, DriverID) VALUES (?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delivery.getShipmentID());
            stmt.setObject(2, delivery.getDriverID() == -1 ? null : delivery.getDriverID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Create Delivery Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDelivery (Delivery delivery) {
        String sql = "UPDATE Delivery SET Location = ?, isDelayed = ?, Delay = ?, EstimatedDateTime = ?, ActualDeliveryDateTime = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, delivery.getLocation());
            stmt.setBoolean(2, delivery.isDelayed());
            stmt.setObject(3, delivery.getDelay());
            stmt.setObject(4, delivery.getEstimatedDeliveryDate());
            stmt.setObject(5, delivery.getActualDeliveryDateTime());
            stmt.setInt(6, delivery.getShipmentID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Delivery Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDelivery (int shipmentID) {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            //Delete from Delivery first
            String deliverySQL = "DELETE FROM Delivery WHERE ShipmentID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deliverySQL)) {
                stmt.setInt(1, shipmentID);
                stmt.executeUpdate();
            }

            //Then delete from Shipment
            String shipmentSQL = "DELETE FROM Shipment WHERE ShipmentID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(shipmentSQL)) {
                stmt.setInt(1, shipmentID);
                int rowsAffected = stmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    System.out.println("Rollback Failed: " + e1.getMessage());
                }
            }
            System.out.println("Delete Delivery Failed: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Connection close Failed: " + e.getMessage());
                }
            }
        }
    }

    public Delivery getDeliverybyShipmentID (int shipmentID) {
        String sql = "SELECT * FROM Delivery WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Delivery(
                            rs.getInt("ShipmentID"),
                            rs.getObject("DriverID") != null ? (rs.getInt("DriverID")) : null,
                            rs.getObject("Location") != null ? rs.getInt("Location") : null,
                            rs.getObject("Rating") != null ? rs.getInt("Rating") : null,
                            rs.getBoolean("isDelayed"),
                            rs.getObject("Delay") != null ? rs.getInt("Delay") : null,
                            rs.getObject("EstimatedDateTime") != null ? rs.getTimestamp("EstimatedDateTime").toLocalDateTime() : null,
                            rs.getObject("ActualDeliveryDateTime") != null ? rs.getTimestamp("ActualDeliveryDateTime").toLocalDateTime() : null
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Fetch delivery failed: " + e.getMessage());
        }
        return null;
    }

    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Delivery";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deliveries.add(new Delivery(
                        rs.getInt("ShipmentID"),
                        rs.getObject("DriverID") != null ? (rs.getInt("DriverID")) : null,
                        rs.getObject("Location") != null ? rs.getInt("Location") : null,
                        rs.getObject("Rating") != null ? rs.getInt("Rating") : null,
                        rs.getBoolean("isDelayed"),
                        rs.getObject("Delay") != null ? rs.getInt("Delay") : null,
                        rs.getObject("EstimatedDateTime") != null ? rs.getTimestamp("EstimatedDateTime").toLocalDateTime() : null,
                        rs.getObject("ActualDeliveryDateTime") != null ? rs.getTimestamp("ActualDeliveryDateTime").toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fetching all deliveries failed: " + e.getMessage());
        }
        return deliveries;
    }

    //------------------------------------- DELIVERY OPERATIONS --------------------------------------------------------

    public boolean approveShipment(int shipmentID, int driverID) {
        //Status update will be handled by ShipmentController and shipmentDAO
        String sql = "INSERT INTO Delivery (ShipmentID, DriverID, Location, Rating, isDelayed, Delay, EstimatedDateTime, ActualDeliveryDateTime) VALUES (?, ?, DEFAULT, NULL, FALSE, NULL, NULL, NULL) " +
                     "ON DUPLICATE KEY UPDATE DriverID = ?, Location = NULL, Rating = NULL, isDelayed = FALSE, Delay = NULL,  EstimatedDateTime = NULL, ActualDeliveryDateTime = NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentID);
            stmt.setInt(2, driverID);
            stmt.setInt(3, driverID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to Approve Shipment: " + e.getMessage());
            return false;
        }
    }

    public boolean disapproveShipment(int shipmentID) {
        //Status update will be handled by ShipmentController and ShipmentDAO
        String sql = "INSERT INTO Delivery (ShipmentID, DriverID, Location, Rating, isDelayed, Delay, EstimatedDateTime, ActualDeliveryDateTime) VALUES (?, NULL, NULL, NULL, FALSE, NULL, NULL, NULL)" +
                     "ON DUPLICATE KEY UPDATE DriverID = NULL, Location = NULL, Rating = NULL, isDelayed = FALSE, Delay = NULL, EstimatedDateTime = NULL, ActualDeliveryDateTime = NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to Disapprove Shipment: " + e.getMessage());
            return false;
        }
    }

    public boolean setDeliveryEstimation(int shipmentID, LocalDateTime estimatedDateTime) {
        String sql = "UPDATE Delivery SET EstimatedDateTime = ? WHERE ShipmentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(estimatedDateTime));
            stmt.setInt(2, shipmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to set delivery estimation: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDeliveryOperations(int shipmentID, String status, Integer location, int delay) {
        String updateShipmentSQL = "UPDATE Shipment SET Status = ? WHERE ShipmentID = ?";
        String updateDeliverySQL = "UPDATE Delivery SET Location = ?, isDelayed = ?, Delay = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateShipmentSQL);
                 PreparedStatement stmt2 = conn.prepareStatement(updateDeliverySQL)) {

                //Updating shipment table
                stmt1.setString(1, status);
                stmt1.setInt(2, shipmentID);
                int shipmentRows = stmt1.executeUpdate();

                //Updating Delivery table
                stmt2.setObject(1, location);
                if (delay > 0) {
                    stmt2.setBoolean(2, true);
                } else {
                    stmt2.setBoolean(2, false);
                }
                stmt2.setInt(3, delay);
                stmt2.setInt(4, shipmentID);
                int DeliveryRows = stmt2.executeUpdate();

                if (shipmentRows > 0 && DeliveryRows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Update Delivery Operations Transaction Failed: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }
}
