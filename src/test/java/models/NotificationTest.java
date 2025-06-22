package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 6, 22, 7, 30);
    }

    @Test
    void testFullConstructor() {
        Notification notification = new Notification(1, 10, "Delivery Update", "Your package has been dispatched", now, false);

        assertEquals(1, notification.getNotificationID());
        assertEquals(10, notification.getUserID());
        assertEquals("Delivery Update", notification.getType());
        assertEquals("Your package has been dispatched", notification.getMessage());
        assertEquals(now, notification.getTimestamp());
        assertFalse(notification.isRead());
    }

    @Test
    void testPartialConstructor() {
        Notification notification = new Notification(5, "Delayed", "Your package has been delayed");

        assertEquals(5, notification.getUserID());
        assertEquals("Delayed", notification.getType());
        assertEquals("Your package has been delayed", notification.getMessage());

        assertEquals(0, notification.getNotificationID());
        assertNull(notification.getTimestamp());
        assertFalse(notification.isRead());
    }

    @Test
    void testGettersAndSetters() {
        Notification notification = new Notification(5, "Delayed", "Your package has been delayed");

        notification.setNotificationID(10);
        assertEquals(10, notification.getNotificationID());

        notification.setUserID(20);
        assertEquals(20, notification.getUserID());

        notification.setType("Delivery Update");
        assertEquals("Delivery Update", notification.getType());

        notification.setMessage("Your package has been delayed by 20 minutes");
        assertEquals("Your package has been delayed by 20 minutes", notification.getMessage());

        notification.setTimeStamp(now);
        assertEquals(now, notification.getTimestamp());

        notification.setRead(true);
        assertTrue(notification.isRead());
    }
}
