package controllers;

import models.DeliveryPersonnel;
import models.DeliveryPersonnelDAO;

public class DeliveryPersonnelController {
    private DeliveryPersonnelDAO driverDAO;

    public DeliveryPersonnelController() {
        this.driverDAO = new DeliveryPersonnelDAO();
    }

    public boolean addDriver(String username, String password, String email, String driverName, String schedule, int routeID) {
        //Validate parameters
        if (username == null || password == null | email == null || driverName == null || schedule == null ||
        username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || driverName.trim().isEmpty() || schedule.trim().isEmpty() || routeID <= 0) {
            return false;
        }

        DeliveryPersonnel driver = new DeliveryPersonnel(username, password, email, driverName, schedule, routeID);
        return driverDAO.addDeliveryPersonnel(driver);
    }

    public boolean updateDrivers(int driverID, String username, String password, String email, String driverName,  String schedule, int routeID){
        if (driverID <= 0 || username == null || password == null || email == null || driverName == null || schedule == null ||
        username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty() || driverName.trim().isEmpty() || schedule.trim().isEmpty() || routeID <= 0) {
            return false;
        }
        DeliveryPersonnel driver = new DeliveryPersonnel(driverID, username, password, email, driverName, schedule, routeID);
        return driverDAO.updateDeliveryPersonnel(driver);
    }

    public boolean deleteDriver(int driverID) {
        if (driverID <= 0) {
            return false;
        }
        return driverDAO.deleteDeliveryPersonnel(driverID) && driverDAO.deleteUser(driverID);
    }

    public Object[][] getAllDrivers() {
        return driverDAO.getAllDeliveryPersonnel();
    }

}