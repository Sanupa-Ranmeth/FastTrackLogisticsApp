package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    //Fetch All Cities from the Database
    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT CityID, CityName FROM City";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cities.add(new City(
                    rs.getInt("CityID"),
                    rs.getString("CityName")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch cities: " + e.getMessage());
        }
        return cities;
    }

    //Get CityID by CityName
    public String getCityNameByCityID(int CityID) {
        String sql = "SELECT CityName FROM City WHERE CityID = ?";
        String cityName = "";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, CityID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cityName = rs.getString("CityName");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to Fetch City Name from ID: " + e.getMessage());
        }
        return cityName;
    }

    //Get CityName by CityID
    public int getCityIDByCityName(String CityName) {
        String sql = "SELECT CityID FROM City WHERE CityName = ?";
        int cityID = 0;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, CityName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cityID = rs.getInt("CityID");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to Fetch City ID from City Name: " + e.getMessage());
        }
        return cityID;
    }
}
