package models;

public class Shipment {
    private int shipmentID;
    private int senderID;
    private String receiverName;
    private String destination;
    private String content;
    private boolean isUrgent;
    private String preferredTimeSlot;

    //Full Constructor
    public Shipment(int shipmentID, int senderID, String receiverName, String destination, String content, boolean isUrgent, String preferredTimeSlot) {
        this.shipmentID = shipmentID;
        this.senderID = senderID;
        this.receiverName = receiverName;
        this.destination = destination;
        this.content = content;
        this.isUrgent = isUrgent;
        this.preferredTimeSlot = preferredTimeSlot;
    }

    //Partial Constructor for Adding Shipments (ShipmentID is auto-incremented)
    public Shipment(String receiverName, String destination, String content, boolean isUrgent, String preferredTimeSlot) {
        this.receiverName = receiverName;
        this.destination = destination;
        this.content = content;
        this.isUrgent = isUrgent;
        this.preferredTimeSlot = preferredTimeSlot;
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

    public String getDestination() {
        return destination;
    }

    public String getContent() {
        return content;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public String getPreferredTimeSlot() {
        return preferredTimeSlot;
    }
}