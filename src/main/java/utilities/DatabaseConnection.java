package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://sql.vihaga.dev:1433;databaseName=FastTrackLogisticsDB;user=SA;password=#28@Vihanga#Jay.;encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() {
        try {
            System.out.println("Connection established");
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }
}
