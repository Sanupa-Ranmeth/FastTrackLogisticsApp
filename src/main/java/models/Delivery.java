package models;

import java.time.LocalDateTime;

//Test comment

public class Delivery {
    private int shipmentID;
    private int driverID;
    private Integer location;
    private Integer rating;
    private boolean isDelayed;
    private Integer delay;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDateTime;

    //Full constructor
    public Delivery(int shipmentID, int driverID, Integer location, Integer rating, boolean isDelayed, Integer delay, LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDateTime) {
        this.shipmentID = shipmentID;
        this.driverID = driverID;
        this.location = location;
        this.rating = rating;
        this.isDelayed = isDelayed;
        this.delay = delay;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDateTime = actualDeliveryDateTime;
    }

    //Constructor for updating shipments - admin shouldn't be able to update customer rating for the driver and actual delivery time
    public Delivery(int shipmentID, int driverID, Integer location, boolean isDelayed, Integer delay, LocalDateTime estimatedDeliveryDate) {
        this.shipmentID = shipmentID;
        this.driverID = driverID;
        this.location = location;
        this.isDelayed = isDelayed;
        this.delay = delay;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    //Constructor for adding shipments (shipmentID will be obtained from Database)
    public Delivery(int driverID) {
        this.driverID = driverID;
    }

    //Getters and setters
    public int getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(int shipmentID) {
        this.shipmentID = shipmentID;
    }

    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDateTime() {
        return actualDeliveryDateTime;
    }

    public void setActualDeliveryDateTime(LocalDateTime actualDeliveryDateTime) {
        this.actualDeliveryDateTime = actualDeliveryDateTime;
    }
}
