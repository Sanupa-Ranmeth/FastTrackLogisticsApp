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

    // Get All Cities in a Route
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
                            rs.getString("CityName")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Getting Cities by RouteID Failed: " + e.getMessage());
        }

        return cities;
    }

    // Find out what route a city belongs to
    public int getRouteFromCityName(String cityName) {
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
        } catch (SQLException e) {
            System.out.println("Getting Route From CityName Failed: " + e.getMessage());
        }

        return routeID;
    }

    // Get all routes with start/end city names
    public List<Route> getAllRoutesWithCities() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT r.RouteID, r.StartCityID, r.EndCityID, c1.CityName as StartCityName, c2.CityName as EndCityName "
                +
                "FROM Route r " +
                "JOIN City c1 ON r.StartCityID = c1.CityID " +
                "JOIN City c2 ON r.EndCityID = c2.CityID";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Route route = new Route(
                        rs.getInt("RouteID"),
                        rs.getInt("StartCityID"),
                        rs.getInt("EndCityID"));
                route.setStartCityName(rs.getString("StartCityName"));
                route.setEndCityName(rs.getString("EndCityName"));
                routes.add(route);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all routes: " + e.getMessage());
        }
        return routes;
    }

    // Remove all cities from a route
    public boolean removeAllCitiesFromRoute(int routeID) {
        String sql = "DELETE FROM Route_City WHERE RouteID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to remove cities from route: " + e.getMessage());
            return false;
        }
    }

    // Update a route's start/end cities
    public boolean updateRoute(int routeID, int startCityID, int endCityID) {
        String sql = "UPDATE Route SET StartCityID = ?, EndCityID = ? WHERE RouteID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, startCityID);
            stmt.setInt(2, endCityID);
            stmt.setInt(3, routeID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update route: " + e.getMessage());
            return false;
        }
    }

    // Remove a specific city from a route
    public boolean removeCityFromRoute(int routeID, int cityID) {
        String sql = "DELETE FROM Route_City WHERE RouteID = ? AND CityID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);
            stmt.setInt(2, cityID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to remove city from route: " + e.getMessage());
            return false;
        }
    }

    public Object[][] getAllRoutes() {
        List<Object[]> routes = new ArrayList<>();
        String sql = "SELECT r.RouteID, c1.CityName as StartCity, c2.CityName as EndCity " +
                "FROM Route r " +
                "JOIN City c1 ON r.StartCityID = c1.CityID " +
                "JOIN City c2 ON r.EndCityID = c2.CityID";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                routes.add(new Object[] {
                        rs.getInt("RouteID"),
                        rs.getString("StartCity"),
                        rs.getString("EndCity")
                });
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all routes: " + e.getMessage());
        }

        return routes.toArray(new Object[0][0]);
    }

    public int getCitySequence(int routeID, int cityID) {
        String sql = "SELECT SequenceNumber FROM Route_City WHERE RouteID = ? AND CityID = ?";
        int sequence = 0;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeID);
            stmt.setInt(2, cityID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sequence = rs.getInt("SequenceNumber");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get city sequence: " + e.getMessage());
        }

        return sequence;
    }
}
