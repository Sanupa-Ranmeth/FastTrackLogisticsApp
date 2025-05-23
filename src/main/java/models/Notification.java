package models;

import java.time.LocalDateTime;

public class Notification {
    private int notificationID;
    private int userID;
    private String message;
    private LocalDateTime timeStamp;
    private boolean isRead;

    // Full constructor
    public Notification(int notificationID, int userID, String message, LocalDateTime timeStamp, boolean isRead) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.message = message;
        this.timeStamp = timeStamp;
        this.isRead = isRead;
    }

    // Partial constructor for creating new notifications
    public Notification(int userID, String message) {
        this.userID = userID;
        this.message = message;
        this.timeStamp = LocalDateTime.now(); // Set timestamp to current time
        this.isRead = false; // Default to unread
    }

    // Getters and Setters
    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getTimestamp() {
        return timeStamp;
    }
}
