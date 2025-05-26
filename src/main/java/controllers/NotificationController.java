package controllers;

import models.Notification;
import models.NotificationDAO;

import java.util.List;

public class NotificationController {
    private NotificationDAO notificationDAO = new NotificationDAO();

    public boolean generateUpdateDeliveryNotification(int packageID, int userID, String status, String location, int delay) {
        String message = "Your package " + packageID + " is " + status +
                ". It is currently in " + location +
                " and en route to you with " + delay + " delay.";
        String type = "Delivery Update";

        Notification notification = new Notification(userID, type, message);
        notificationDAO.saveNotification(notification, userID);
        return true;
    }

    public boolean generateApproveDeliveryNotification(int packageID, int userID) {
        String message = "Your shipment with Package ID " + packageID + " has been approved and is ready for delivery.";
        String type = "Approval";
        Notification notification = new Notification(userID, type, message);
        notificationDAO.saveNotification(notification, userID);
        return true;
    }

    public boolean generateDisapproveDeliveryNotification(int packageID, int userID) {
        String message = "Your shipment with Package ID " + packageID + " has been disapproved.";
        String type = "Disapproval";
        Notification notification = new Notification(userID, type, message);
        notificationDAO.saveNotification(notification, userID);
        return true;
    }

    public boolean generateDeliveryEstimationNotification(int packageID, int userID, String estimatedDeliveryDate) {
        String message = "Your shipment with Package ID " + packageID + " is estimated to reach you by " + estimatedDeliveryDate + ".";
        String type = "Delivery Estimation";
        Notification notification = new Notification(userID, type, message);
        notificationDAO.saveNotification(notification, userID);
        return true;
    }

    public List<Notification> getUserNotifications(int userID) {
        return notificationDAO.getAllNotifications(userID);
    }
    public boolean markNotificationAsRead(int notificationID) {
        return notificationDAO.markAsRead(notificationID);
    }
    public boolean deleteNotification(int notificationID){
        return notificationDAO.deleteNofitication(notificationID);
    }
}
