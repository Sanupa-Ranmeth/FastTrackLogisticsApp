package models;

import java.util.Date;

public class Shipment {
    private int shipmentID;
    private int senderID;
    private final String receiverName;
    private Integer destination;
    private final String destinationAddress;
    private final String content;
    private final boolean isUrgent;
    private Date deliveryDate;
    private Integer preferredTimeSlot; //Integer - can be null unlike int
    private String status;

    //Full Constructor
    public Shipment(int shipmentID, int senderID, String receiverName, Integer destination, String destinationAddress, String content, boolean isUrgent, Date deliveryDate, Integer preferredTimeSlot, String status) {
        this.shipmentID = shipmentID;
        this.senderID = senderID;
        this.receiverName = receiverName;
        this.destination = destination;
        this.destinationAddress = destinationAddress;
        this.content = content;
        this.isUrgent = isUrgent;
        this.deliveryDate = deliveryDate;
        this.preferredTimeSlot = preferredTimeSlot;
        this.status = status;
    }

    //Partial Constructor for Adding Shipments (ShipmentID is auto-incremented)
    public Shipment(String receiverName, Integer destination, String destinationAddress, String content, boolean isUrgent, Date deliveryDate, Integer preferredTimeSlot, String status) {
        this.receiverName = receiverName;
        this.destination = destination;
        this.destinationAddress = destinationAddress;
        this.content = content;
        this.isUrgent = isUrgent;
        this.deliveryDate = deliveryDate;
        this.preferredTimeSlot = preferredTimeSlot;
        this.status = status;
    }

    //Getters
    public int getShipmentID() {
        return shipmentID;
    }

    public int getSenderID() {
        return senderID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public Integer getDestination() {
        return destination;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getContent() {
        return content;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Integer getPreferredTimeSlot() {
        return preferredTimeSlot;
    }

    public String getStatus() {
        return status;
    }

    //Setters (if needed for updates)
    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setPreferredTimeSlot(Integer preferredTimeSlot) {
        this.preferredTimeSlot = preferredTimeSlot;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}