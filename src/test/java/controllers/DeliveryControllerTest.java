package controllers;

import models.Delivery;
import models.DeliveryDAO;
import models.Shipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeliveryControllerTest {
    @Mock
    private DeliveryDAO deliveryDAO;

    @Mock
    private ShipmentController shipmentController;

    @InjectMocks
    private DeliveryController deliveryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        deliveryController = new DeliveryController(deliveryDAO, shipmentController);
    }

    @Test
    public void testApproveDelivery_Success() {
        when(deliveryDAO.approveShipment(1, 2)).thenReturn(true);
        when(shipmentController.approveShipment(1)).thenReturn(true);

        boolean result = deliveryController.approveDelivery(1, 2);
        assertTrue(result);
    }

    @Test
    public void testDisapproveDelivery_Success() {
        when(deliveryDAO.disapproveShipment(1)).thenReturn(true);
        when(shipmentController.disapproveShipment(1)).thenReturn(true);

        boolean result = deliveryController.disapproveDelivery(1);
        assertTrue(result);
    }

    @Test
    public void testAddDelivery_Success() {
        Shipment shipment = mock(Shipment.class);
        Delivery delivery = mock(Delivery.class);

        when(deliveryDAO.createDelivery(1, shipment, delivery)).thenReturn(true);

        boolean result = deliveryController.addDelivery(1, shipment, delivery);
        assertTrue(result);
    }

    @Test
    public void testUpdateDelivery_Success() {
        Shipment shipment = mock(Shipment.class);
        Delivery delivery = mock(Delivery.class);

        when(deliveryDAO.updateDelivery(shipment, delivery)).thenReturn(true);

        boolean result = deliveryController.updateDelivery(shipment, delivery);
        assertTrue(result);
    }

    @Test
    public void testDeleteDelivery_Success() {
        when(deliveryDAO.deleteDelivery(1)).thenReturn(true);

        boolean result = deliveryController.deleteDelivery(1);
        assertTrue(result);
    }

    @Test
    public void testGetDeliveryByShipmentID() {
        Delivery expected = mock(Delivery.class);
        when(deliveryDAO.getDeliverybyShipmentID(15)).thenReturn(expected);

        Delivery result = deliveryController.getDeliveryByShipmentID(15);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAllDeliveries() {
        List<Delivery> mockList = Arrays.asList(mock(Delivery.class), mock(Delivery.class));
        when(deliveryDAO.getAllDeliveries()).thenReturn(mockList);

        List<Delivery> result = deliveryController.getAllDeliveries();
        assertEquals(2, result.size());
    }

    @Test
    public void testSetDeliveryEstimation_Success() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(2);
        when(deliveryDAO.setDeliveryEstimation(20, dateTime)).thenReturn(true);

        boolean result = deliveryController.setDeliveryEstimation(20, dateTime);
        assertTrue(result);
    }

    @Test
    public void testUpdateDeliveryOperations_Success() {
        when(deliveryDAO.updateDeliveryOperations(1, "In Progress", 5, 0)).thenReturn(true);

        boolean result = deliveryController.updateDeliveryOperations(1, "In Progress", 5, 0);
        assertTrue(result);
    }

    @Test
    public void testGetAssignedShipments() {
        Object[][] expected = new Object[][] {{"Item 1"}, {"Item 2"}};
        when(deliveryDAO.getAssignedShipments(100)).thenReturn(expected);

        Object[][] result = deliveryController.getAssignedShipments(100);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testUpdateDeliveryStatus() {
        LocalDateTime actualTime = LocalDateTime.now();
        when(deliveryDAO.updateDeliveryStatus(1, "Delivered", actualTime)).thenReturn(true);

        boolean result = deliveryController.updateDeliveryStatus(1, "Delivered", actualTime);
        assertTrue(result);
    }

    @Test
    public void testGetDriverHistory() {
        Object[][] expected = new Object[][] {{"Delivery 1"}, {"Delivery 2"}};
        when(deliveryDAO.getDriverDeliveryHistory(42)).thenReturn(expected);

        Object[][] result = deliveryController.getDriverDeliveryHistory(42);
        assertArrayEquals(expected, result);
    }
}
