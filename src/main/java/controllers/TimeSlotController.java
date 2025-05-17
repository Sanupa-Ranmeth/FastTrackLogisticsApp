package controllers;

import models.TimeSlot;
import models.TimeSlotDAO;

import java.sql.Time;
import java.util.List;

public class TimeSlotController {
    private TimeSlotDAO timeSlotDAO;

    public TimeSlotController() {
        this.timeSlotDAO = new TimeSlotDAO();
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotDAO.getAllTimeSlots();
    }

    public boolean addTimeSlot(Time startTime, Time endTime) {
        if (startTime.after(endTime)) {
            System.out.println("Start time must be before the end time.");
            return false;
        }
        return timeSlotDAO.addTimeSlot(startTime, endTime);
    }

    public boolean removeTimeSlot(int timeSlotID) {
        return timeSlotDAO.removeTimeSlot(timeSlotID);
    }
}
