package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryTest {

    private Delivery fullDelivery;
    private LocalDateTime estimatedDeliveryDateTime;
    private LocalDateTime actualDeliveryDateTime;

    @BeforeEach
    public void setUp() {
        estimatedDeliveryDateTime = LocalDateTime.of(2025, 6, 23, 10, 0);
        actualDeliveryDateTime = LocalDateTime.of(2025, 6, 23, 11, 30);

        fullDelivery = new Delivery(1, 2, 3, 4, true, 90, estimatedDeliveryDateTime, actualDeliveryDateTime);
    }

    @Test
    void testFullConstructor() {
        assertEquals(1, fullDelivery.getShipmentID());
        assertEquals(2, fullDelivery.getDriverID());
        assertEquals(3, fullDelivery.getLocation());
        assertEquals(4, fullDelivery.getRating());
        assertTrue(fullDelivery.isDelayed());
        assertEquals(90, fullDelivery.getDelay());
        assertEquals(estimatedDeliveryDateTime, fullDelivery.getEstimatedDeliveryDate());
        assertEquals(actualDeliveryDateTime, fullDelivery.getActualDeliveryDateTime());
    }

    @Test
    void testUpdateConstructor() {
        LocalDateTime estDate = LocalDateTime.of(2025, 6, 23, 10, 0);
        Delivery delivery = new Delivery(2, 3, 4, false, 0,estDate);

        assertEquals(2, delivery.getShipmentID());
        assertEquals(3, delivery.getDriverID());
        assertEquals(4, delivery.getLocation());
        assertFalse(delivery.isDelayed());
        assertEquals(estDate, delivery.getEstimatedDeliveryDate());
        assertNull(delivery.getRating());
        assertNull(delivery.getActualDeliveryDateTime());
    }

    @Test
    void testAddConstructor() {
        Delivery delivery = new Delivery(5);
        assertEquals(5, delivery.getDriverID());
        assertEquals(0, delivery.getShipmentID());
        assertNull(delivery.getLocation());
        assertNull(delivery.getRating());
    }

    @Test
    void testGettersAndSetters() {
        Delivery delivery = new Delivery(1);
        LocalDateTime newEst = LocalDateTime.now().plusDays(2);
        LocalDateTime newActual = LocalDateTime.now().plusDays(3);

        delivery.setShipmentID(123);
        assertEquals(123, delivery.getShipmentID());

        delivery.setLocation(8);
        assertEquals(8, delivery.getLocation());

        delivery.setRating(5);
        assertEquals(5, delivery.getRating());

        delivery.setDelayed(true);
        assertTrue(delivery.isDelayed());

        delivery.setDelay(30);
        assertEquals(30, delivery.getDelay());

        delivery.setEstimatedDeliveryDate(newEst);
        assertEquals(newEst, delivery.getEstimatedDeliveryDate());

        delivery.setActualDeliveryDateTime(newActual);
        assertEquals(newActual, delivery.getActualDeliveryDateTime());
    }
}
