package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentTest {

    private Shipment shipment;
    private Date deliveryDate;

    @BeforeEach
    public void setUp() {
        deliveryDate = new Date();
        shipment = new Shipment("Sanupa", 3, "Kobeithuduwa, Batapola", "RTX 4070", false, deliveryDate, null, "pending");
    }

    @Test
    public void testReceiverName() {
        assertEquals("Sanupa", shipment.getReceiverName());
    }

    @Test
    public void testDestination() {
        assertEquals(3, shipment.getDestination());
    }

    @Test
    public void testContent() {
        assertEquals("RTX 4070", shipment.getContent());
    }

    @Test
    public void testIsUrgent() {
        assertFalse(shipment.isUrgent());
    }

    @Test
    public void testDeliveryDate() {
        assertEquals(deliveryDate, shipment.getDeliveryDate());
    }

    @Test
    public void testPreferredTimeSlotIsNull() {
        assertNull(shipment.getPreferredTimeSlot());
    }

    @Test
    public void testStatus() {
        assertEquals("pending", shipment.getStatus());
    }

    @Test
    public void testStatusSetter() {
        shipment.setStatus("In Transit");
        assertEquals("In Transit", shipment.getStatus());
    }

    @Test
    public void testSetPreferredTimeSlot() {
        shipment.setPreferredTimeSlot(4);
        assertEquals(4, shipment.getPreferredTimeSlot());
    }

    @Test
    public void testSetDestination() {
        shipment.setDestination(5);
        assertEquals(5, shipment.getDestination());
    }

    @Test
    public void testDeliveryDateSetter() {
        Date newDate = new Date(System.currentTimeMillis() + 86400000);
        shipment.setDeliveryDate(newDate);
        assertEquals(newDate, shipment.getDeliveryDate());
    }

    @AfterEach
    public void tearDown() {
        shipment = null;
    }

}