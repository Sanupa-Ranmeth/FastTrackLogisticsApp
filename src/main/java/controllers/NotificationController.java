package controllers;

import models.Notification;
import models.NotificationDAO;

import java.util.List;

public class NotificationController {
    private final NotificationDAO notificationDAO;
    public NotificationController() {
        this.notificationDAO = new NotificationDAO();
    }
    public boolean generateUpdateDeliveryNotification(int packageID, int userID, String status, String location, String delay) {
        String message = "Your package " + packageID + " is " + status +
                ". It is currently in " + location +
                " and route to you with " + delay + " delay.";

        Notification notification = new Notification(userID, message);
        notificationDAO.saveNotification(notification);
        return true;
    }
    public boolean generateApproveDeliveryNotitfication(int packageID, int userID, String status, String location, String delay ) {
        String message = "Your shipment with Package ID " + packageID + " has been approved and is ready for delivery.";
        Notification notification = new Notification(userID, message);
        notificationDAO.saveNotification(notification);
        return true;
    }
    public boolean generateDisapproveDeliveryNotification(int packageID, int userID) {
        String message = "Your shipment with Package ID " + packageID + " has been disapproved.";
        Notification notification = new Notification(userID, message);
        notificationDAO.saveNotification(notification);
        return true;
    }
    public boolean generateSetDeliveryEstimationNotification(int packageID, int userID, String estimatedDeliveryDate) {
        String message = "Your shipment with Package ID " + packageID + " has an estimated delivery date of " + estimatedDeliveryDate + ".";
        Notification notification = new Notification(userID, message);
        notificationDAO.saveNotification(notification);
        return true;
    }
    public List<Notification> getUserNotifications(int userID) {
        return notificationDAO.getAllNotifications(userID);
    }
}
