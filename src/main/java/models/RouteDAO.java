package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {
    public boolean addRoute(Route route) {
        String sql = "INSERT INTO Route (StartCityID, EndCityID) VALUES (?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, route.getStartCityID());
            stmt.setInt(2, route.getEndCityID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add Route Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoute(int routeID) {
        String sql = "DELETE FROM Route WHERE RouteID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete Route Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addCitytoRoute(int routeID, int cityID, int sequence) {
        String sql = "INSERT INTO Route_City (RouteID, CityID, SequenceNumber) VALUES (?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);
            stmt.setInt(2, cityID);
            stmt.setInt(3, sequence);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to Add City to Route: " + e.getMessage());
            return false;
        }
    }

    //Get All Cities in a Route
    public List<City> getCitiesbyRouteID(int routeID) {
        List<City> cities = new ArrayList<>();

        String sql = "SELECT c.CityID, c.CityName " +
                     "FROM Route_City rc " +
                     "JOIN City c ON rc.CityID = c.CityID " +
                     "WHERE rc.RouteID = ? " +
                     "ORDER BY rc.SequenceNumber ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cities.add(new City(
                       rs.getInt("CityID"),
                       rs.getString("CityName")
                    ));
                }
            }
        }  catch (SQLException e) {
            System.out.println("Getting Cities by RouteID Failed: " + e.getMessage());
        }

        return cities;
    }

    //Find out what route a city belongs to
    public int getRouteFromCityName (String cityName) {
        String sql = "SELECT rc.RouteID " +
                     "FROM Route_City rc " +
                     "JOIN City c ON rc.CityID = c.CityID " +
                     "WHERE CityName = ?";
        int routeID = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cityName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    routeID = rs.getInt("RouteID");
                }
            }
        }  catch (SQLException e) {
            System.out.println("Getting Route From CityName Failed: " + e.getMessage());
        }

        return routeID;
    }
}
