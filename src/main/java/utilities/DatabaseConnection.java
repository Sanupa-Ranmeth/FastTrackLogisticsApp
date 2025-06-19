package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    /*
    Remote Connection URL

    private static final String USER = "root";
    private static final String PASSWORD = "#28@Vihanga#Jay.";
    private static final String URL = "jdbc:mysql://sql.vihaga.dev:3306/FastTrackLogisticsDB?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
     */

    //Localhost URL
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/fasttracklogisticsdb";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure driver is loaded
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }
}