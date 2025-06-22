package models;

import utilities.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Test comment

public class TimeSlotDAO {
    //Getting all time slots
    public List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
        String sql = "SELECT * FROM TimeSlot";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                timeSlots.add(new TimeSlot(
                        rs.getInt("TimeslotID"),
                        rs.getTime("StartTime"),
                        rs.getTime("EndTime")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch timeslots: " + e.getMessage());
        }
        return timeSlots;
    }

    //Adding timeslots
    public boolean addTimeSlot(Time startTime, Time endTime) {
        String sql = "INSERT INTO TimeSlot (StartTime, EndTime) VALUES (?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, startTime);
            stmt.setTime(2, endTime);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to add time slot: " + e.getMessage());
            return false;
        }
    }

    //Removing timeslots
    public boolean removeTimeSlot(int timeSlotID) {
        String sql = "DELETE FROM TimeSlot WHERE TimeslotID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timeSlotID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to remove time slot: " + e.getMessage());
            return false;
        }
    }
}
