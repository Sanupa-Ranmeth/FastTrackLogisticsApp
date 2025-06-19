package controllers;

import models.CityDAO;
import models.Shipment;
import models.ShipmentDAO;
import models.TimeSlotDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ShipmentControllerTest {
    private ShipmentDAO shipmentDAO;
    private TimeSlotDAO timeSlotDAO;
    private CityDAO cityDAO;
    private ShipmentController shipmentController;

    @BeforeEach
    public void setUp() {
        shipmentDAO = mock(ShipmentDAO.class);
        timeSlotDAO = mock(TimeSlotDAO.class);
        cityDAO = mock(CityDAO.class);
        shipmentController = new ShipmentController(shipmentDAO, timeSlotDAO, cityDAO);
    }

    @Test
    public void testAddShipment_ValidUser() {
        Shipment shipment = new Shipment("Receiver", 1, "Address", "Books", false, new Date(), null, "pending");
        when(shipmentDAO.getUserIDbyUsername("sanupa")).thenReturn(10);
        when(shipmentDAO.addShipment(shipment, 10)).thenReturn(true);

        boolean result = shipmentController.addShipment(shipment, "sanupa");

        assertTrue(result);
        verify(shipmentDAO).addShipment(shipment, 10);
    }

    @Test
    public void testAddShipment_InvalidUser() {
        Shipment shipment = new Shipment("Receiver", 1, "Address", "Books", false, new Date(), null, "pending");

        when(shipmentDAO.getUserIDbyUsername("sithuja")).thenReturn(-1);

        boolean result = shipmentController.addShipment(shipment, "sithuja");

        assertFalse(result);
        verify(shipmentDAO, never()).addShipment(any(), anyInt());
    }

    @Test
    public void testRateShipment_InvalidRating() {
        boolean result = shipmentController.rateShipment(1, 0);
        assertFalse(result);
    }

    @Test
    public void testRateShipment_ValidRating() {
        when(shipmentDAO.rateDelivery(1, 5)).thenReturn(true);

        boolean result = shipmentController.rateShipment(1, 5);

        assertTrue(result);
        verify(shipmentDAO).rateDelivery(1, 5);
    }

    @Test
    public void testApproveShipment() {
        when(shipmentDAO.updateShipmentStatus(5, "Approved")).thenReturn(true);

        boolean result = shipmentController.approveShipment(5);

        assertTrue(result);
        verify(shipmentDAO).updateShipmentStatus(5, "Approved");
    }

    @Test
    public void testDisapproveShipment() {
        when(shipmentDAO.updateShipmentStatus(5, "Disapproved")).thenReturn(true);
        boolean result = shipmentController.disapproveShipment(5);
        assertTrue(result);
        verify(shipmentDAO).updateShipmentStatus(5, "Disapproved");
    }

    @AfterEach
    public void tearDown() {
        shipmentDAO = null;
        timeSlotDAO = null;
        cityDAO = null;
        shipmentController = null;
    }
}
