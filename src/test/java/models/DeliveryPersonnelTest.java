package models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryPersonnelTest {

    private static DeliveryPersonnel fullConstructorDP;
    private DeliveryPersonnel partialConstructorDP;

    @BeforeAll
    static void initAll() {
        fullConstructorDP = new DeliveryPersonnel(1, "driver2", "password", "driver2@gmail.com", "Anuththara", "3 AM - 7 PM Weekends", 1, true);
    }

    @BeforeEach
    void init() {
        partialConstructorDP = new DeliveryPersonnel("driver1", "password123", "driver1@gmail.com", "Matheesha", "7 AM - 7 PM Weekends", 1, true);
    }

    @Test
    void partialConstructorGetterTest() {
        assertEquals("Matheesha", partialConstructorDP.getDriverName());
        assertEquals("7 AM - 7 PM Weekends", partialConstructorDP.getSchedule());
        assertEquals(1, partialConstructorDP.getRouteID());
        assertTrue(partialConstructorDP.isAvailable());

        //Fields inherited from User
        assertEquals("driver1", partialConstructorDP.getUsername());
        assertEquals("password123", partialConstructorDP.getPassword());
        assertEquals("driver1@gmail.com", partialConstructorDP.getEmail());
        assertEquals("driver", partialConstructorDP.getRole());
    }

    @Test
    void fullConstructorGetterTest() {
        assertEquals(1, fullConstructorDP.getDriverID());
        assertEquals("Anuththara", fullConstructorDP.getDriverName());
        assertEquals("3 AM - 7 PM Weekends", fullConstructorDP.getSchedule());
        assertEquals(1, fullConstructorDP.getRouteID());
        assertTrue(fullConstructorDP.isAvailable());

        //Inherited fields
        assertEquals("driver2", fullConstructorDP.getUsername());
        assertEquals("password", fullConstructorDP.getPassword());
        assertEquals("driver2@gmail.com", fullConstructorDP.getEmail());
        assertEquals("driver", fullConstructorDP.getRole());
    }

    @Test
    void setterTest() {
        partialConstructorDP.setDriverID(5);
        assertEquals(5, partialConstructorDP.getDriverID());

        partialConstructorDP.setDriverName("Anuththara Fernando");
        assertEquals("Anuththara Fernando", partialConstructorDP.getDriverName());

        partialConstructorDP.setSchedule("9 AM - 9 PM Weekdays");
        assertEquals("9 AM - 9 PM Weekdays", partialConstructorDP.getSchedule());

        partialConstructorDP.setRouteID(2);
        assertEquals(2, partialConstructorDP.getRouteID());

        partialConstructorDP.setAvailable(false);
        assertFalse(partialConstructorDP.isAvailable());
    }
}
