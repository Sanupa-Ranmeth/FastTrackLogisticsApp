package controllers;

import models.DeliveryPersonnel;
import models.DeliveryPersonnelDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeliveryPersonnelControllerTest {

    private DeliveryPersonnelController controller;

    @Mock
    private DeliveryPersonnelDAO driverDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new DeliveryPersonnelController(driverDAO);
    }

    @Test
    void testAddDriver_Valid() {
        when(driverDAO.addDeliveryPersonnel(any())).thenReturn(true);

        boolean result = controller.addDriver("driver1", "password", "driver1@gmail.com", "Anuththara", "1 AM - 1 PM Weekdays", 1, true);
        assertTrue(result);
        verify(driverDAO).addDeliveryPersonnel(any(DeliveryPersonnel.class));
    }

    @Test
    void testAddDriver_Invalid() {
        boolean result = controller.addDriver("", "pass", "email", "driver", "1 AM - 1 PM Weekdays", 0, false);
        assertFalse(result);
        verify(driverDAO, never()).addDeliveryPersonnel(any());
    }

    @Test
    void testUpdateDriver_Valid() throws SQLException {
        DeliveryPersonnel existing = new DeliveryPersonnel(1, "user", "password", "email@gmail.com", "Anuththara", "1 AM - 1 PM Weekdays", 1, true);
        when(driverDAO.getDriverByID(1)).thenReturn(existing);
        when(driverDAO.updateDeliveryPersonnel(any())).thenReturn(true);

        boolean result = controller.updateDrivers(1, "New name", "3 AM - 3 PM Weekends", 2, true);
        assertTrue(result);
        verify(driverDAO).updateDeliveryPersonnel(any(DeliveryPersonnel.class));
    }

    @Test
    void testUpdateDriver_NonExistent() throws SQLException {
        when(driverDAO.getDriverByID(999)).thenReturn(null);

        boolean result = controller.updateDrivers(999, "New name", "3 AM - 3 PM Weekends", 2, true);
        assertFalse(result);
    }

    @Test
    void testDeleteDriver_Valid() {
        when(driverDAO.deleteDeliveryPersonnel(1)).thenReturn(true);
        when(driverDAO.deleteUser(1)).thenReturn(true);

        boolean result = controller.deleteDriver(1);
        assertTrue(result);
    }

    @Test
    void testDeleteDriver_Invalid() {
        boolean result = controller.deleteDriver(0);
        assertFalse(result);
        verify(driverDAO, never()).deleteDeliveryPersonnel(anyInt());
    }

    @Test
    void testGetAllDriverNames() {
        List<String> names = Arrays.asList("Anuththara", "Matheesha");
        when(driverDAO.getALLDriverNames()).thenReturn(names);

        List<String> result = controller.getAllDriverNames();
        assertEquals(2, result.size());
    }

    @Test
    void testGetUserIDByUsername() {
        when(driverDAO.getUserIDbyUsername("Anuththara")).thenReturn(32);
        assertEquals(32, controller.getUserIDbyUsername("Anuththara"));
    }
}
