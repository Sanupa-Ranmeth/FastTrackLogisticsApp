package controllers;

import models.NotificationDAO;
import models.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationControllerTest {

    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationController = new NotificationController(notificationDAO);
    }

    @Test
    void testGenerateUpdateDeliveryNotification() {
        boolean result = notificationController.generateUpdateDeliveryNotification(1, 20, "In Transit", "Colombo", 2);
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(20));
    }

    @Test
    void testApproveDeliveryNotification() {
        boolean result = notificationController.generateApproveDeliveryNotification(2, 20);
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(20));
    }

    @Test
    void testDisapproveDeliveryNotification() {
        boolean result = notificationController.generateDisapproveDeliveryNotification(2, 20);
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(20));
    }

    @Test
    void testGenerateDeliveryEstimationNotification() {
        boolean result = notificationController.generateDeliveryEstimationNotification(4, 32, "2025-06-30");
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(32));
    }

    @Test
    void testGetUserNotifications() {
        List<Notification> expected = Arrays.asList(
                new Notification(1, 10, "Delivery", "Message 1", null, false),
                new Notification(2, 10, "Delayed", "Message 2", null, false)
        );
        when(notificationDAO.getAllNotifications(10)).thenReturn(expected);

        List<Notification> notifications = notificationController.getUserNotifications(10);
        assertEquals(2, notifications.size());
    }

    @Test
    void testMarkNotificationAsRead() {
        when(notificationDAO.markAsRead(50)).thenReturn(true);
        assertTrue(notificationController.markNotificationAsRead(50));
    }

    @Test
    void testDeleteNotification() {
        when(notificationDAO.deleteNotification(60)).thenReturn(true);
        assertTrue(notificationController.deleteNotification(60));
    }

    @Test
    void testGenerateDriverAssignmentNotification_Normal() {
        boolean result = notificationController.generateDriverAssignmentNotification(1, 20, "Normal");
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(20));
    }

    @Test
    void testGenerateDriverAssignmentNotification_Urgent() {
        boolean result = notificationController.generateDriverAssignmentNotification(1, 20, "Urgent");
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(20));
    }

    @Test
    void testGenerateRouteChangeNotification() {
        boolean result = notificationController.generateRouteChangeNotification(1, 20);
        assertTrue(result);
        verify(notificationDAO).saveNotification(any(Notification.class), eq(1));
    }
}
