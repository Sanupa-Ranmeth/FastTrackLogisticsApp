package controllers;

import models.DeliveryPersonnel;
import models.DeliveryPersonnelDAO;

import utilities.DatabaseConnection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


public class DeliveryPersonnelController {
    private DeliveryPersonnelDAO driverDAO;

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

    public boolean updateDrivers(int driverID, String username, String password, String email, String driverName,  String schedule, int routeID,  boolean isAvailable) {
        if (driverID <= 0 || username == null || password == null || email == null || driverName == null || schedule == null ||
        username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || driverName.trim().isEmpty() || schedule.trim().isEmpty() || routeID <= 0) {
            return false;
        }
        DeliveryPersonnel driver = new DeliveryPersonnel(driverID, username, password, email, driverName, schedule, routeID ,   isAvailable);
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

    public void updateAvailability(String DriverName, int value) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Driver SET isAvailable = ? WHERE DriverName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, value);
            stmt.setString(2, DriverName.trim()); // trim spaces to avoid mismatch
            int rows = stmt.executeUpdate();
            System.out.println("updateAvailability called: driverName='" + DriverName + "', value=" + value + ", rows affected=" + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean getAvailability(String DriverName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT isAvailable FROM Driver WHERE DriverName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, DriverName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("isAvailable") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}