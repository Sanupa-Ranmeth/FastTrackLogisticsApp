package controllers;

import models.Shipment;
import models.ShipmentDAO;

public class ShipmentController {
    private ShipmentDAO shipmentDAO;

    public ShipmentController() {
        this.shipmentDAO = new ShipmentDAO();
    }

    public boolean addShipment(Shipment shipment, String username) {
        int senderID = shipmentDAO.getUserIDbyUsername(username);
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
        int senderID = shipmentDAO.getUserIDbyUsername(username);
        if (senderID == -1) {
            return new Object[0][0];
        }
        return shipmentDAO.getShipmentsByCustomer(senderID);
    }
}
