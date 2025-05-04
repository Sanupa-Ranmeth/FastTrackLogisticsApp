package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelDAO {
    private UserDAO userDAO;

    public DeliveryPersonnelDAO() {
        this.userDAO = new UserDAO();
    }

    public boolean addDeliveryPersonnel(DeliveryPersonnel driver) {

        //First the driver needs to be registered as a User in USER table
        boolean userAdded = userDAO.registerUser(new User(
                driver.getUsername(),
                driver.getPassword(),
                driver.getEmail(),
                "driver"
        ));

        // userAdded = false means the user already probably exists
        if (!userAdded) {
            User existingUser = userDAO.loginUser(driver.getUsername(), driver.getPassword()); //tries to login user to confirm if he exists
            if (existingUser == null || !existingUser.getRole().equals("driver")) {
                System.out.println("Failed to add or find valid user for driver");
                return false;
            }
        }

        //Getting userID of driver
        int userID = getUserIDbyUsername(driver.getUsername());
        if (userID == -1) {
            System.out.println("UserID not found for username " + driver.getUsername());
            return false;
        }

        String sql = "INSERT INTO Driver (DriverID, DriverName, Schedule, RouteID) VALUES (?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setString(2, driver.getDriverName());
            stmt.setString(3, driver.getSchedule());
            stmt.setInt(4, driver.getRouteID());

            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                driver.setDriverID(userID); //Update DriverID
            }
            return success;
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

    //Only deletes the driver from the Driver Table
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

    //The driver needs to be deleted from the User table as well
    public boolean deleteUser (int userID) {
        String sql = "DELETE FROM `User` WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
            return false;
        }
    }

    /* Redundant for now
    public List<DeliveryPersonnel> getAllDeliveryPersonnel() {
        List<DeliveryPersonnel> drivers = new ArrayList<>();
        String sql = "SELECT d.DriverID, u.Username, u.Password, u.Email, d.DriverName, d.Schedule, d.RouteID FROM Driver d JOIN `User` u ON d.DriverID = u.UserID";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                drivers.add(new DeliveryPersonnel(
                   rs.getInt("DriverID"),
                   rs.getString("Username"),
                   rs.getString("Password"),
                   rs.getString("Email"),
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
     */

    public Object[][] getAllDeliveryPersonnel() {
        List<Object[]> driverData = new ArrayList<>();
        String sql = "SELECT d.DriverID, d.DriverName, d.Schedule, d.RouteID, d.AverageRating FROM DriverInfoView d";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                double avgRating = rs.getDouble("AverageRating");
                Object rating = rs.wasNull() ? "N/A" : avgRating;
                driverData.add(new Object[]{
                        rs.getInt("DriverID"),
                        rs.getString("DriverName"),
                        rs.getString("Schedule"),
                        rs.getInt("RouteID"),
                        rating
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get Drivers: " + e.getMessage());
        }
        return driverData.toArray(new Object[0][0]);
    }

    //Method to get userID from username
    private int getUserIDbyUsername(String username) {
        String sql = "SELECT UserID FROM `User` WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get UserID: " + e.getMessage());
        }
        return -1;
    }
}