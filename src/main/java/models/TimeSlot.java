package models;

import java.sql.Time;

public class TimeSlot {
    private final int timeSlotID;
    private final Time startTime;
    private final Time endTime;

    //Constructor
    public TimeSlot(int timeSlotID, Time startTime, Time endTime) {
        this.timeSlotID = timeSlotID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //Getters
    public int getTimeSlotID() {
        return timeSlotID;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    //TimeSlot display
    @Override
    public String toString() {
        return this.startTime + " - " + this.endTime;
    }
}
