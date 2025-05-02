package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelDAO {
    public boolean addDeliveryPersonnel(DeliveryPersonnel driver) {
        String sql = "INSERT INTO Driver (DriverID, DriverName, Schedule, RouteID) " +
                     "SELECT UserID, ?, ?, ? FROM [User] WHERE Role = 'driver' AND UserID NOT IN (SELECT DriverID FROM Driver)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getDriverName());
            stmt.setString(2, driver.getSchedule());
            stmt.setInt(3, driver.getRouteID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Delivery Personnel Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDeliveryPersonnel(DeliveryPersonnel driver) {
        String sql = "UPDATE Driver SET DriverName = ?, Schedule = ?, RouteID = ? WHERE DriverID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getDriverName());
            stmt.setString(2, driver.getSchedule());
            stmt.setInt(3, driver.getRouteID());
            stmt.setInt(4, driver.getDriverID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update Delivery Personnel Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDeliveryPersonnel(int driverID) {
        String sql = "DELETE FROM Driver WHERE DriverID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete Delivery Personnel Failed: " + e.getMessage());
            return false;
        }
    }

    public List<DeliveryPersonnel> getAllDeliveryPersonnel() {
        List<DeliveryPersonnel> drivers = new ArrayList<>();
        String sql = "SELECT * FROM Driver";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(new DeliveryPersonnel(
                   rs.getInt("DriverID"),
                   rs.getString("DriverName"),
                   rs.getString("Schedule"),
                   rs.getInt("RouteID")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Get All Delivery Personnel Failed: " + e.getMessage());
        }
        return drivers;
    }
}