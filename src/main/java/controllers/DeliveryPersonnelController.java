package controllers;

import models.DeliveryPersonnel;
import models.DeliveryPersonnelDAO;

import utilities.DatabaseConnection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;


public class DeliveryPersonnelController {
    private final DeliveryPersonnelDAO driverDAO;

    public DeliveryPersonnelController() {
        this.driverDAO = new DeliveryPersonnelDAO();
    }

    public boolean addDriver(String username, String password, String email, String driverName, String schedule, int routeID, boolean isAvailable) {
        //Validate parameters
        if (username == null || password == null || email == null || driverName == null || schedule == null ||
        username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || driverName.trim().isEmpty() || schedule.trim().isEmpty() || routeID <= 0) {
            return false;
        }

        DeliveryPersonnel driver = new DeliveryPersonnel(username, password, email, driverName, schedule, routeID,  isAvailable);
        return driverDAO.addDeliveryPersonnel(driver);
    }

    public boolean updateDrivers(int driverID, String driverName,  String schedule, int routeID,  boolean isAvailable) {
        if (driverID <= 0 || driverName == null || schedule == null || driverName.trim().isEmpty() || schedule.trim().isEmpty() || routeID <= 0) {
            return false;
        }

        DeliveryPersonnel existingDriver;

        try {
            existingDriver = driverDAO.getDriverByID(driverID);
            if (existingDriver == null) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Couldn't fetch driver's username, email and password from the database to update him.");
            return false;
        }

        DeliveryPersonnel driver = new DeliveryPersonnel(driverID, existingDriver.getUsername(), existingDriver.getPassword(), existingDriver.getEmail(), driverName, schedule, routeID ,   isAvailable);
        return driverDAO.updateDeliveryPersonnel(driver);
    }

    public boolean deleteDriver(int driverID) {
        if (driverID <= 0) {
            return false;
        }
        return driverDAO.deleteDeliveryPersonnel(driverID) && driverDAO.deleteUser(driverID);
    }

    public Object[][] getAllDrivers() {
        return driverDAO.getAllDeliveryPersonnel();
    }

    public List<String> getAllDriverNames() {
        return driverDAO.getALLDriverNames();
    }

    public boolean isDriverAvailable(String DriverName) {
        boolean isAvailable = false;
        String query = "SELECT IsAvailable FROM Driver WHERE DriverName = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, DriverName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isAvailable = rs.getBoolean("IsAvailable");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAvailable;



    }

    public static void updateAvailability(int DriverID, int value) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Driver SET isAvailable = ? WHERE DriverID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, value);
            stmt.setInt(2, DriverID); // trim spaces to avoid mismatch
            int rows = stmt.executeUpdate();
            System.out.println("updateAvailability called: driverName='" + DriverID + "', value=" + value + ", rows affected=" + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean getAvailability(int DriverID) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT isAvailable FROM Driver WHERE DriverID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, DriverID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("isAvailable") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




    public int getUserIDbyUsername(String username) {
        return driverDAO.getUserIDbyUsername(username);
    }



}