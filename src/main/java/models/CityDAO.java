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
}
