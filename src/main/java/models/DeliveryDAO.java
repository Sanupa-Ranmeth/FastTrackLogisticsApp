package models;

import com.mysql.cj.protocol.Resultset;
import utilities.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO {
    //This method will add the Shipment to the shipment table first, then add it to Delivery table
    public boolean createDelivery (int senderID, Shipment shipment, Delivery delivery) {
        String insertShipmentSQL = "INSERT INTO Shipment (SenderID, ReceiverName, Destination, DestinationAddress, Contents, isUrgent, preferredTimeSlot, DeliveryDate, Status)" +
                                   "VALUES (?,?,?,?,?,?,?,?,?)";
        String insertDeliverySQL = "INSERT INTO Delivery (ShipmentID, DriverID) VALUES (?,?)";

        //Use a database transaction
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            //Inserting into Shipment
            try (PreparedStatement shipmentStmt = conn.prepareStatement(insertShipmentSQL, Statement.RETURN_GENERATED_KEYS)) {
                shipmentStmt.setInt(1, senderID);
                shipmentStmt.setString(2, shipment.getReceiverName());
                shipmentStmt.setObject(3, shipment.getDestination());
                shipmentStmt.setString(4, shipment.getDestinationAddress());
                shipmentStmt.setString(5, shipment.getContent());
                shipmentStmt.setBoolean(6, shipment.isUrgent());
                shipmentStmt.setObject(7, shipment.getPreferredTimeSlot());
                shipmentStmt.setDate(8, shipment.getDeliveryDate() != null ? new java.sql.Date(shipment.getDeliveryDate().getTime()) : null);
                shipmentStmt.setString(9, shipment.getStatus());

                int affectedRows = shipmentStmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    System.out.println("Inserting Shipment Failed");
                    return false;
                }

                //Get generated shipmentID
                try (ResultSet generatedKeys = shipmentStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int shipmentID = generatedKeys.getInt(1);

                        //Inserting into delivery
                        try (PreparedStatement deliveryStmt = conn.prepareStatement(insertDeliverySQL)) {
                            deliveryStmt.setInt(1, shipmentID);
                            deliveryStmt.setInt(2, delivery.getDriverID());

                            if (deliveryStmt.executeUpdate() == 0) {
                                conn.rollback();
                                System.out.println("Inserting Delivery Failed");
                                return false;
                            }

                            //If both succeed, commit transaction
                            conn.commit();
                            return true;
                        }
                    } else {
                        conn.rollback();
                        System.out.println("Getting shipmentID failed");
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction Failed: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database Connection or Rollback Failed: " + e.getMessage());
            return false;
        }
    }

    //Both Shipment and Delivery tables should be updated
    public boolean updateDelivery (Shipment shipment, Delivery delivery) {
        String updateShipmentSQL = "UPDATE Shipment SET ReceiverName = ?, Destination = ?, DestinationAddress = ?, Contents = ?, isUrgent = ?, preferredTimeSlot = ?, DeliveryDate = ?, Status = ? WHERE ShipmentID = ?";
        String updateDeliverySQL = "UPDATE Delivery SET DriverID = ?, Location = ?, isDelayed = ?, Delay = ?, EstimatedDateTime = ? WHERE ShipmentID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            //Update Shipment table
            try (PreparedStatement shipmentStmt = conn.prepareStatement(updateShipmentSQL)) {
                shipmentStmt.setString(1, shipment.getReceiverName());
                shipmentStmt.setObject(2, shipment.getDestination());
                shipmentStmt.setString(3, shipment.getDestinationAddress());
                shipmentStmt.setString(4, shipment.getContent());
                shipmentStmt.setBoolean(5, shipment.isUrgent());
                shipmentStmt.setObject(6, shipment.getPreferredTimeSlot());
                shipmentStmt.setDate(7, shipment.getDeliveryDate() != null ? new java.sql.Date(shipment.getDeliveryDate().getTime()) : null);
                shipmentStmt.setString(8, shipment.getStatus());
                shipmentStmt.setInt(9, shipment.getShipmentID());

                int shipmentUpdated = shipmentStmt.executeUpdate();
                if (shipmentUpdated == 0) {
                    conn.rollback();
                    System.out.println("Shipment Update Failed");
                    return false;
                }
            }

            //Update Delivery table
            try (PreparedStatement deliveryStmt = conn.prepareStatement(updateDeliverySQL)) {
                deliveryStmt.setInt(1, delivery.getDriverID());
                deliveryStmt.setObject(2, delivery.getLocation());
                deliveryStmt.setBoolean(3, delivery.isDelayed());
                deliveryStmt.setObject(4, delivery.getDelay());
                deliveryStmt.setTimestamp(5, delivery.getEstimatedDeliveryDate() != null ? Timestamp.valueOf(delivery.getEstimatedDeliveryDate()) : null);
                deliveryStmt.setInt(6, delivery.getShipmentID());

                int deliveryUpdated = deliveryStmt.executeUpdate();
                if (deliveryUpdated == 0) {
                    conn.rollback();
                    System.out.println("Delivery Update Failed");
                    return false;
                }
            }

            //If both succeed, commit them
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Update Delivery Transaction Failed: " + e.getMessage());
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
        // Define allowed status values based on your database schema constraints
        final String[] ALLOWED_STATUSES = {"Pending", "Approved", "In Transit", "Delivered", "Failed Delivery", "Delay"};

        // Validate status
        boolean validStatus = false;
        for (String allowedStatus : ALLOWED_STATUSES) {
            if (allowedStatus.equals(status)) {
                validStatus = true;
                break;
            }
        }

        if (!validStatus) {
            System.out.println("Invalid status value: " + status);
            return false;
        }

        Connection connection = null;
        PreparedStatement updateShipmentStmt = null;
        PreparedStatement updateDeliveryStmt = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);  // Start transaction

            // Update Shipment status
            String updateShipmentSQL = "UPDATE Shipment SET Status = ? WHERE ShipmentID = ?";
            updateShipmentStmt = connection.prepareStatement(updateShipmentSQL);
            updateShipmentStmt.setString(1, status);
            updateShipmentStmt.setInt(2, shipmentID);
            int shipmentUpdated = updateShipmentStmt.executeUpdate();

            // Update Delivery details
            String updateDeliverySQL = "UPDATE Delivery SET Location = ?, isDelayed = ?, Delay = ? WHERE ShipmentID = ?";
            updateDeliveryStmt = connection.prepareStatement(updateDeliverySQL);

            // Set location (may be null)
            if (location != null) {
                updateDeliveryStmt.setInt(1, location);
            } else {
                updateDeliveryStmt.setNull(1, java.sql.Types.INTEGER);
            }

            // Set isDelayed based on delay value
            updateDeliveryStmt.setBoolean(2, delay > 0);
            updateDeliveryStmt.setInt(3, delay);
            updateDeliveryStmt.setInt(4, shipmentID);

            int deliveryUpdated = updateDeliveryStmt.executeUpdate();

            connection.commit();  // Commit transaction
            return (shipmentUpdated > 0 && deliveryUpdated > 0);

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();  // Rollback transaction on error
                }
            } catch (SQLException ex) {
                System.out.println("Failed to rollback transaction: " + ex.getMessage());
            }

            System.out.println("Update Delivery Operations Transaction Failed: " + e.getMessage());
            return false;

        } finally {
            try {
                if (updateShipmentStmt != null) updateShipmentStmt.close();
                if (updateDeliveryStmt != null) updateDeliveryStmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);  // Reset auto-commit
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close resources: " + e.getMessage());
            }
        }
    }

    public Object[][] getAssignedShipments(int driverID) {
        List<Object[]> shipments = new ArrayList<>();
        String sql = "SELECT s.ShipmentID, u.Username AS Sender, s.ReceiverName, c.CityName, s.DestinationAddress, " +
                "s.Contents, s.Status, s.isUrgent, d.EstimatedDateTime, cl.CityName AS CurrentLocation " +
                "FROM Shipment s " +
                "JOIN Delivery d ON s.ShipmentID = d.ShipmentID " +
                "JOIN `User` u ON s.SenderID = u.UserID " +
                "JOIN City c ON s.Destination = c.CityID " +
                "LEFT JOIN City cl ON d.Location = cl.CityID " +
                "WHERE d.DriverID = ? AND s.Status NOT IN ('Delivered', 'Cancelled')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                shipments.add(new Object[] {
                        rs.getInt("ShipmentID"),
                        rs.getString("Sender"),
                        rs.getString("ReceiverName"),
                        rs.getString("CityName"),
                        rs.getString("DestinationAddress"),
                        rs.getString("Contents"),
                        rs.getString("Status"),
                        rs.getBoolean("isUrgent"),
                        rs.getTimestamp("EstimatedDateTime") != null ?
                                rs.getTimestamp("EstimatedDateTime").toLocalDateTime() : null,
                        rs.getString("CurrentLocation")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assigned shipments: " + e.getMessage());
        }

        return shipments.toArray(new Object[0][0]);
    }

    public boolean updateDeliveryStatus(int shipmentID, String status, LocalDateTime actualDeliveryTime) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update shipment status
            String shipmentSql = "UPDATE Shipment SET Status = ? WHERE ShipmentID = ?";
            try (PreparedStatement shipmentStmt = conn.prepareStatement(shipmentSql)) {
                shipmentStmt.setString(1, status);
                shipmentStmt.setInt(2, shipmentID);

                int shipmentUpdated = shipmentStmt.executeUpdate();
                if (shipmentUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Update delivery actual time if delivered
            if (actualDeliveryTime != null) {
                String deliverySql = "UPDATE Delivery SET ActualDeliveryDateTime = ? WHERE ShipmentID = ?";
                try (PreparedStatement deliveryStmt = conn.prepareStatement(deliverySql)) {
                    deliveryStmt.setTimestamp(1, java.sql.Timestamp.valueOf(actualDeliveryTime));
                    deliveryStmt.setInt(2, shipmentID);

                    int deliveryUpdated = deliveryStmt.executeUpdate();
                    if (deliveryUpdated == 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update delivery status: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to reset auto-commit or close connection: " + e.getMessage());
                }
            }
        }
    }

    public Object[][] getDriverDeliveryHistory(int driverID) {
        List<Object[]> history = new ArrayList<>();
        String sql = "SELECT s.ShipmentID, d.ActualDeliveryDateTime, s.Status, s.ReceiverName, d.Rating " +
                "FROM Shipment s " +
                "JOIN Delivery d ON s.ShipmentID = d.ShipmentID " +
                "WHERE d.DriverID = ? AND s.Status IN ('Delivered', 'Failed Delivery') " +
                "ORDER BY d.ActualDeliveryDateTime DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new Object[] {
                        rs.getInt("ShipmentID"),
                        rs.getTimestamp("ActualDeliveryDateTime") != null ?
                                rs.getTimestamp("ActualDeliveryDateTime").toLocalDateTime() : null,
                        rs.getString("Status"),
                        rs.getString("ReceiverName"),
                        rs.getObject("Rating") != null ? rs.getInt("Rating") : "Not Rated"
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to get driver delivery history: " + e.getMessage());
        }

        return history.toArray(new Object[0][0]);
    }
}
