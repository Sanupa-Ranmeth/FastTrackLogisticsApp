package controllers;

import models.*;

import java.util.List;

public class ShipmentController {
    private ShipmentDAO shipmentDAO;
    private TimeSlotDAO timeSlotDAO;
    private CityDAO cityDAO;

    public ShipmentController() {
        this.shipmentDAO = new ShipmentDAO();
        this.timeSlotDAO = new TimeSlotDAO();
        this.cityDAO = new CityDAO();
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

    //Fetching time slots and cities for the dropdowns
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotDAO.getAllTimeSlots();
    }

    public List<City> getAllCities() {
        return cityDAO.getAllCities();
    }
}
