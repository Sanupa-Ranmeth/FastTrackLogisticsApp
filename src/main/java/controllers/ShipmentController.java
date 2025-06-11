package controllers;

import models.*;

public class ShipmentController {
    private final ShipmentDAO shipmentDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final CityDAO cityDAO;

    public ShipmentController() {
        this.shipmentDAO = new ShipmentDAO();
        this.timeSlotDAO = new TimeSlotDAO();
        this.cityDAO = new CityDAO();
    }

    public int getUserIDbyUsername(String username) {
        return shipmentDAO.getUserIDbyUsername(username);
    }

    public boolean addShipment(Shipment shipment, String username) {
        int senderID = getUserIDbyUsername(username);
        if (senderID == -1) {
            return false;
        }
        return shipmentDAO.addShipment(shipment, senderID);
    }

    public boolean updateShipment(Shipment shipment) {
        return shipmentDAO.updateShipment(shipment);
    }

    public boolean deleteShipment(int shipmentID) {
        return shipmentDAO.deleteShipment(shipmentID);
    }

    public Object[][] getCustomerShipments(String username) {
        int senderID = getUserIDbyUsername(username);
        if (senderID == -1) {
            return new Object[0][0];
        }
        return shipmentDAO.getShipmentsByCustomer(senderID);
    }

    //Admin-only functions
    public Object[][] getAllAdminShipments() {
        return shipmentDAO.getAllAdminShipments();
    }

    public Object[][] getAdminShipmentsByStatus(String status) {
        return shipmentDAO.getAdminShipmentsByStatus(status);
    }

    //Methods to update shipment status - approve and disapprove buttons
    public boolean approveShipment(int shipmentID) {
        return shipmentDAO.updateShipmentStatus(shipmentID, "Approved");
    }

    public boolean disapproveShipment(int shipmentID) {
        return shipmentDAO.updateShipmentStatus(shipmentID, "Disapproved");
    }

    // get shipment status/
    public Object[][] getShipmentTracking(int shipmentID) {
        return ShipmentDAO.getShipmentTracking(shipmentID);
    }

    public boolean hasShipmentBeenRated(int shipmentID) {
        return shipmentDAO.hasRating(shipmentID);
    }

    public boolean rateShipment(int shipmentID, int rating) {
        // Validate rating is within acceptable range
        if (rating < 1 || rating > 5) {
            return false;
        }

        return shipmentDAO.rateDelivery(shipmentID, rating);
    }

}
