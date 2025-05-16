package controllers;

import models.Delivery;
import models.DeliveryDAO;
import models.Shipment;

import java.time.LocalDateTime;
import java.util.List;

public class DeliveryController {
    private DeliveryDAO deliveryDAO = new DeliveryDAO();
    private ShipmentController shipmentController = new ShipmentController();

    public boolean approveDelivery(int shipmentID, int driverID) {
        return deliveryDAO.approveShipment(shipmentID, driverID) && shipmentController.approveShipment(shipmentID);
    }

    public boolean disapproveDelivery(int shipmentID) {
        return deliveryDAO.disapproveShipment(shipmentID) && shipmentController.disapproveShipment(shipmentID);
    }

    public boolean addDelivery(int SenderID, Shipment shipment, Delivery delivery) {
        return deliveryDAO.createDelivery(SenderID, shipment, delivery);
    }

    public boolean updateDelivery(Shipment shipment, Delivery delivery) {
        return deliveryDAO.updateDelivery(shipment, delivery);
    }

    public boolean deleteDelivery(int shipmentID) {
        return deliveryDAO.deleteDelivery(shipmentID);
    }

    public Delivery getDeliveryByShipmentID(int shipmentID) {
        return deliveryDAO.getDeliverybyShipmentID(shipmentID);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryDAO.getAllDeliveries();
    }

    public boolean setDeliveryEstimation(int shipmentID, LocalDateTime estimatedDateTime) {
        return deliveryDAO.setDeliveryEstimation(shipmentID, estimatedDateTime);
    }

    public boolean updateDeliveryOperations(int shipmentID, String status, Integer location, int delay) {
        return deliveryDAO.updateDeliveryOperations(shipmentID, status, location, delay);
    }
}
