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

        String sql = "INSERT INTO Driver (DriverID, DriverName, Schedule, RouteID , isAvailable) VALUES (?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setString(2, driver.getDriverName());
            stmt.setString(3, driver.getSchedule());
            stmt.setInt(4, driver.getRouteID());
            stmt.setBoolean(5, driver.isAvailable());

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
        String sql = "UPDATE Driver SET DriverName = ?, Schedule = ?, RouteID = ? , isAvailable= ? WHERE DriverID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getDriverName());
            stmt.setString(2, driver.getSchedule());
            stmt.setInt(3, driver.getRouteID());
            stmt.setBoolean(4, driver.isAvailable());
            stmt.setInt(5, driver.getDriverID());
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
    public boolean deleteUser(int userID) {
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
        String sql = "SELECT d.DriverID, d.DriverName, d.Schedule, d.RouteID, d.AverageRating ,d.isAvailable FROM DriverInfoView d";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                double avgRating = rs.getDouble("AverageRating");
                Object rating = rs.wasNull() ? "N/A" : avgRating;

                boolean available = rs.getBoolean("isAvailable");
                Object availability = rs.wasNull() ? "Unknown" : (available ? "Available" : "Unavailable");

                driverData.add(new Object[]{
                        rs.getInt("DriverID"),
                        rs.getString("DriverName"),
                        rs.getString("Schedule"),
                        rs.getInt("RouteID"),
                        rating,
                        availability
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get Drivers: " + e.getMessage());
        }
        return driverData.toArray(new Object[0][0]);
    }

    //Method to get userID from username
    public int getUserIDbyUsername(String Username) {
        String sql = "SELECT UserID FROM `User` WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Username.trim());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.out.println("Failed to Get UserID: " + e.getMessage());
        }
        return -1;
    }

    //fetching Driver-names from database
    public List<String> getALLDriverNames() {
        List<String> driverNames = new ArrayList<>();  // Create an empty list to store driver names

        String sql = "SELECT DriverName FROM Driver WHERE isAvailable = true";  // SQL query to select DriverName from Driver table

        try (
                // Establish connection, prepare statement, and execute the query
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);  // Prepare the SQL statement
                ResultSet resultSet = stmt.executeQuery();  // Execute the query and store results
        ) {
            // Iterate over the result set to fetch all driver names
            while (resultSet.next()) {
                // Get the driver's name from the result set
                String name = resultSet.getString("DriverName");  // "DriverName" is the column from the DB
                driverNames.add(name);  // Add the driver's name to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print any SQL exceptions if something goes wrong
        }

        return driverNames;  // Return the list of driver names
    }

}