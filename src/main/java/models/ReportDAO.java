package models;

import utilities.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {
    private final Connection conn;

    public ReportDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public int getTotalShipments(int year, int month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Shipment WHERE YEAR(created_at) = ? AND MONTH(created_at) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int getCancelledShipments(int year, int month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Shipment WHERE Status = 'Cancelled' AND YEAR(created_at) = ? AND MONTH(created_at) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double getOnTimeDeliveryRate(int year, int month) throws SQLException {
        String totalSQL = "SELECT COUNT(*) " +
                "FROM Delivery d " +
                "JOIN Shipment s " +
                "ON d.ShipmentID = s.ShipmentID " +
                "WHERE YEAR(s.created_at) = ? AND MONTH(s.created_at) = ? ";
        String onTimeSQL = "SELECT COUNT(*) " +
                "FROM Delivery d " +
                "JOIN Shipment s " +
                "ON d.ShipmentID = s.ShipmentID " +
                "WHERE d.isDelayed = 0 AND YEAR(s.created_at) = ? AND MONTH(s.created_at) = ?";

        try (PreparedStatement totalStmt = conn.prepareStatement(totalSQL);
        PreparedStatement onTimeStmt = conn.prepareStatement(onTimeSQL)) {
            totalStmt.setInt(1, year);
            totalStmt.setInt(2, month);
            onTimeStmt.setInt(1, year);
            onTimeStmt.setInt(2, month);

            ResultSet totalRS = totalStmt.executeQuery();
            ResultSet onTimeRS = onTimeStmt.executeQuery();

            int total = totalRS.next() ? totalRS.getInt(1) : 0;
            int onTime = onTimeRS.next() ? onTimeRS.getInt(1) : 0;
            return total == 0 ? 0 : (onTime * 100.0 / total);
        }
    }

    public double getAverageDelay(int year, int month) throws SQLException {
        String sql = "SELECT AVG(d.Delay) FROM Delivery d " +
                "JOIN Shipment s ON d.ShipmentID = s.ShipmentID " +
                "WHERE d.isDelayed = 1 AND YEAR(s.created_at) = ? AND MONTH(s.created_at) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    public int getDelay(String mode, int year, int month) throws SQLException {
        String sql = mode.equals("max") ? "SELECT MAX(d.Delay) FROM Delivery d JOIN Shipment s ON d.ShipmentID = s.ShipmentID WHERE d.isDelayed = 1 AND YEAR(s.created_at) = ? AND MONTH(s.created_at) = ?" :
                "SELECT MIN(d.Delay) FROM Delivery d JOIN Shipment s ON d.ShipmentID = s.ShipmentID WHERE d.isDelayed = 1 AND YEAR(s.created_at) = ? AND MONTH(s.created_at) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double getSuccessRate(int year, int month) throws SQLException {
        String sqlDelivered = "SELECT COUNT(*) FROM Shipment WHERE Status = 'Delivered' AND YEAR(created_at) = ? AND MONTH(created_at) = ?";
        String sqlTotal = "SELECT COUNT(*) FROM Shipment WHERE Status IN ('Delivered', 'Failed Delivery') AND YEAR(created_at) = ? AND MONTH(created_at) = ?";

        try (PreparedStatement stmtDelivered = conn.prepareStatement(sqlDelivered);
        PreparedStatement stmtTotal = conn.prepareStatement(sqlTotal)) {
            stmtDelivered.setInt(1, year);
            stmtDelivered.setInt(2, month);
            stmtTotal.setInt(1, year);
            stmtTotal.setInt(2, month);

            ResultSet dRS = stmtDelivered.executeQuery(), rRS = stmtTotal.executeQuery();
            int d = dRS.next() ? dRS.getInt(1) : 0;
            int t = dRS.next() ? dRS.getInt(1) : 0;
            return t == 0 ? 0 : (d * 100.0 / t);
        }
    }

    public double getFailureRate(int year, int month) throws SQLException {
        String sqlFailed = "SELECT COUNT(*) FROM Shipment WHERE Status = 'Failed Delivery' AND YEAR(created_at) = ? AND MONTH(created_at) = ?";
        String sqlTotal = "SELECT COUNT(*) FROM Shipment WHERE Status IN ('Delivered', 'Failed Delivery') AND YEAR(created_at) = ? AND MONTH(created_at) = ?";

        try (PreparedStatement stmtFailed = conn.prepareStatement(sqlFailed);
             PreparedStatement stmtTotal = conn.prepareStatement(sqlTotal)) {
            stmtFailed.setInt(1, year);
            stmtFailed.setInt(2, month);
            stmtTotal.setInt(1, year);
            stmtTotal.setInt(2, month);

            ResultSet fRS = stmtFailed.executeQuery(), rRS = stmtTotal.executeQuery();
            int f = fRS.next() ? fRS.getInt(1) : 0;
            int t = fRS.next() ? fRS.getInt(1) : 0;
            return t == 0 ? 0 : (f * 100.0 / t);
        }
    }

    public double getAverageRating (int year, int month) throws SQLException {
        String sql = "SELECT AVG(d.Rating) FROM Delivery d JOIN Shipment s ON d.ShipmentID = s.ShipmentID WHERE d.Rating IS NOT NULL AND YEAR(created_at) = ? AND MONTH(created_at) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    public int getRatingCount(int stars, int year, int month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Delivery d JOIN Shipment s ON d.ShipmentID = s.ShipmentID WHERE d.Rating = ? AND YEAR(created_at) = ? AND MONTH(created_at) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stars);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public String getTopRatedDriver(int year, int month) throws SQLException {
        String sql = """
                SELECT u.Username 
                FROM Delivery d 
                JOIN Shipment s ON d.ShipmentID = s.ShipmentID 
                JOIN Driver dr ON d.DriverID = dr.DriverID 
                JOIN User u ON dr.driverID = u.UserID 
                WHERE d.Rating IS NOT NULL AND YEAR(created_at) = ? AND MONTH(created_at) = ? 
                GROUP BY u.Username 
                ORDER BY AVG(d.Rating) DESC 
                LIMIT 1
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("Username") : "No data";
            }
        }
    }
}